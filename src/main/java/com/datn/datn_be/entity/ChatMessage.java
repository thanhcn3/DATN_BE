package com.datn.datn_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chat_messages", schema = "store")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "sender_role", nullable = false, length = 20)
    private String senderRole;

    @ColumnDefault("'USER'")
    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;

    @Column(name = "sender_name", nullable = false, length = 150)
    private String senderName;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @ColumnDefault("false")
    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
