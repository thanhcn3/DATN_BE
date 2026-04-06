package com.datn.datn_be.service;

import com.datn.datn_be.dto.AdminNotificationResponse;
import com.datn.datn_be.entity.AppNotification;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.AppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private static final String RECIPIENT_ADMIN = "ADMIN";
    private static final String RECIPIENT_USER = "USER";
    private static final long MAX_NOTIFICATION_SIZE = 500;

    private final AppNotificationRepository appNotificationRepository;

    @Transactional
    public void notifyNewOrder(String orderId, String customerName, BigDecimal totalAmount, String status, String paymentMethod) {
        String safeCustomer = customerName != null && !customerName.isBlank() ? customerName : "Khách hàng";
        String safeStatus = status != null && !status.isBlank() ? status : "PENDING";
        String safePaymentMethod = paymentMethod != null && !paymentMethod.isBlank() ? paymentMethod : "COD";
        String vnStatus = toVietnameseOrderStatus(safeStatus);
        String vnPaymentMethod = toVietnamesePaymentMethod(safePaymentMethod);

        AppNotification notification = new AppNotification();
        notification.setType("NEW_ORDER");
        notification.setTitle("Đơn hàng mới");
        notification.setMessage(String.format("%s vừa tạo đơn #%s - %s (%s)",
                safeCustomer,
                orderId.substring(0, Math.min(8, orderId.length())).toUpperCase(),
                totalAmount != null ? totalAmount.toPlainString() + " VND" : "0 VND",
            vnPaymentMethod + "/" + vnStatus));
        notification.setOrderId(parseUuid(orderId));
        notification.setRecipientType(RECIPIENT_ADMIN);
        notification.setRecipientUserId(null);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        appNotificationRepository.save(notification);
        trimAdminNotifications();
    }

    @Transactional
    public void notifyUserOrderStatusChanged(String userId, String orderId, String status) {
        if (userId == null || userId.isBlank()) {
            return;
        }

        String safeStatus = status != null && !status.isBlank() ? status : "PENDING";
        String vnStatus = toVietnameseOrderStatus(safeStatus);
        AppNotification notification = new AppNotification();
        notification.setType("ORDER_STATUS_UPDATED");
        notification.setTitle("Đơn hàng cập nhật trạng thái");
        notification.setMessage(String.format("Đơn #%s đã chuyển sang trạng thái %s",
                orderId.substring(0, Math.min(8, orderId.length())).toUpperCase(),
                vnStatus));
        notification.setOrderId(parseUuid(orderId));
        notification.setRecipientType(RECIPIENT_USER);
        notification.setRecipientUserId(parseUuid(userId));
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        appNotificationRepository.save(notification);
        trimUserNotifications(parseUuid(userId));
    }

    @Transactional
    public void notifyChatMessageToAdmin(String roomId, String senderName, String content) {
        String safeSender = senderName != null && !senderName.isBlank() ? senderName : "Khách hàng";
        String safeContent = content != null && !content.isBlank() ? content.trim() : "(tin nhắn trống)";
        String roomSuffix = roomId != null && roomId.length() >= 6
            ? roomId.substring(roomId.length() - 6).toUpperCase()
            : "N/A";

        AppNotification notification = new AppNotification();
        notification.setType("CHAT_MESSAGE");
        notification.setTitle("Tin nhắn hỗ trợ mới");
        notification.setMessage(String.format(
            "[Phòng %s] %s vừa gửi tin nhắn: %s",
            roomSuffix,
                safeSender,
                abbreviate(safeContent, 140)
        ));
        notification.setOrderId(null);
        notification.setRecipientType(RECIPIENT_ADMIN);
        notification.setRecipientUserId(null);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        appNotificationRepository.save(notification);
        trimAdminNotifications();
    }

    @Transactional
    public void notifyChatMessageToUser(String userId, String senderName, String content) {
        if (userId == null || userId.isBlank()) {
            return;
        }

        String safeSender = senderName != null && !senderName.isBlank() ? senderName : "Quản trị viên";
        String safeContent = content != null && !content.isBlank() ? content.trim() : "(tin nhắn trống)";

        UUID userUUID = parseUuid(userId);
        AppNotification notification = new AppNotification();
        notification.setType("CHAT_MESSAGE");
        notification.setTitle("Tin nhắn mới từ quản trị viên");
        notification.setMessage(String.format(
                "%s: %s",
                safeSender,
                abbreviate(safeContent, 140)
        ));
        notification.setOrderId(null);
        notification.setRecipientType(RECIPIENT_USER);
        notification.setRecipientUserId(userUUID);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        appNotificationRepository.save(notification);
        trimUserNotifications(userUUID);
    }

    @Transactional
    public void notifyChatHandoffToAdmin(String roomId, String userName, String reason) {
        String safeUserName = userName != null && !userName.isBlank() ? userName : "Khách hàng";
        String safeReason = reason != null && !reason.isBlank() ? reason.trim() : "Muốn gặp nhân viên";
        String roomSuffix = roomId != null && roomId.length() >= 6
                ? roomId.substring(roomId.length() - 6).toUpperCase()
                : "N/A";

        AppNotification notification = new AppNotification();
        notification.setType("CHAT_HANDOFF");
        notification.setTitle("Yêu cầu hỗ trợ nhân viên");
        notification.setMessage(String.format(
                "[Phòng %s] %s yêu cầu gặp nhân viên. Lý do: %s",
                roomSuffix,
                safeUserName,
                abbreviate(safeReason, 140)
        ));
        notification.setOrderId(null);
        notification.setRecipientType(RECIPIENT_ADMIN);
        notification.setRecipientUserId(null);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        appNotificationRepository.save(notification);
        trimAdminNotifications();
    }

    public List<AdminNotificationResponse> getNotifications(int limit) {
        return appNotificationRepository.findByRecipientTypeAndIsReadFalseOrderByCreatedAtDesc(
                        RECIPIENT_ADMIN,
                        PageRequest.of(0, sanitizeLimit(limit), Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AdminNotificationResponse> getUserNotifications(String userId, int limit) {
        UUID userUUID = parseUuid(userId);
        return appNotificationRepository.findByRecipientTypeAndRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(
                        RECIPIENT_USER,
                        userUUID,
                        PageRequest.of(0, sanitizeLimit(limit), Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long getUnreadCount() {
        return appNotificationRepository.countByRecipientTypeAndIsReadFalse(RECIPIENT_ADMIN);
    }

    public long getUserUnreadCount(String userId) {
        return appNotificationRepository.countByRecipientTypeAndRecipientUserIdAndIsReadFalse(
                RECIPIENT_USER,
                parseUuid(userId)
        );
    }

    @Transactional
    public void markAsRead(String id) {
        AppNotification notification = appNotificationRepository.findByIdAndRecipientType(parseUuid(id), RECIPIENT_ADMIN)
                .orElseThrow(() -> new ClientSideException(404, "Notification not found"));
        notification.setRead(true);
        appNotificationRepository.save(notification);
    }

    @Transactional
    public void markUserAsRead(String userId, String id) {
        AppNotification notification = appNotificationRepository.findByIdAndRecipientTypeAndRecipientUserId(
                        parseUuid(id),
                        RECIPIENT_USER,
                        parseUuid(userId)
                )
                .orElseThrow(() -> new ClientSideException(404, "Notification not found"));
        notification.setRead(true);
        appNotificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        List<AppNotification> unread = appNotificationRepository.findByRecipientTypeAndIsReadFalse(RECIPIENT_ADMIN);
        unread.forEach(item -> item.setRead(true));
        appNotificationRepository.saveAll(unread);
    }

    @Transactional
    public void markAllUserAsRead(String userId) {
        List<AppNotification> unread = appNotificationRepository.findByRecipientTypeAndRecipientUserIdAndIsReadFalse(
                RECIPIENT_USER,
                parseUuid(userId)
        );
        unread.forEach(item -> item.setRead(true));
        appNotificationRepository.saveAll(unread);
    }

    private AdminNotificationResponse mapToResponse(AppNotification item) {
        return new AdminNotificationResponse(
                item.getId().toString(),
                item.getType(),
                item.getTitle(),
                item.getMessage(),
                item.getOrderId() != null ? item.getOrderId().toString() : null,
                item.getCreatedAt() != null ? item.getCreatedAt().toString() : null,
                item.isRead()
        );
    }

    private int sanitizeLimit(int limit) {
        return limit <= 0 ? 20 : Math.min(limit, 200);
    }

    private String toVietnameseOrderStatus(String status) {
        return switch (status) {
            case "PENDING" -> "Chờ xác nhận";
            case "CONFIRMED" -> "Đã xác nhận";
            case "SHIPPING" -> "Đang giao";
            case "DELIVERED" -> "Đã giao";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String toVietnamesePaymentMethod(String paymentMethod) {
        return switch (paymentMethod) {
            case "COD" -> "Thanh toán khi nhận hàng";
            case "VNPAY" -> "VNPay";
            default -> paymentMethod;
        };
    }

    private String abbreviate(String input, int maxLength) {
        if (input == null) {
            return "";
        }
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (Exception e) {
            throw new ClientSideException(400, "Invalid UUID");
        }
    }

    @Transactional
    private void trimAdminNotifications() {
        List<AppNotification> all = appNotificationRepository.findByRecipientTypeOrderByCreatedAtDesc(
                RECIPIENT_ADMIN,
                PageRequest.of(0, (int) (MAX_NOTIFICATION_SIZE + 200), Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        if (all.size() > MAX_NOTIFICATION_SIZE) {
            appNotificationRepository.deleteAll(all.subList((int) MAX_NOTIFICATION_SIZE, all.size()));
        }
    }

    @Transactional
    private void trimUserNotifications(UUID userId) {
        List<AppNotification> all = appNotificationRepository.findByRecipientTypeAndRecipientUserIdOrderByCreatedAtDesc(
                RECIPIENT_USER,
                userId,
                PageRequest.of(0, (int) (MAX_NOTIFICATION_SIZE + 200), Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        if (all.size() > MAX_NOTIFICATION_SIZE) {
            appNotificationRepository.deleteAll(all.subList((int) MAX_NOTIFICATION_SIZE, all.size()));
        }
    }
}
