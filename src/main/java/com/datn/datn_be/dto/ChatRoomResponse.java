package com.datn.datn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private String roomId;
    private String userId;
    private String userName;
    private String userFullName;
    private String adminId;
    private String adminName;
    private String status;
    private boolean handoffRequested;
    private Instant handoffAt;
    private String lastMessage;
    private Instant lastMessageAt;
    private int unreadUserCount;
    private int unreadAdminCount;
    private Instant createdAt;
    private Instant updatedAt;
}
