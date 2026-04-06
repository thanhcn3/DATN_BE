# Bao cao cuoi ngay - Nhan vien 2 (Backend)

## Da xong
- Hoan tat contract mo rong cho chat message:
  - Them `messageType` vao `ChatMessageResponse`.
  - Them truong `message_type` vao entity `chat_messages`.
- Hoan tat contract mo rong cho chat room:
  - Them `handoffRequested`, `handoffAt` vao `ChatRoomResponse`.
  - Them truong `handoff_requested`, `handoff_at` vao entity `chat_rooms`.
- Tao endpoint moi `POST /v1/chat/my-room/handoff`.
- Tao request DTO moi `ChatHandoffRequest` (`reason`).
- Cap nhat logic `ChatService`:
  - User gui tin nhan: luu message type `USER`.
  - Admin gui tin nhan: luu message type `ADMIN`.
  - Neu room chua handoff: chay FAQ matcher rule-based tieng Viet va tao BOT message (`messageType=BOT`) cung room khi match.
  - Handoff: set room `handoffRequested=true`, `handoffAt=now`, tao SYSTEM message (`messageType=SYSTEM`) vao timeline.
  - Giu tuong thich luong unread count (admin/user).
- Cap nhat `AdminNotificationService`:
  - Them thong bao rieng cho su kien handoff (`CHAT_HANDOFF`).
- Migration:
  - Tao file moi `MIGRATION_004_add_chatbot_handoff_fields.sql` de bo sung cot moi theo huong backward-compatible.
  - Dong bo `MIGRATION_002_add_chat_tables.sql` de moi moi truong tao moi cung co du cot.

## Dang lam
- Cho FE tich hop contract moi `messageType`, `handoffRequested`, `handoffAt` va endpoint handoff.
- Chuan bi bo testcase regression chat flow theo checklist.

## Vuong blocker
- Khong compile bang Maven wrapper duoc tren may hien tai vi bien moi truong `JAVA_HOME` dang sai:
  - `The JAVA_HOME environment variable is not defined correctly`.
- Da kiem tra nhanh qua IDE diagnostics, cac file vua sua khong bao loi cu phap/typing.

## Can ho tro tu lead
- Ho tro cau hinh lai `JAVA_HOME` tren may build/deploy de chay compile va test tu dong.
- Chot huong realtime Session B:
  - Hien codebase backend chua co ha tang WebSocket/STOMP de publish event message/handoff.
  - De xuat trong dot tiep theo: bo sung WebSocket config + auth + channel convention roi moi day event theo room/user/admin.
