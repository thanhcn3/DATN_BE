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
@Table(name = "chat_rooms", schema = "store")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "admin_id")
    private UUID adminId;

    @ColumnDefault("'OPEN'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "last_message", length = 1000)
    private String lastMessage;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @ColumnDefault("0")
    @Column(name = "unread_user_count", nullable = false)
    private Integer unreadUserCount;

    @ColumnDefault("0")
    @Column(name = "unread_admin_count", nullable = false)
    private Integer unreadAdminCount;

    @ColumnDefault("false")
    @Column(name = "handoff_requested", nullable = false)
    private boolean handoffRequested;

    @Column(name = "handoff_at")
    private Instant handoffAt;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;
}
