-- Migration: Cho phep moi user co nhieu phong chat lich su
-- Chi duoc ton tai toi da 1 phong OPEN cho moi user

DROP INDEX IF EXISTS store.uq_chat_rooms_user_id;

CREATE UNIQUE INDEX IF NOT EXISTS uq_chat_rooms_user_open
    ON store.chat_rooms (user_id)
    WHERE status = 'OPEN';
