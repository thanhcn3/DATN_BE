-- Migration: Thêm các cột mới cho bảng orders (VNPay integration)
-- Chạy script này nếu không dùng spring.jpa.hibernate.ddl-auto=update

ALTER TABLE store.orders ADD COLUMN IF NOT EXISTS shipping_name   VARCHAR(200);
ALTER TABLE store.orders ADD COLUMN IF NOT EXISTS shipping_phone  VARCHAR(20);
ALTER TABLE store.orders ADD COLUMN IF NOT EXISTS shipping_address VARCHAR(500);
ALTER TABLE store.orders ADD COLUMN IF NOT EXISTS payment_method  VARCHAR(20);
