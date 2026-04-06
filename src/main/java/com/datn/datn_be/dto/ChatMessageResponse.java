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
public class ChatMessageResponse {
    private String id;
    private String roomId;
    private String senderId;
    private String senderRole;
    private String messageType;
    private String senderName;
    private String content;
    private boolean isRead;
    private Instant createdAt;
}
