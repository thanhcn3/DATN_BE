-- Insert additional product data related to categories
-- This script adds sample data for products, including subcategories

-- First, let's ensure we have a proper category hierarchy
-- Assuming category 1 is the parent category

-- Insert subcategories for category 1 (if they don't exist)
INSERT INTO store.categories (id, name, parent_id) VALUES
(2, 'Laptops', 1),
(3, 'Desktops', 1),
(4, 'Tablets', 1),
(5, 'Gaming Laptops', 2),
(6, 'Business Laptops', 2),
(7, 'Workstations', 3),
(8, 'Budget Gaming PC', 3)
ON CONFLICT (id) DO NOTHING;

-- Insert products for category 1 (parent category)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Generic Tech Device 1', 'SKU-GEN-001', 'A generic technology product', 299.99, 1, 'TechBrand', 'ACTIVE', now()),
(UUID(), 'Generic Tech Device 2', 'SKU-GEN-002', 'Another generic technology product', 399.99, 1, 'TechBrand', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 2 (Laptops)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Standard Laptop Pro', 'SKU-LAP-001', 'High-performance laptop for everyday use', 799.99, 2, 'Dell', 'ACTIVE', now()),
(UUID(), 'Budget Laptop Basic', 'SKU-LAP-002', 'Affordable laptop for students', 449.99, 2, 'HP', 'ACTIVE', now()),
(UUID(), 'Ultrabook Elite', 'SKU-LAP-003', 'Ultra-thin and lightweight laptop', 1299.99, 2, 'Lenovo', 'ACTIVE', now()),
(UUID(), 'Laptop with TouchScreen', 'SKU-LAP-004', 'Laptop with 2-in-1 touchscreen capability', 899.99, 2, 'Asus', 'ACTIVE', now()),
(UUID(), 'Business Laptop Max', 'SKU-LAP-005', 'Enterprise-grade laptop with security features', 1199.99, 2, 'Lenovo', 'ACTIVE', now()),
(UUID(), 'Portable Work Laptop', 'SKU-LAP-006', 'Lightweight laptop perfect for remote work', 649.99, 2, 'HP', 'ACTIVE', now()),
(UUID(), 'Premium Laptop Designer', 'SKU-LAP-007', 'Premium laptop for creative professionals', 1599.99, 2, 'Apple', 'ACTIVE', now()),
(UUID(), 'Value Laptop Standard', 'SKU-LAP-008', 'Great value laptop for general purposes', 549.99, 2, 'Acer', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 3 (Desktops)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Office Desktop Computer', 'SKU-DES-001', 'Compact desktop for office use', 499.99, 3, 'Dell', 'ACTIVE', now()),
(UUID(), 'Gaming Desktop Beast', 'SKU-DES-002', 'High-end gaming desktop with RTX GPU', 1899.99, 3, 'Corsair', 'ACTIVE', now()),
(UUID(), 'Home Desktop Family', 'SKU-DES-003', 'All-in-one desktop for family use', 699.99, 3, 'HP', 'ACTIVE', now()),
(UUID(), 'Mini Desktop Compact', 'SKU-DES-004', 'Space-saving mini desktop PC', 399.99, 3, 'Intel', 'ACTIVE', now()),
(UUID(), 'Workstation Desktop Pro', 'SKU-DES-005', 'Professional workstation for design work', 2299.99, 3, 'Lenovo', 'ACTIVE', now()),
(UUID(), 'Budget Gaming Desktop', 'SKU-DES-006', 'Affordable gaming desktop for entry-level gamers', 899.99, 3, 'MSI', 'ACTIVE', now()),
(UUID(), 'Content Creator Desktop', 'SKU-DES-007', 'Powerful desktop for video editing and 3D rendering', 1699.99, 3, 'Apple', 'ACTIVE', now()),
(UUID(), 'Server Desktop Tower', 'SKU-DES-008', 'Tower desktop suitable for small server operations', 1299.99, 3, 'Supermicro', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 4 (Tablets)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Standard Tablet 10inch', 'SKU-TAB-001', '10-inch tablet for entertainment', 349.99, 4, 'Apple', 'ACTIVE', now()),
(UUID(), 'Budget Tablet 7inch', 'SKU-TAB-002', '7-inch budget tablet for reading', 199.99, 4, 'Samsung', 'ACTIVE', now()),
(UUID(), 'Premium Tablet OLED', 'SKU-TAB-003', 'Premium tablet with OLED display', 799.99, 4, 'Apple', 'ACTIVE', now()),
(UUID(), 'Business Tablet Stylus', 'SKU-TAB-004', 'Tablet with stylus for business professionals', 599.99, 4, 'Microsoft', 'ACTIVE', now()),
(UUID(), 'Drawing Tablet Artist', 'SKU-TAB-005', 'Specialized tablet for digital artists', 699.99, 4, 'Wacom', 'ACTIVE', now()),
(UUID(), 'Education Tablet Kids', 'SKU-TAB-006', 'Kid-friendly tablet with parental controls', 249.99, 4, 'Amazon', 'ACTIVE', now()),
(UUID(), 'Gaming Tablet Extreme', 'SKU-TAB-007', 'High-performance tablet for gaming', 549.99, 4, 'Samsung', 'ACTIVE', now()),
(UUID(), 'Reading Tablet eReader', 'SKU-TAB-008', 'E-reader tablet for book lovers', 199.99, 4, 'Amazon', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 5 (Gaming Laptops - subcategory of Laptops)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Gaming Laptop RTX3080', 'SKU-GLT-001', 'Gaming laptop with RTX 3080 graphics', 1599.99, 5, 'ASUS ROG', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop i9 Beast', 'SKU-GLT-002', 'High-end gaming laptop with Intel i9', 1899.99, 5, 'MSI', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop Budget Edition', 'SKU-GLT-003', 'Budget-friendly gaming laptop', 799.99, 5, 'Dell', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop Ultra Portable', 'SKU-GLT-004', 'Portable gaming laptop with RTX 4070', 1299.99, 5, 'Razer', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop 144Hz Display', 'SKU-GLT-005', 'Gaming laptop with 144Hz refresh rate display', 1199.99, 5, 'Lenovo Legion', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop Pro Max', 'SKU-GLT-006', 'Premium gaming laptop for professional gamers', 1999.99, 5, 'ASUS ROG', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop VR Ready', 'SKU-GLT-007', 'VR-ready gaming laptop with powerful GPU', 1699.99, 5, 'HP Omen', 'ACTIVE', now()),
(UUID(), 'Gaming Laptop Thermals Pro', 'SKU-GLT-008', 'Gaming laptop with advanced cooling system', 1399.99, 5, 'MSI GE', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 6 (Business Laptops - subcategory of Laptops)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Business Laptop Security Pro', 'SKU-BLT-001', 'Business laptop with enterprise security', 1099.99, 6, 'Lenovo ThinkPad', 'ACTIVE', now()),
(UUID(), 'Business Laptop Ultralight', 'SKU-BLT-002', 'Ultra-lightweight business laptop for travelers', 899.99, 6, 'Dell Latitude', 'ACTIVE', now()),
(UUID(), 'Business Laptop Workstation', 'SKU-BLT-003', 'Powerful workstation laptop for business', 1299.99, 6, 'HP ZBook', 'ACTIVE', now()),
(UUID(), 'Business Laptop Battery Life', 'SKU-BLT-004', 'Business laptop with extended battery life', 799.99, 6, 'Lenovo IdeaPad', 'ACTIVE', now()),
(UUID(), 'Business Laptop Executive', 'SKU-BLT-005', 'Premium business laptop for executives', 1499.99, 6, 'Apple MacBook Pro', 'ACTIVE', now()),
(UUID(), 'Business Laptop Value', 'SKU-BLT-006', 'Budget business laptop for startups', 599.99, 6, 'Asus Vivobook', 'ACTIVE', now()),
(UUID(), 'Business Laptop Rugged', 'SKU-BLT-007', 'Rugged business laptop for field work', 999.99, 6, 'Panasonic Toughbook', 'ACTIVE', now()),
(UUID(), 'Business Laptop 2-in-1', 'SKU-BLT-008', 'Convertible 2-in-1 business laptop', 949.99, 6, 'HP Pavilion', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 7 (Workstations - subcategory of Desktops)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Workstation CAD Design', 'SKU-WST-001', 'Professional CAD workstation', 2899.99, 7, 'Dell Precision', 'ACTIVE', now()),
(UUID(), 'Workstation Video Editing', 'SKU-WST-002', 'High-end video editing workstation', 3299.99, 7, 'HP Z-series', 'ACTIVE', now()),
(UUID(), 'Workstation 3D Rendering', 'SKU-WST-003', 'Powerful 3D rendering workstation', 3799.99, 7, 'Lenovo ThinkStation', 'ACTIVE', now()),
(UUID(), 'Workstation Multi-GPU', 'SKU-WST-004', 'Workstation with multiple GPU support', 4299.99, 7, 'NVIDIA DGX', 'ACTIVE', now()),
(UUID(), 'Workstation Entry Level', 'SKU-WST-005', 'Entry-level professional workstation', 1999.99, 7, 'Dell Optiplex', 'ACTIVE', now()),
(UUID(), 'Workstation AI Training', 'SKU-WST-006', 'AI and machine learning training workstation', 5299.99, 7, 'SuperMicro', 'ACTIVE', now()),
(UUID(), 'Workstation Scientific', 'SKU-WST-007', 'Scientific computing workstation', 2799.99, 7, 'Lenovo ThinkStation', 'ACTIVE', now()),
(UUID(), 'Workstation Animation Studio', 'SKU-WST-008', 'Animation and motion graphics workstation', 3699.99, 7, 'Apple Mac Pro', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert products for category 8 (Budget Gaming PC - subcategory of Desktops)
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Budget Gaming PC Entry', 'SKU-BGP-001', 'Entry-level budget gaming PC', 599.99, 8, 'Generic', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC GTX1660', 'SKU-BGP-002', 'Budget gaming PC with GTX 1660', 799.99, 8, 'Corsair', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC Streaming', 'SKU-BGP-003', 'Budget gaming PC suitable for streaming', 899.99, 8, 'NZXT', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC 1080p', 'SKU-BGP-004', 'Budget gaming PC for 1080p gaming', 699.99, 8, 'ABS', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC VR Entry', 'SKU-BGP-005', 'Budget gaming PC for entry-level VR', 999.99, 8, 'IBUYPOWER', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC Esports', 'SKU-BGP-006', 'Budget esports gaming PC', 799.99, 8, 'Skytech', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC Ultra', 'SKU-BGP-007', 'Budget gaming PC for Ultra settings', 1099.99, 8, 'ABS', 'ACTIVE', now()),
(UUID(), 'Budget Gaming PC Upgrade', 'SKU-BGP-008', 'Upgrade-friendly budget gaming PC', 899.99, 8, 'NZXT', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- Insert some sample products with images/descriptions for category 1 and its children
-- This ensures good pagination results
INSERT INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
(UUID(), 'Combo Electronics Bundle', 'SKU-COMBO-001', 'Bundle of electronics from multiple categories', 2499.99, 1, 'MultiTech', 'ACTIVE', now()),
(UUID(), 'Starter Tech Kit', 'SKU-STARTER-001', 'Starter kit for tech enthusiasts', 999.99, 1, 'TechKit', 'ACTIVE', now()),
(UUID(), 'Professional Tech Suite', 'SKU-SUITE-001', 'Complete professional technology suite', 4999.99, 1, 'ProTech', 'ACTIVE', now()),
(UUID(), 'Student Tech Package', 'SKU-STUDENT-001', 'Discounted tech package for students', 1499.99, 1, 'EduTech', 'ACTIVE', now()),
(UUID(), 'Home Office Setup', 'SKU-HOME-001', 'Complete home office technology setup', 1999.99, 1, 'OfficeMax', 'ACTIVE', now()),
(UUID(), 'Gaming Setup Package', 'SKU-GAMING-001', 'Complete gaming technology package', 3499.99, 1, 'GameTech', 'ACTIVE', now()),
(UUID(), 'Content Creator Kit', 'SKU-CREATOR-001', 'Technology kit for content creators', 2999.99, 1, 'CreativeTools', 'ACTIVE', now()),
(UUID(), 'Remote Work Bundle', 'SKU-REMOTE-001', 'Complete remote work technology bundle', 1799.99, 1, 'WorkFromHome', 'ACTIVE', now())
ON CONFLICT DO NOTHING;

-- If your database doesn't support ON CONFLICT, use this alternative syntax for MySQL:
-- Note: Uncomment the section below if using MySQL instead of PostgreSQL

/*
-- For MySQL, use INSERT IGNORE instead of ON CONFLICT
INSERT IGNORE INTO store.categories (id, name, parent_id) VALUES
(2, 'Laptops', 1),
(3, 'Desktops', 1),
(4, 'Tablets', 1),
(5, 'Gaming Laptops', 2),
(6, 'Business Laptops', 2),
(7, 'Workstations', 3),
(8, 'Budget Gaming PC', 3);

-- Products would be inserted with UUID() or similar function
INSERT IGNORE INTO store.products (id, name, sku, description, price, category_id, brand, status, created_at) VALUES
...
*/

