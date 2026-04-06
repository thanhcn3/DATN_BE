-- Migration: Add chatbot message type and handoff flags (backward-compatible)

ALTER TABLE IF EXISTS store.chat_messages
    ADD COLUMN IF NOT EXISTS message_type VARCHAR(20) NOT NULL DEFAULT 'USER';

ALTER TABLE IF EXISTS store.chat_rooms
    ADD COLUMN IF NOT EXISTS handoff_requested BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS handoff_at TIMESTAMP NULL;

-- Backfill message_type by sender_role for existing records
UPDATE store.chat_messages
SET message_type = CASE
    WHEN sender_role = 'ADMIN' THEN 'ADMIN'
    WHEN sender_role = 'USER' THEN 'USER'
    ELSE 'SYSTEM'
END
WHERE message_type IS NULL OR message_type = '';
