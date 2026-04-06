package com.datn.datn_be.controller;

import com.datn.datn_be.dto.AdminNotificationResponse;
import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminNotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<AdminNotificationResponse> notifications = adminNotificationService.getNotifications(limit);
            return ResponseEntity.ok(new ApiResponse<>(0, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        try {
            long count = adminNotificationService.getUnreadCount();
            return ResponseEntity.ok(new ApiResponse<>(0, "Unread count retrieved successfully", Map.of("unreadCount", count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable String id) {
        try {
            adminNotificationService.markAsRead(id);
            return ResponseEntity.ok(new ApiResponse<>(0, "Notification marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        try {
            adminNotificationService.markAllAsRead();
            return ResponseEntity.ok(new ApiResponse<>(0, "All notifications marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
