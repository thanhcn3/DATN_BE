package com.datn.datn_be.service;

import com.datn.datn_be.dto.CreateOrderRequest;
import com.datn.datn_be.dto.OrderItemResponse;
import com.datn.datn_be.dto.OrderResponse;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.entity.Order;
import com.datn.datn_be.entity.OrderItem;
import com.datn.datn_be.entity.Payment;
import com.datn.datn_be.entity.PaymentMethod;
import com.datn.datn_be.entity.ProductImage;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.CartItemRepository;
import com.datn.datn_be.repository.CartRepository;
import com.datn.datn_be.repository.OrderItemRepository;
import com.datn.datn_be.repository.OrderRepository;
import com.datn.datn_be.repository.PaymentMethodRepository;
import com.datn.datn_be.repository.PaymentRepository;
import com.datn.datn_be.repository.ProductImageRepository;
import com.datn.datn_be.repository.ProductRepository;
import com.datn.datn_be.util.JwtUtil;
import com.datn.datn_be.util.VnpayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final AdminNotificationService adminNotificationService;
    private final JwtUtil jwtUtil;
    private final VnpayUtil vnpayUtil;

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    private UUID getUserIdFromHeader(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) throw new ClientSideException(401, "Token không hợp lệ");
        return UUID.fromString(userId);
    }

    /** Tạo đơn hàng mới */
    @Transactional
    public OrderResponse createOrder(String authHeader, CreateOrderRequest request, String clientIp) {
        UUID userId = getUserIdFromHeader(authHeader);

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ClientSideException(400, "Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        // Ghép địa chỉ đầy đủ
        String fullAddress = String.join(", ",
                request.getAddress(),
                request.getWard() != null ? request.getWard() : "",
                request.getDistrict() != null ? request.getDistrict() : "",
                request.getProvince() != null ? request.getProvince() : ""
        ).replaceAll(",\\s*,", ",").replaceAll("^,\\s*|,\\s*$", "");

        // Tạo Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(Instant.now());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus("PENDING");
        order.setShippingName(request.getFullName());
        order.setShippingPhone(request.getPhone());
        order.setShippingAddress(fullAddress);
        order.setPaymentMethod(request.getPaymentMethod());
        order = orderRepository.save(order);

        // Tạo OrderItems
        for (var item : request.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setProductId(UUID.fromString(item.getProductId()));
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getPrice());
            orderItemRepository.save(oi);
        }

        // Xác định payment method id — tra cứu theo tên, tự tạo nếu chưa có
        String methodName = "VNPAY".equalsIgnoreCase(request.getPaymentMethod()) ? "VNPAY" : "COD";
        int methodId = paymentMethodRepository.findByNameIgnoreCase(methodName)
                .map(pm -> pm.getId())
                .orElseGet(() -> {
                    PaymentMethod newPm = new PaymentMethod();
                    newPm.setName(methodName);
                    return paymentMethodRepository.save(newPm).getId();
                });

        // Tạo Payment record
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setMethodId(methodId);
        payment.setAmount(request.getTotalAmount());
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        // Xóa giỏ hàng trong DB
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cartItemRepository.deleteByCartId(cart.getId());
        });

        // Map response
        OrderResponse resp = mapOrder(order, List.of(), null);

        // Nếu VNPAY → tạo URL thanh toán
        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            String txnRef = order.getId().toString().replace("-", "").substring(0, 20);
            String orderInfo = "Thanh toan don hang " + order.getId().toString().substring(0, 8);
            long amountVnd = request.getTotalAmount().longValue();
            String paymentUrl = vnpayUtil.buildPaymentUrl(txnRef, amountVnd, orderInfo, clientIp);
            resp.setPaymentUrl(paymentUrl);

            // Lưu txnRef vào Payment để đối chiếu sau
            payment.setStatus("WAITING");
            paymentRepository.save(payment);
        } else {
            // COD: xác nhận đơn ngay
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
            payment.setStatus("PENDING");
            paymentRepository.save(payment);
            resp.setStatus("CONFIRMED");
        }

        // Notify admins immediately when a new order is created from webapp.
        adminNotificationService.notifyNewOrder(
                order.getId().toString(),
                order.getShippingName(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod()
        );

        return resp;
    }

    /** Xác minh callback từ VNPay và cập nhật trạng thái */
    @Transactional
    public OrderResponse verifyVnpayReturn(Map<String, String> params) {
        if (!vnpayUtil.verifySignature(params)) {
            throw new ClientSideException(400, "Chữ ký VNPay không hợp lệ");
        }

        String responseCode = params.get("vnp_ResponseCode");
        // txnRef = 20 chars prefix of orderId (no dashes)
        String txnRef = params.get("vnp_TxnRef");

        // Tìm order theo txnRef (prefix của UUID đã remove dashes)
        List<Order> orders = orderRepository.findAll();
        Order order = orders.stream()
                .filter(o -> o.getId().toString().replace("-", "").substring(0, 20).equals(txnRef))
                .findFirst()
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy thông tin thanh toán"));

        boolean success = "00".equals(responseCode);
        if (success) {
            order.setStatus("CONFIRMED");
            payment.setStatus("PAID");
            payment.setPaidAt(Instant.now());
        } else {
            order.setStatus("CANCELLED");
            payment.setStatus("FAILED");
        }
        orderRepository.save(order);
        paymentRepository.save(payment);

        List<OrderItemResponse> items = getOrderItems(order.getId());
        return mapOrder(order, items, payment.getStatus());
    }

    /** Lấy danh sách đơn hàng của user */
    public List<OrderResponse> getMyOrders(String authHeader) {
        UUID userId = getUserIdFromHeader(authHeader);
        return orderRepository.findByUserId(userId).stream()
                .map(order -> {
                    List<OrderItemResponse> items = getOrderItems(order.getId());
                    Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
                    return mapOrder(order, items, payment != null ? payment.getStatus() : null);
                })
                .collect(Collectors.toList());
    }

    /** Lấy chi tiết 1 đơn hàng */
    public OrderResponse getOrderById(String authHeader, String orderId) {
        UUID userId = getUserIdFromHeader(authHeader);
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy đơn hàng"));
        if (!order.getUserId().equals(userId)) {
            throw new ClientSideException(403, "Không có quyền truy cập đơn hàng này");
        }
        List<OrderItemResponse> items = getOrderItems(order.getId());
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        return mapOrder(order, items, payment != null ? payment.getStatus() : null);
    }

    private List<OrderItemResponse> getOrderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId).stream().map(oi -> {
            OrderItemResponse r = new OrderItemResponse();
            r.setId(oi.getId());
            r.setProductId(oi.getProductId().toString());
            r.setQuantity(oi.getQuantity());
            r.setPrice(oi.getPrice());
            productRepository.findById(oi.getProductId()).ifPresent(p -> {
                r.setProductName(p.getName());
            });
            List<ProductImage> imgs = productImageRepository.findByProductId(oi.getProductId());
            if (!imgs.isEmpty()) r.setImageUrl(imgs.get(0).getImageUrl());
            return r;
        }).collect(Collectors.toList());
    }

    private OrderResponse mapOrder(Order order, List<OrderItemResponse> items, String paymentStatus) {
        OrderResponse r = new OrderResponse();
        r.setOrderId(order.getId().toString());
        r.setStatus(order.getStatus());
        r.setPaymentMethod(order.getPaymentMethod());
        r.setPaymentStatus(paymentStatus);
        r.setTotalAmount(order.getTotalAmount());
        r.setShippingName(order.getShippingName());
        r.setShippingPhone(order.getShippingPhone());
        r.setShippingAddress(order.getShippingAddress());
        r.setCreatedAt(order.getOrderDate() != null ? DISPLAY_FMT.format(order.getOrderDate()) : null);
        r.setItems(items);
        return r;
    }

    /** Admin: lấy tất cả đơn hàng (phân trang, lọc theo trạng thái) */
    public PaginationResponse<OrderResponse> getAllOrders(int page, int pageSize, String status) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 15;
        if (pageSize > 100) pageSize = 100;

        List<Order> allOrders = (status != null && !status.isEmpty())
                ? orderRepository.findByStatus(status)
                : orderRepository.findAll();

        allOrders.sort((a, b) -> {
            if (a.getOrderDate() == null) return 1;
            if (b.getOrderDate() == null) return -1;
            return b.getOrderDate().compareTo(a.getOrderDate());
        });

        int total = allOrders.size();
        int totalPages = total == 0 ? 1 : (total + pageSize - 1) / pageSize;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Order> pageContent = (start >= total) ? java.util.Collections.<Order>emptyList() : allOrders.subList(start, end);
        List<OrderResponse> content = pageContent.stream()
                .map(order -> {
                    List<OrderItemResponse> items = getOrderItems(order.getId());
                    Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
                    return mapOrder(order, items, payment != null ? payment.getStatus() : null);
                })
                .collect(Collectors.toList());

        PaginationResponse<OrderResponse> resp = new PaginationResponse<>();
        resp.setPage(page);
        resp.setPageSize(pageSize);
        resp.setTotalElements(total);
        resp.setTotalPages(totalPages);
        resp.setContent(content);
        resp.setHasNext(page < totalPages - 1);
        resp.setHasPrevious(page > 0);
        return resp;
    }

    /** Admin: xem chi tiết bất kỳ đơn hàng, không kiểm tra quyền sở hữu */
    public OrderResponse getOrderByIdAdmin(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy đơn hàng"));
        List<OrderItemResponse> items = getOrderItems(order.getId());
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        return mapOrder(order, items, payment != null ? payment.getStatus() : null);
    }

    /** Admin: cập nhật trạng thái bất kỳ đơn hàng */
    @Transactional
    public OrderResponse updateOrderStatusAdmin(String orderId, String status) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy đơn hàng"));
        if (status == null || status.isEmpty()) {
            throw new ClientSideException(400, "Trạng thái không hợp lệ");
        }
        order.setStatus(status);
        orderRepository.save(order);

        adminNotificationService.notifyUserOrderStatusChanged(
            order.getUserId() != null ? order.getUserId().toString() : null,
            order.getId().toString(),
            status
        );

        List<OrderItemResponse> items = getOrderItems(order.getId());
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        return mapOrder(order, items, payment != null ? payment.getStatus() : null);
    }

    /**
     * Tự động hủy đơn VNPAY đang chờ thanh toán quá 5 phút.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void autoCancelOverdueWaitingPayments() {
        Instant cutoff = Instant.now().minus(5, ChronoUnit.MINUTES);
        List<Payment> waitingPayments = paymentRepository.findByStatus("WAITING");

        for (Payment payment : waitingPayments) {
            Optional<Order> orderOpt = orderRepository.findById(payment.getOrderId());
            if (orderOpt.isEmpty()) continue;

            Order order = orderOpt.get();
            Instant orderDate = order.getOrderDate();
            if (orderDate == null) continue;

            boolean isOverdue = orderDate.isBefore(cutoff);
            boolean isWaitingOrder = "PENDING".equalsIgnoreCase(order.getStatus());

            if (!isOverdue || !isWaitingOrder) continue;

            order.setStatus("CANCELLED");
            payment.setStatus("FAILED");

            orderRepository.save(order);
            paymentRepository.save(payment);

            adminNotificationService.notifyUserOrderStatusChanged(
                    order.getUserId() != null ? order.getUserId().toString() : null,
                    order.getId().toString(),
                    "CANCELLED"
            );
        }
    }
}
