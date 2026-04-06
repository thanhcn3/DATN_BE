-- ============================================
-- SQL INSERT DATA FOR DATN_BE PROJECT
-- Database: db_invest (PostgreSQL)
-- Schema: store
-- ============================================

-- 1. INSERT ROLES DATA
INSERT INTO store.roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_SELLER');

-- 2. INSERT USERS DATA
INSERT INTO store.users (id, username, password_hash, email, full_name, phone, status, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'admin', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QIDM', 'admin@example.com', 'Admin User', '0123456789', 'ACTIVE', now(), now()),
('550e8400-e29b-41d4-a716-446655440001', 'john_doe', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QIDM', 'john@example.com', 'John Doe', '0987654321', 'ACTIVE', now(), now()),
('550e8400-e29b-41d4-a716-446655440002', 'jane_smith', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QIDM', 'jane@example.com', 'Jane Smith', '0912345678', 'ACTIVE', now(), now()),
('550e8400-e29b-41d4-a716-446655440003', 'bob_wilson', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QIDM', 'bob@example.com', 'Bob Wilson', '0934567890', 'ACTIVE', now(), now());

-- 3. INSERT USER ROLES DATA
INSERT INTO store.user_roles (user_id, role_id) VALUES
('550e8400-e29b-41d4-a716-446655440000', 1),
('550e8400-e29b-41d4-a716-446655440001', 2),
('550e8400-e29b-41d4-a716-446655440002', 2),
('550e8400-e29b-41d4-a716-446655440003', 2);

-- 4. INSERT CATEGORIES DATA
INSERT INTO store.categories (name, parent_id) VALUES
('Electronics', NULL),
('Clothing', NULL),
('Home & Garden', NULL),
('Books', NULL),
('Sports & Outdoors', NULL),
('Beauty & Personal Care', NULL),
('Food & Beverages', NULL),
('Toys & Games', NULL),
('Smartphones', 1),
('Laptops', 1),
('Tablets', 1),
('Headphones & Audio', 1),
('Cameras', 1),
('Smart Home', 1),
('Gaming Devices', 1),
('Men Clothing', 2),
('Women Clothing', 2),
('Kids Clothing', 2),
('Shoes', 2),
('Accessories', 2),
('Furniture', 3),
('Gardening Tools', 3),
('Kitchen Appliances', 3),
('Lighting', 3),
('Running', 5),
('Cycling', 5),
('Fitness Equipment', 5),
('Outdoor Gear', 5),
('Skincare', 6),
('Hair Care', 6),
('Makeup', 6),
('Fragrances', 6),
('Coffee & Tea', 7),
('Snacks', 7),
('Beverages', 7),
('Cookbooks', 4),
('Fiction', 4),
('Non-Fiction', 4),
('Self-Help', 4),
('Action Figures', 8),
('Board Games', 8),
('Video Games', 8),
('Puzzles', 8);

-- 5. INSERT PRODUCTS DATA (50+ Products)
-- SMARTPHONES (Category 9)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at, description_detail) VALUES
('650e8400-e29b-41d4-a716-446655440000', 'iPhone 15 Pro', 'SKU-IPHONE-15-PRO', 'Latest Apple iPhone with advanced features', 999.99, 9, 'Apple', 'ACTIVE', now(), '{"color": "Black", "storage": "256GB", "ram": "8GB"}'),
('650e8400-e29b-41d4-a716-446655440001', 'iPhone 15', 'SKU-IPHONE-15', 'Standard iPhone 15 model', 799.99, 9, 'Apple', 'ACTIVE', now(), '{"color": "White", "storage": "128GB", "ram": "6GB"}'),
('650e8400-e29b-41d4-a716-446655440002', 'Samsung Galaxy S24', 'SKU-SAMSUNG-S24', 'Flagship Samsung smartphone', 899.99, 9, 'Samsung', 'ACTIVE', now(), '{"color": "Silver", "storage": "256GB", "ram": "12GB"}'),
('650e8400-e29b-41d4-a716-446655440003', 'Samsung Galaxy A55', 'SKU-SAMSUNG-A55', 'Mid-range Samsung phone', 449.99, 9, 'Samsung', 'ACTIVE', now(), '{"color": "Blue", "storage": "128GB", "ram": "8GB"}'),
('650e8400-e29b-41d4-a716-446655440004', 'Google Pixel 9', 'SKU-GOOGLE-PIXEL-9', 'Google flagship with AI features', 799.99, 9, 'Google', 'ACTIVE', now(), '{"color": "Obsidian", "storage": "128GB", "ram": "12GB"}'),
('650e8400-e29b-41d4-a716-446655440005', 'OnePlus 12', 'SKU-ONEPLUS-12', 'Fast charging smartphone', 699.99, 9, 'OnePlus', 'ACTIVE', now(), '{"color": "Black", "storage": "256GB", "ram": "12GB"}'),
('650e8400-e29b-41d4-a716-446655440006', 'Xiaomi 14 Ultra', 'SKU-XIAOMI-14', 'Budget flagship phone', 599.99, 9, 'Xiaomi', 'ACTIVE', now(), '{"color": "Gray", "storage": "512GB", "ram": "16GB"}'),
('650e8400-e29b-41d4-a716-446655440007', 'Realme GT 6', 'SKU-REALME-GT6', 'Performance oriented phone', 549.99, 9, 'Realme', 'ACTIVE', now(), '{"color": "Yellow", "storage": "512GB", "ram": "12GB"}'),

-- LAPTOPS (Category 10)
('650e8400-e29b-41d4-a716-446655440008', 'MacBook Pro 16"', 'SKU-MACBOOK-PRO-16', 'High performance laptop', 2499.99, 10, 'Apple', 'ACTIVE', now(), '{"processor": "M3 Max", "storage": "1TB", "ram": "36GB"}'),
('650e8400-e29b-41d4-a716-446655440009', 'MacBook Pro 14"', 'SKU-MACBOOK-PRO-14', 'Powerful laptop for professionals', 1999.99, 10, 'Apple', 'ACTIVE', now(), '{"processor": "M3 Max", "storage": "512GB", "ram": "16GB"}'),
('650e8400-e29b-41d4-a716-446655440010', 'MacBook Air M3', 'SKU-MACBOOK-AIR-M3', 'Lightweight performance laptop', 1299.99, 10, 'Apple', 'ACTIVE', now(), '{"processor": "M3", "storage": "256GB", "ram": "8GB"}'),
('650e8400-e29b-41d4-a716-446655440011', 'Dell XPS 17', 'SKU-DELL-XPS-17', 'Premium large screen laptop', 2299.99, 10, 'Dell', 'ACTIVE', now(), '{"processor": "Intel i9", "storage": "1TB", "ram": "32GB"}'),
('650e8400-e29b-41d4-a716-446655440012', 'Dell XPS 15', 'SKU-DELL-XPS-15', 'Premium Windows laptop', 1599.99, 10, 'Dell', 'ACTIVE', now(), '{"processor": "Intel i7", "storage": "512GB", "ram": "16GB"}'),
('650e8400-e29b-41d4-a716-446655440013', 'HP Pavilion 15', 'SKU-HP-PAVILION-15', 'Everyday laptop', 749.99, 10, 'HP', 'ACTIVE', now(), '{"processor": "Intel i5", "storage": "256GB", "ram": "8GB"}'),
('650e8400-e29b-41d4-a716-446655440014', 'Lenovo ThinkPad X1', 'SKU-LENOVO-X1', 'Business laptop', 1299.99, 10, 'Lenovo', 'ACTIVE', now(), '{"processor": "Intel i7", "storage": "512GB", "ram": "16GB"}'),
('650e8400-e29b-41d4-a716-446655440015', 'ASUS ROG Gaming', 'SKU-ASUS-ROG', 'High performance gaming laptop', 1899.99, 10, 'ASUS', 'ACTIVE', now(), '{"processor": "Intel i9", "storage": "1TB", "ram": "32GB", "gpu": "RTX 4090"}'),

-- TABLETS (Category 11)
('650e8400-e29b-41d4-a716-446655440016', 'iPad Pro 12.9', 'SKU-IPAD-PRO-129', 'Powerful tablet', 1099.99, 11, 'Apple', 'ACTIVE', now(), '{"storage": "256GB", "ram": "8GB", "screen": "12.9 inch"}'),
('650e8400-e29b-41d4-a716-446655440017', 'iPad Air', 'SKU-IPAD-AIR', 'Mid-range iPad', 599.99, 11, 'Apple', 'ACTIVE', now(), '{"storage": "128GB", "ram": "6GB", "screen": "10.9 inch"}'),
('650e8400-e29b-41d4-a716-446655440018', 'Samsung Galaxy Tab S9', 'SKU-SAMSUNG-TAB-S9', 'Premium Android tablet', 799.99, 11, 'Samsung', 'ACTIVE', now(), '{"storage": "256GB", "ram": "8GB", "screen": "11 inch"}'),

-- HEADPHONES & AUDIO (Category 12)
('650e8400-e29b-41d4-a716-446655440019', 'AirPods Pro', 'SKU-AIRPODS-PRO', 'Premium wireless earbuds', 249.99, 12, 'Apple', 'ACTIVE', now(), '{"type": "In-ear", "battery": "30 hours", "noise_cancellation": true}'),
('650e8400-e29b-41d4-a716-446655440020', 'Sony WH-1000XM5', 'SKU-SONY-XM5', 'Best noise cancelling headphones', 399.99, 12, 'Sony', 'ACTIVE', now(), '{"type": "Over-ear", "battery": "30 hours", "noise_cancellation": true}'),
('650e8400-e29b-41d4-a716-446655440021', 'JBL Flip 6', 'SKU-JBL-FLIP6', 'Portable Bluetooth speaker', 129.99, 12, 'JBL', 'ACTIVE', now(), '{"type": "Speaker", "battery": "12 hours", "waterproof": true}'),

-- CAMERAS (Category 13)
('650e8400-e29b-41d4-a716-446655440022', 'Canon EOS R5', 'SKU-CANON-R5', 'Professional mirrorless camera', 3899.99, 13, 'Canon', 'ACTIVE', now(), '{"megapixels": "45MP", "sensor": "Full Frame", "4k_video": true}'),
('650e8400-e29b-41d4-a716-446655440023', 'Sony A7IV', 'SKU-SONY-A7IV', 'High quality mirrorless camera', 2498.99, 13, 'Sony', 'ACTIVE', now(), '{"megapixels": "61MP", "sensor": "Full Frame", "4k_video": true}'),

-- SMART HOME (Category 14)
('650e8400-e29b-41d4-a716-446655440024', 'Google Home Hub', 'SKU-GOOGLE-HOME-HUB', 'Smart display', 199.99, 14, 'Google', 'ACTIVE', now(), '{"screen": "10 inch", "resolution": "1280x800", "voice_assistant": "Google"}'),
('650e8400-e29b-41d4-a716-446655440025', 'Amazon Echo Show 8', 'SKU-ECHO-SHOW-8', 'Smart display with Alexa', 139.99, 14, 'Amazon', 'ACTIVE', now(), '{"screen": "8 inch", "resolution": "1280x720", "voice_assistant": "Alexa"}'),

-- GAMING DEVICES (Category 15)
('650e8400-e29b-41d4-a716-446655440026', 'PlayStation 5', 'SKU-PS5', 'Latest gaming console', 499.99, 15, 'Sony', 'ACTIVE', now(), '{"storage": "825GB", "resolution": "4K", "fps": "120fps"}'),
('650e8400-e29b-41d4-a716-446655440027', 'Xbox Series X', 'SKU-XBOX-X', 'Powerful gaming console', 499.99, 15, 'Microsoft', 'ACTIVE', now(), '{"storage": "1TB", "resolution": "4K", "fps": "120fps"}'),

-- CLOTHING - MEN (Category 16)
('650e8400-e29b-41d4-a716-446655440028', 'T-Shirt Cotton Blue', 'SKU-TSHIRT-001', 'Comfortable cotton t-shirt', 19.99, 16, 'Fashion Brand', 'ACTIVE', now(), '{"size": "M", "color": "Blue", "material": "100% Cotton"}'),
('650e8400-e29b-41d4-a716-446655440029', 'T-Shirt Cotton Black', 'SKU-TSHIRT-002', 'Classic black cotton shirt', 19.99, 16, 'Fashion Brand', 'ACTIVE', now(), '{"size": "L", "color": "Black", "material": "100% Cotton"}'),
('650e8400-e29b-41d4-a716-446655440030', 'Jeans Casual', 'SKU-JEANS-001', 'Classic casual jeans', 49.99, 16, 'Fashion Brand', 'ACTIVE', now(), '{"size": "32", "color": "Dark Blue", "material": "Denim"}'),
('650e8400-e29b-41d4-a716-446655440031', 'Polo Shirt', 'SKU-POLO-001', 'Business casual polo', 39.99, 16, 'Ralph Lauren', 'ACTIVE', now(), '{"size": "M", "color": "White", "material": "Cotton"}'),

-- CLOTHING - WOMEN (Category 17)
('650e8400-e29b-41d4-a716-446655440032', 'Summer Dress Red', 'SKU-DRESS-001', 'Light elegant summer dress', 59.99, 17, 'Fashion Brand', 'ACTIVE', now(), '{"size": "S", "color": "Red", "material": "Polyester"}'),
('650e8400-e29b-41d4-a716-446655440033', 'Summer Dress Blue', 'SKU-DRESS-002', 'Blue summer dress', 59.99, 17, 'Fashion Brand', 'ACTIVE', now(), '{"size": "M", "color": "Blue", "material": "Polyester"}'),
('650e8400-e29b-41d4-a716-446655440034', 'Blouse Casual', 'SKU-BLOUSE-001', 'Casual women blouse', 34.99, 17, 'Fashion Brand', 'ACTIVE', now(), '{"size": "S", "color": "White", "material": "Cotton"}'),

-- KIDS CLOTHING (Category 18)
('650e8400-e29b-41d4-a716-446655440035', 'Kids T-Shirt', 'SKU-KIDS-TSHIRT-001', 'Colorful kids t-shirt', 14.99, 18, 'Kids Brand', 'ACTIVE', now(), '{"size": "8Y", "color": "Multi", "material": "Cotton"}'),
('650e8400-e29b-41d4-a716-446655440036', 'Kids Shorts', 'SKU-KIDS-SHORTS-001', 'Summer kids shorts', 19.99, 18, 'Kids Brand', 'ACTIVE', now(), '{"size": "10Y", "color": "Blue", "material": "Cotton"}'),

-- SHOES (Category 19)
('650e8400-e29b-41d4-a716-446655440037', 'Nike Air Max', 'SKU-NIKE-AIRMAX', 'Popular Nike sneakers', 119.99, 19, 'Nike', 'ACTIVE', now(), '{"size": "10", "color": "White", "type": "Sneakers"}'),
('650e8400-e29b-41d4-a716-446655440038', 'Adidas Ultraboost', 'SKU-ADIDAS-UB', 'Comfortable running shoes', 179.99, 19, 'Adidas', 'ACTIVE', now(), '{"size": "9", "color": "Black", "type": "Running"}'),
('650e8400-e29b-41d4-a716-446655440039', 'Puma RS-X', 'SKU-PUMA-RSX', 'Retro style shoes', 89.99, 19, 'Puma', 'ACTIVE', now(), '{"size": "8", "color": "Red", "type": "Casual"}'),

-- ACCESSORIES (Category 20)
('650e8400-e29b-41d4-a716-446655440040', 'Watch Smart', 'SKU-WATCH-SMART', 'Smartwatch fitness tracker', 249.99, 20, 'Apple', 'ACTIVE', now(), '{"type": "Smartwatch", "waterproof": true, "battery": "18 hours"}'),
('650e8400-e29b-41d4-a716-446655440041', 'Leather Belt', 'SKU-BELT-001', 'Premium leather belt', 49.99, 20, 'Fashion Brand', 'ACTIVE', now(), '{"material": "Leather", "color": "Black", "size": "One Size"}'),

-- FURNITURE (Category 21)
('650e8400-e29b-41d4-a716-446655440042', 'Wooden Dining Table', 'SKU-TABLE-001', 'Solid wood table for 6', 349.99, 21, 'Furniture Co', 'ACTIVE', now(), '{"material": "Oak", "dimension": "180x90x75cm", "color": "Brown"}'),
('650e8400-e29b-41d4-a716-446655440043', 'Office Chair', 'SKU-CHAIR-001', 'Ergonomic office chair', 199.99, 21, 'Office Pro', 'ACTIVE', now(), '{"material": "Leather", "color": "Black", "adjustable": true}'),
('650e8400-e29b-41d4-a716-446655440044', 'Sofa Bed', 'SKU-SOFA-001', 'Convertible sofa bed', 599.99, 21, 'Furniture Co', 'ACTIVE', now(), '{"material": "Fabric", "size": "Queen", "color": "Gray"}'),

-- GARDENING TOOLS (Category 22)
('650e8400-e29b-41d4-a716-446655440045', 'Garden Tool Set', 'SKU-TOOLS-001', 'Complete gardening tool set', 39.99, 22, 'Garden Pro', 'ACTIVE', now(), '{"pieces": "15", "material": "Steel", "color": "Green"}'),
('650e8400-e29b-41d4-a716-446655440046', 'Hedge Trimmer', 'SKU-TRIMMER-001', 'Electric hedge trimmer', 79.99, 22, 'Garden Pro', 'ACTIVE', now(), '{"type": "Electric", "power": "600W", "length": "20 inch"}'),

-- BOOKS (Category 4, 36-39)
('650e8400-e29b-41d4-a716-446655440047', 'The Great Gatsby', 'SKU-BOOK-001', 'Classic novel by F. Scott Fitzgerald', 14.99, 36, 'Penguin Classics', 'ACTIVE', now(), '{"author": "F. Scott Fitzgerald", "pages": "180", "language": "English"}'),
('650e8400-e29b-41d4-a716-446655440048', 'To Kill a Mockingbird', 'SKU-BOOK-002', 'Classic American literature', 13.99, 36, 'Harper Perennial', 'ACTIVE', now(), '{"author": "Harper Lee", "pages": "310", "language": "English"}'),
('650e8400-e29b-41d4-a716-446655440049', 'Sapiens', 'SKU-BOOK-003', 'History of humankind', 21.99, 37, 'Harper', 'ACTIVE', now(), '{"author": "Yuval Noah Harari", "pages": "443", "language": "English"}'),
('650e8400-e29b-41d4-a716-446655440050', 'Atomic Habits', 'SKU-BOOK-004', 'Build better habits', 16.99, 39, 'Avery', 'ACTIVE', now(), '{"author": "James Clear", "pages": "320", "language": "English"}'),
('650e8400-e29b-41d4-a716-446655440051', 'The 7 Habits', 'SKU-BOOK-005', 'Personal effectiveness', 18.99, 39, 'Free Press', 'ACTIVE', now(), '{"author": "Stephen Covey", "pages": "372", "language": "English"}');

-- 6. INSERT PRODUCT IMAGES DATA (Multiple images for each product)
INSERT INTO store.product_images (id, product_id, image_url, alt_text, sort_order, created_at) VALUES
-- Smartphones
('750e8400-e29b-41d4-a716-446655440000', '650e8400-e29b-41d4-a716-446655440000', 'https://example.com/images/iphone-15-pro.jpg', 'iPhone 15 Pro Black', 1, now()),
('750e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440000', 'https://example.com/images/iphone-15-pro-side.jpg', 'iPhone 15 Pro Side View', 2, now()),
('750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001', 'https://example.com/images/iphone-15.jpg', 'iPhone 15 White', 1, now()),
('750e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440002', 'https://example.com/images/samsung-s24.jpg', 'Samsung Galaxy S24', 1, now()),
('750e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440003', 'https://example.com/images/samsung-a55.jpg', 'Samsung Galaxy A55', 1, now()),
('750e8400-e29b-41d4-a716-446655440005', '650e8400-e29b-41d4-a716-446655440004', 'https://example.com/images/pixel-9.jpg', 'Google Pixel 9', 1, now()),
('750e8400-e29b-41d4-a716-446655440006', '650e8400-e29b-41d4-a716-446655440005', 'https://example.com/images/oneplus-12.jpg', 'OnePlus 12', 1, now()),
('750e8400-e29b-41d4-a716-446655440007', '650e8400-e29b-41d4-a716-446655440006', 'https://example.com/images/xiaomi-14.jpg', 'Xiaomi 14 Ultra', 1, now()),
('750e8400-e29b-41d4-a716-446655440008', '650e8400-e29b-41d4-a716-446655440007', 'https://example.com/images/realme-gt6.jpg', 'Realme GT 6', 1, now()),
-- Laptops
('750e8400-e29b-41d4-a716-446655440009', '650e8400-e29b-41d4-a716-446655440008', 'https://example.com/images/macbook-pro-16.jpg', 'MacBook Pro 16 inch', 1, now()),
('750e8400-e29b-41d4-a716-446655440010', '650e8400-e29b-41d4-a716-446655440009', 'https://example.com/images/macbook-pro-14.jpg', 'MacBook Pro 14 inch', 1, now()),
('750e8400-e29b-41d4-a716-446655440011', '650e8400-e29b-41d4-a716-446655440010', 'https://example.com/images/macbook-air-m3.jpg', 'MacBook Air M3', 1, now()),
('750e8400-e29b-41d4-a716-446655440012', '650e8400-e29b-41d4-a716-446655440011', 'https://example.com/images/dell-xps-17.jpg', 'Dell XPS 17', 1, now()),
('750e8400-e29b-41d4-a716-446655440013', '650e8400-e29b-41d4-a716-446655440012', 'https://example.com/images/dell-xps-15.jpg', 'Dell XPS 15', 1, now()),
('750e8400-e29b-41d4-a716-446655440014', '650e8400-e29b-41d4-a716-446655440013', 'https://example.com/images/hp-pavilion-15.jpg', 'HP Pavilion 15', 1, now()),
('750e8400-e29b-41d4-a716-446655440015', '650e8400-e29b-41d4-a716-446655440014', 'https://example.com/images/lenovo-x1.jpg', 'Lenovo ThinkPad X1', 1, now()),
('750e8400-e29b-41d4-a716-446655440016', '650e8400-e29b-41d4-a716-446655440015', 'https://example.com/images/asus-rog.jpg', 'ASUS ROG Gaming', 1, now()),
-- Tablets
('750e8400-e29b-41d4-a716-446655440017', '650e8400-e29b-41d4-a716-446655440016', 'https://example.com/images/ipad-pro-129.jpg', 'iPad Pro 12.9', 1, now()),
('750e8400-e29b-41d4-a716-446655440018', '650e8400-e29b-41d4-a716-446655440017', 'https://example.com/images/ipad-air.jpg', 'iPad Air', 1, now()),
('750e8400-e29b-41d4-a716-446655440019', '650e8400-e29b-41d4-a716-446655440018', 'https://example.com/images/samsung-tab-s9.jpg', 'Samsung Galaxy Tab S9', 1, now()),
-- Audio
('750e8400-e29b-41d4-a716-446655440020', '650e8400-e29b-41d4-a716-446655440019', 'https://example.com/images/airpods-pro.jpg', 'AirPods Pro', 1, now()),
('750e8400-e29b-41d4-a716-446655440021', '650e8400-e29b-41d4-a716-446655440020', 'https://example.com/images/sony-xm5.jpg', 'Sony WH-1000XM5', 1, now()),
('750e8400-e29b-41d4-a716-446655440022', '650e8400-e29b-41d4-a716-446655440021', 'https://example.com/images/jbl-flip6.jpg', 'JBL Flip 6', 1, now()),
-- Cameras
('750e8400-e29b-41d4-a716-446655440023', '650e8400-e29b-41d4-a716-446655440022', 'https://example.com/images/canon-r5.jpg', 'Canon EOS R5', 1, now()),
('750e8400-e29b-41d4-a716-446655440024', '650e8400-e29b-41d4-a716-446655440023', 'https://example.com/images/sony-a7iv.jpg', 'Sony A7IV', 1, now()),
-- Smart Home
('750e8400-e29b-41d4-a716-446655440025', '650e8400-e29b-41d4-a716-446655440024', 'https://example.com/images/google-home-hub.jpg', 'Google Home Hub', 1, now()),
('750e8400-e29b-41d4-a716-446655440026', '650e8400-e29b-41d4-a716-446655440025', 'https://example.com/images/echo-show-8.jpg', 'Amazon Echo Show 8', 1, now()),
-- Gaming
('750e8400-e29b-41d4-a716-446655440027', '650e8400-e29b-41d4-a716-446655440026', 'https://example.com/images/ps5.jpg', 'PlayStation 5', 1, now()),
('750e8400-e29b-41d4-a716-446655440028', '650e8400-e29b-41d4-a716-446655440027', 'https://example.com/images/xbox-x.jpg', 'Xbox Series X', 1, now()),
-- Clothing
('750e8400-e29b-41d4-a716-446655440029', '650e8400-e29b-41d4-a716-446655440028', 'https://example.com/images/tshirt-blue.jpg', 'T-Shirt Blue', 1, now()),
('750e8400-e29b-41d4-a716-446655440030', '650e8400-e29b-41d4-a716-446655440029', 'https://example.com/images/tshirt-black.jpg', 'T-Shirt Black', 1, now()),
('750e8400-e29b-41d4-a716-446655440031', '650e8400-e29b-41d4-a716-446655440030', 'https://example.com/images/jeans.jpg', 'Jeans Casual', 1, now()),
('750e8400-e29b-41d4-a716-446655440032', '650e8400-e29b-41d4-a716-446655440031', 'https://example.com/images/polo-shirt.jpg', 'Polo Shirt', 1, now()),
('750e8400-e29b-41d4-a716-446655440033', '650e8400-e29b-41d4-a716-446655440032', 'https://example.com/images/dress-red.jpg', 'Summer Dress Red', 1, now()),
('750e8400-e29b-41d4-a716-446655440034', '650e8400-e29b-41d4-a716-446655440033', 'https://example.com/images/dress-blue.jpg', 'Summer Dress Blue', 1, now()),
('750e8400-e29b-41d4-a716-446655440035', '650e8400-e29b-41d4-a716-446655440034', 'https://example.com/images/blouse.jpg', 'Blouse Casual', 1, now()),
('750e8400-e29b-41d4-a716-446655440036', '650e8400-e29b-41d4-a716-446655440035', 'https://example.com/images/kids-tshirt.jpg', 'Kids T-Shirt', 1, now()),
('750e8400-e29b-41d4-a716-446655440037', '650e8400-e29b-41d4-a716-446655440036', 'https://example.com/images/kids-shorts.jpg', 'Kids Shorts', 1, now()),
-- Shoes
('750e8400-e29b-41d4-a716-446655440038', '650e8400-e29b-41d4-a716-446655440037', 'https://example.com/images/nike-airmax.jpg', 'Nike Air Max', 1, now()),
('750e8400-e29b-41d4-a716-446655440039', '650e8400-e29b-41d4-a716-446655440038', 'https://example.com/images/adidas-ub.jpg', 'Adidas Ultraboost', 1, now()),
('750e8400-e29b-41d4-a716-446655440040', '650e8400-e29b-41d4-a716-446655440039', 'https://example.com/images/puma-rsx.jpg', 'Puma RS-X', 1, now()),
-- Accessories
('750e8400-e29b-41d4-a716-446655440041', '650e8400-e29b-41d4-a716-446655440040', 'https://example.com/images/smartwatch.jpg', 'Watch Smart', 1, now()),
('750e8400-e29b-41d4-a716-446655440042', '650e8400-e29b-41d4-a716-446655440041', 'https://example.com/images/leather-belt.jpg', 'Leather Belt', 1, now()),
-- Furniture
('750e8400-e29b-41d4-a716-446655440043', '650e8400-e29b-41d4-a716-446655440042', 'https://example.com/images/dining-table.jpg', 'Dining Table', 1, now()),
('750e8400-e29b-41d4-a716-446655440044', '650e8400-e29b-41d4-a716-446655440043', 'https://example.com/images/office-chair.jpg', 'Office Chair', 1, now()),
('750e8400-e29b-41d4-a716-446655440045', '650e8400-e29b-41d4-a716-446655440044', 'https://example.com/images/sofa-bed.jpg', 'Sofa Bed', 1, now()),
-- Garden
('750e8400-e29b-41d4-a716-446655440046', '650e8400-e29b-41d4-a716-446655440045', 'https://example.com/images/garden-tools.jpg', 'Garden Tool Set', 1, now()),
('750e8400-e29b-41d4-a716-446655440047', '650e8400-e29b-41d4-a716-446655440046', 'https://example.com/images/hedge-trimmer.jpg', 'Hedge Trimmer', 1, now()),
-- Books
('750e8400-e29b-41d4-a716-446655440048', '650e8400-e29b-41d4-a716-446655440047', 'https://example.com/images/gatsby.jpg', 'The Great Gatsby', 1, now()),
('750e8400-e29b-41d4-a716-446655440049', '650e8400-e29b-41d4-a716-446655440048', 'https://example.com/images/mockingbird.jpg', 'To Kill a Mockingbird', 1, now()),
('750e8400-e29b-41d4-a716-446655440050', '650e8400-e29b-41d4-a716-446655440049', 'https://example.com/images/sapiens.jpg', 'Sapiens', 1, now()),
('750e8400-e29b-41d4-a716-446655440051', '650e8400-e29b-41d4-a716-446655440050', 'https://example.com/images/atomic-habits.jpg', 'Atomic Habits', 1, now()),
('750e8400-e29b-41d4-a716-446655440052', '650e8400-e29b-41d4-a716-446655440051', 'https://example.com/images/7-habits.jpg', 'The 7 Habits', 1, now());

-- 7. INSERT INVENTORY DATA (Stock for all products)
INSERT INTO store.inventory (product_id, quantity, updated_at) VALUES
-- Smartphones
('650e8400-e29b-41d4-a716-446655440000', 50, now()),
('650e8400-e29b-41d4-a716-446655440001', 45, now()),
('650e8400-e29b-41d4-a716-446655440002', 35, now()),
('650e8400-e29b-41d4-a716-446655440003', 60, now()),
('650e8400-e29b-41d4-a716-446655440004', 40, now()),
('650e8400-e29b-41d4-a716-446655440005', 30, now()),
('650e8400-e29b-41d4-a716-446655440006', 25, now()),
('650e8400-e29b-41d4-a716-446655440007', 20, now()),
-- Laptops
('650e8400-e29b-41d4-a716-446655440008', 15, now()),
('650e8400-e29b-41d4-a716-446655440009', 20, now()),
('650e8400-e29b-41d4-a716-446655440010', 25, now()),
('650e8400-e29b-41d4-a716-446655440011', 10, now()),
('650e8400-e29b-41d4-a716-446655440012', 18, now()),
('650e8400-e29b-41d4-a716-446655440013', 35, now()),
('650e8400-e29b-41d4-a716-446655440014', 22, now()),
('650e8400-e29b-41d4-a716-446655440015', 12, now()),
-- Tablets
('650e8400-e29b-41d4-a716-446655440016', 14, now()),
('650e8400-e29b-41d4-a716-446655440017', 28, now()),
('650e8400-e29b-41d4-a716-446655440018', 16, now()),
-- Audio
('650e8400-e29b-41d4-a716-446655440019', 75, now()),
('650e8400-e29b-41d4-a716-446655440020', 45, now()),
('650e8400-e29b-41d4-a716-446655440021', 65, now()),
-- Cameras
('650e8400-e29b-41d4-a716-446655440022', 8, now()),
('650e8400-e29b-41d4-a716-446655440023', 11, now()),
-- Smart Home
('650e8400-e29b-41d4-a716-446655440024', 32, now()),
('650e8400-e29b-41d4-a716-446655440025', 38, now()),
-- Gaming
('650e8400-e29b-41d4-a716-446655440026', 22, now()),
('650e8400-e29b-41d4-a716-446655440027', 19, now()),
-- Clothing Men
('650e8400-e29b-41d4-a716-446655440028', 100, now()),
('650e8400-e29b-41d4-a716-446655440029', 95, now()),
('650e8400-e29b-41d4-a716-446655440030', 80, now()),
('650e8400-e29b-41d4-a716-446655440031', 55, now()),
-- Clothing Women
('650e8400-e29b-41d4-a716-446655440032', 70, now()),
('650e8400-e29b-41d4-a716-446655440033', 68, now()),
('650e8400-e29b-41d4-a716-446655440034', 60, now()),
-- Kids Clothing
('650e8400-e29b-41d4-a716-446655440035', 85, now()),
('650e8400-e29b-41d4-a716-446655440036', 75, now()),
-- Shoes
('650e8400-e29b-41d4-a716-446655440037', 90, now()),
('650e8400-e29b-41d4-a716-446655440038', 50, now()),
('650e8400-e29b-41d4-a716-446655440039', 65, now()),
-- Accessories
('650e8400-e29b-41d4-a716-446655440040', 35, now()),
('650e8400-e29b-41d4-a716-446655440041', 110, now()),
-- Furniture
('650e8400-e29b-41d4-a716-446655440042', 8, now()),
('650e8400-e29b-41d4-a716-446655440043', 12, now()),
('650e8400-e29b-41d4-a716-446655440044', 6, now()),
-- Garden
('650e8400-e29b-41d4-a716-446655440045', 48, now()),
('650e8400-e29b-41d4-a716-446655440046', 24, now()),
-- Books
('650e8400-e29b-41d4-a716-446655440047', 150, now()),
('650e8400-e29b-41d4-a716-446655440048', 130, now()),
('650e8400-e29b-41d4-a716-446655440049', 95, now()),
('650e8400-e29b-41d4-a716-446655440050', 110, now()),
('650e8400-e29b-41d4-a716-446655440051', 100, now());

-- 8. INSERT CARTS DATA
INSERT INTO store.carts (id, user_id, status, created_at, updated_at) VALUES
('950e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', 'ACTIVE', now(), now()),
('950e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'ACTIVE', now(), now()),
('950e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', 'ACTIVE', now(), now());

-- 9. INSERT CART ITEMS DATA
INSERT INTO store.cart_items (id, cart_id, product_id, quantity, unit_price, created_at, updated_at) VALUES
('a50e8400-e29b-41d4-a716-446655440000', '950e8400-e29b-41d4-a716-446655440000', '650e8400-e29b-41d4-a716-446655440000', 1, 999.99, now(), now()),
('a50e8400-e29b-41d4-a716-446655440001', '950e8400-e29b-41d4-a716-446655440000', '650e8400-e29b-41d4-a716-446655440004', 2, 19.99, now(), now()),
('a50e8400-e29b-41d4-a716-446655440002', '950e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440002', 1, 1999.99, now(), now()),
('a50e8400-e29b-41d4-a716-446655440003', '950e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440006', 1, 59.99, now(), now());

-- 10. INSERT ORDERS DATA
INSERT INTO store.orders (id, user_id, order_date, total_amount, status) VALUES
('b50e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', now() - interval '7 days', 2039.97, 'DELIVERED'),
('b50e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', now() - interval '3 days', 1999.99, 'PROCESSING'),
('b50e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', now() - interval '1 day', 59.99, 'PENDING');

-- 11. INSERT ORDER ITEMS DATA
INSERT INTO store.order_items (id, order_id, product_id, quantity, unit_price, subtotal) VALUES
('c50e8400-e29b-41d4-a716-446655440000', 'b50e8400-e29b-41d4-a716-446655440000', '650e8400-e29b-41d4-a716-446655440000', 1, 999.99, 999.99),
('c50e8400-e29b-41d4-a716-446655440001', 'b50e8400-e29b-41d4-a716-446655440000', '650e8400-e29b-41d4-a716-446655440004', 2, 19.99, 39.98),
('c50e8400-e29b-41d4-a716-446655440002', 'b50e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440002', 1, 1999.99, 1999.99),
('c50e8400-e29b-41d4-a716-446655440003', 'b50e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440006', 1, 59.99, 59.99);

-- 12. INSERT PAYMENT METHODS DATA
INSERT INTO store.payment_methods (id, method_name, description, icon_url, is_active) VALUES
('d50e8400-e29b-41d4-a716-446655440000', 'Credit Card', 'Visa, MasterCard, American Express', 'https://example.com/images/credit-card.png', true),
('d50e8400-e29b-41d4-a716-446655440001', 'Debit Card', 'Bank Debit Cards', 'https://example.com/images/debit-card.png', true),
('d50e8400-e29b-41d4-a716-446655440002', 'Bank Transfer', 'Direct bank transfer', 'https://example.com/images/bank-transfer.png', true),
('d50e8400-e29b-41d4-a716-446655440003', 'E-Wallet', 'Digital wallets like PayPal', 'https://example.com/images/ewallet.png', true);

-- 13. INSERT PAYMENTS DATA
INSERT INTO store.payments (id, order_id, payment_method_id, amount, transaction_id, status, payment_date) VALUES
('e50e8400-e29b-41d4-a716-446655440000', 'b50e8400-e29b-41d4-a716-446655440000', 'd50e8400-e29b-41d4-a716-446655440000', 2039.97, 'TXN-001-2024', 'COMPLETED', now() - interval '7 days'),
('e50e8400-e29b-41d4-a716-446655440001', 'b50e8400-e29b-41d4-a716-446655440001', 'd50e8400-e29b-41d4-a716-446655440000', 1999.99, 'TXN-002-2024', 'PENDING', now() - interval '3 days'),
('e50e8400-e29b-41d4-a716-446655440002', 'b50e8400-e29b-41d4-a716-446655440002', 'd50e8400-e29b-41d4-a716-446655440002', 59.99, 'TXN-003-2024', 'PENDING', now() - interval '1 day');

-- 14. INSERT PROMOTIONS DATA
INSERT INTO store.promotions (id, name, description, discount_type, discount_value, start_date, end_date, is_active, created_at) VALUES
('f50e8400-e29b-41d4-a716-446655440000', 'New Year Sale', '20% off on all items', 'PERCENTAGE', 20.00, now(), now() + interval '30 days', true, now()),
('f50e8400-e29b-41d4-a716-446655440001', 'Electronics Week', '15% discount on electronics', 'PERCENTAGE', 15.00, now(), now() + interval '7 days', true, now()),
('f50e8400-e29b-41d4-a716-446655440002', 'Spring Bundle', 'Flat $50 off on orders above $500', 'FIXED', 50.00, now(), now() + interval '45 days', true, now());

-- 15. INSERT INVOICES DATA
INSERT INTO store.invoices (id, order_id, invoice_number, invoice_date, due_date, total_amount, status, created_at) VALUES
('g50e8400-e29b-41d4-a716-446655440000', 'b50e8400-e29b-41d4-a716-446655440000', 'INV-2024-001', now() - interval '7 days', now() + interval '30 days', 2039.97, 'PAID', now() - interval '7 days'),
('g50e8400-e29b-41d4-a716-446655440001', 'b50e8400-e29b-41d4-a716-446655440001', 'INV-2024-002', now() - interval '3 days', now() + interval '30 days', 1999.99, 'PENDING', now() - interval '3 days'),
('g50e8400-e29b-41d4-a716-446655440002', 'b50e8400-e29b-41d4-a716-446655440002', 'INV-2024-003', now() - interval '1 day', now() + interval '30 days', 59.99, 'PENDING', now() - interval '1 day');

-- ============================================
-- OPTIONAL: VERIFICATION QUERIES
-- ============================================

-- Verify users inserted
-- SELECT * FROM store.users;

-- Verify categories inserted
-- SELECT * FROM store.categories;

-- Verify products inserted
-- SELECT * FROM store.products;

-- Verify orders with order items
-- SELECT o.id, o.user_id, o.total_amount, o.status, COUNT(oi.id) as item_count
-- FROM store.orders o
-- LEFT JOIN store.order_items oi ON o.id = oi.order_id
-- GROUP BY o.id;

-- Verify cart with items
-- SELECT c.id, c.user_id, COUNT(ci.id) as item_count
-- FROM store.carts c
-- LEFT JOIN store.cart_items ci ON c.id = ci.cart_id
-- GROUP BY c.id;

-- Count total records
-- SELECT
--   (SELECT COUNT(*) FROM store.users) as user_count,
--   (SELECT COUNT(*) FROM store.categories) as category_count,
--   (SELECT COUNT(*) FROM store.products) as product_count,
--   (SELECT COUNT(*) FROM store.orders) as order_count,
--   (SELECT COUNT(*) FROM store.carts) as cart_count;


