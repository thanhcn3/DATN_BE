package com.datn.datn_be.controller;

import com.datn.datn_be.dto.AdminNotificationResponse;
import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.service.AdminNotificationService;
import com.datn.datn_be.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/notification")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final AdminNotificationService adminNotificationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AdminNotificationResponse>>> getMyNotifications(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            String userId = extractUserId(authHeader);
            List<AdminNotificationResponse> notifications = adminNotificationService.getUserNotifications(userId, limit);
            return ResponseEntity.ok(new ApiResponse<>(0, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/my/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMyUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = extractUserId(authHeader);
            long count = adminNotificationService.getUserUnreadCount(userId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Unread count retrieved successfully", Map.of("unreadCount", count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/my/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markMyNotificationAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        try {
            String userId = extractUserId(authHeader);
            adminNotificationService.markUserAsRead(userId, id);
            return ResponseEntity.ok(new ApiResponse<>(0, "Notification marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/my/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllMyNotificationsAsRead(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = extractUserId(authHeader);
            adminNotificationService.markAllUserAsRead(userId);
            return ResponseEntity.ok(new ApiResponse<>(0, "All notifications marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    private String extractUserId(String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null || userId.isBlank()) {
            throw new ClientSideException(401, "Token không hợp lệ");
        }
        return userId;
    }
}
