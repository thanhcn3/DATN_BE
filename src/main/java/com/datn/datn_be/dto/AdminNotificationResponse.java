package com.datn.datn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationResponse {
    private String id;
    private String type;
    private String title;
    private String message;
    private String orderId;
    private String createdAt;
    private boolean isRead;
}
