package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.CreateOrderRequest;
import com.datn.datn_be.dto.OrderResponse;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** Tạo đơn hàng mới (COD hoặc VNPAY) */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        try {
            String clientIp = getClientIp(httpRequest);
            OrderResponse resp = orderService.createOrder(authHeader, request, clientIp);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đặt hàng thành công", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Xác minh kết quả thanh toán VNPay (FE gọi sau khi VNPay redirect về) */
    @GetMapping("/vnpay-verify")
    public ResponseEntity<ApiResponse<OrderResponse>> verifyVnpay(
            @RequestParam Map<String, String> params) {
        try {
            OrderResponse resp = orderService.verifyVnpayReturn(params);
            return ResponseEntity.ok(new ApiResponse<>(0, "Xác minh thành công", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Lấy danh sách đơn hàng của user */
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @RequestHeader("Authorization") String authHeader) {
        try {
            List<OrderResponse> orders = orderService.getMyOrders(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy danh sách đơn hàng thành công", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Lấy chi tiết đơn hàng theo ID */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId) {
        try {
            OrderResponse resp = orderService.getOrderById(authHeader, orderId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy đơn hàng thành công", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        // Lấy IP đầu tiên nếu có nhiều
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        // VNPay chỉ chấp nhận IPv4 — chuyển IPv6 localhost về 127.0.0.1
        if (ip == null || ip.isEmpty() || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /** Admin: lấy tất cả đơn hàng (phân trang) */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PaginationResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String status) {
        try {
            PaginationResponse<OrderResponse> resp = orderService.getAllOrders(page, pageSize, status);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy danh sách đơn hàng thành công", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Admin: xem chi tiết bất kỳ đơn hàng */
    @GetMapping("/admin/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByIdAdmin(
            @PathVariable String orderId) {
        try {
            OrderResponse resp = orderService.getOrderByIdAdmin(orderId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy đơn hàng thành công", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Admin: cập nhật trạng thái đơn hàng */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String orderId,
            @RequestBody Map<String, String> body) {
        try {
            OrderResponse resp = orderService.updateOrderStatusAdmin(orderId, body.get("status"), authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Cập nhật trạng thái thành công", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
