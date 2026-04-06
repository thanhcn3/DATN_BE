-- Migration: Thêm bảng chat room và chat message
-- Chạy script này nếu không dùng spring.jpa.hibernate.ddl-auto=update

CREATE TABLE IF NOT EXISTS store.chat_rooms (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    admin_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    handoff_requested BOOLEAN NOT NULL DEFAULT FALSE,
    handoff_at TIMESTAMP,
    last_message VARCHAR(1000),
    last_message_at TIMESTAMP,
    unread_user_count INTEGER NOT NULL DEFAULT 0,
    unread_admin_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_chat_rooms_user_id
    ON store.chat_rooms (user_id);

CREATE INDEX IF NOT EXISTS idx_chat_rooms_admin_id
    ON store.chat_rooms (admin_id);

CREATE INDEX IF NOT EXISTS idx_chat_rooms_status_updated
    ON store.chat_rooms (status, updated_at DESC);

CREATE TABLE IF NOT EXISTS store.chat_messages (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES store.chat_rooms(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL,
    sender_role VARCHAR(20) NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    sender_name VARCHAR(150) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_room_created
    ON store.chat_messages (room_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_chat_messages_room_read
    ON store.chat_messages (room_id, is_read);
