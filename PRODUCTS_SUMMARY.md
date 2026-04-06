# 📊 SQL INSERT DATA - DATN_BE Project Summary

## 📈 Dữ Liệu Được Thêm Vào

### 1. **Danh Mục (Categories)** - 43 danh mục
   - **Danh mục chính (8):** Electronics, Clothing, Home & Garden, Books, Sports & Outdoors, Beauty & Personal Care, Food & Beverages, Toys & Games
   - **Danh mục con:** Các chi nhánh chi tiết của từng danh mục chính

### 2. **Sản Phẩm (Products)** - 51 sản phẩm
   
   #### 💻 Điện Tử (19 sản phẩm)
   - **Smartphones (8):** iPhone 15 Pro, iPhone 15, Samsung Galaxy S24, Samsung Galaxy A55, Google Pixel 9, OnePlus 12, Xiaomi 14 Ultra, Realme GT 6
   - **Laptops (8):** MacBook Pro 16", MacBook Pro 14", MacBook Air M3, Dell XPS 17, Dell XPS 15, HP Pavilion 15, Lenovo ThinkPad X1, ASUS ROG Gaming
   - **Tablets (3):** iPad Pro 12.9, iPad Air, Samsung Galaxy Tab S9
   
   #### 🎧 Âm Thanh & Thiết Bị (7 sản phẩm)
   - **Headphones/Audio (3):** AirPods Pro, Sony WH-1000XM5, JBL Flip 6
   - **Cameras (2):** Canon EOS R5, Sony A7IV
   - **Smart Home (2):** Google Home Hub, Amazon Echo Show 8
   
   #### 🎮 Trò Chơi & Giải Trí (2 sản phẩm)
   - **Gaming Devices:** PlayStation 5, Xbox Series X
   
   #### 👗 Quần Áo & Giày (11 sản phẩm)
   - **Men Clothing (4):** T-Shirt Blue, T-Shirt Black, Jeans, Polo Shirt
   - **Women Clothing (3):** Summer Dress Red, Summer Dress Blue, Blouse
   - **Kids Clothing (2):** Kids T-Shirt, Kids Shorts
   - **Shoes (3):** Nike Air Max, Adidas Ultraboost, Puma RS-X
   
   #### 🏠 Nội Thất & Phụ Kiện (7 sản phẩm)
   - **Furniture (3):** Wooden Dining Table, Office Chair, Sofa Bed
   - **Accessories (2):** Smart Watch, Leather Belt
   - **Gardening (2):** Garden Tool Set, Hedge Trimmer
   
   #### 📚 Sách & Xuất Bản (5 sản phẩm)
   - **Books:** The Great Gatsby, To Kill a Mockingbird, Sapiens, Atomic Habits, The 7 Habits

### 3. **Hình Ảnh Sản Phẩm (Product Images)** - 53 hình ảnh
   - Mỗi sản phẩm có 1-2 hình ảnh
   - URL mẫu: `https://example.com/images/[product-name].jpg`

### 4. **Kho (Inventory)** - 51 bản ghi
   - Số lượng tồn kho khác nhau cho mỗi sản phẩm
   - **Electronics:** 10-60 sản phẩm
   - **Clothing/Shoes:** 50-110 sản phẩm
   - **Books:** 95-150 sản phẩm

### 5. **Người Dùng (Users)** - 4 người dùng
   - **admin:** Người quản trị
   - **john_doe:** Người dùng thường
   - **jane_smith:** Người dùng thường
   - **bob_wilson:** Người dùng thường

### 6. **Vai Trò (Roles)** - 3 vai trò
   - ROLE_ADMIN
   - ROLE_USER
   - ROLE_SELLER

### 7. **Giỏ Hàng (Carts)** - 3 giỏ hàng
   - Mỗi user có 1 giỏ hàng ACTIVE

### 8. **Mục Giỏ Hàng (Cart Items)** - 4 mục
   - Ví dụ: Giỏ của john_doe có iPhone 15 Pro (1x) + T-Shirt (2x)

### 9. **Đơn Hàng (Orders)** - 3 đơn hàng
   - **Order 1:** 7 ngày trước, DELIVERED, $2039.97
   - **Order 2:** 3 ngày trước, PROCESSING, $1999.99
   - **Order 3:** 1 ngày trước, PENDING, $59.99

### 10. **Chi Tiết Đơn Hàng (Order Items)** - 4 chi tiết
   - Liên kết với các sản phẩm cụ thể

### 11. **Phương Thức Thanh Toán (Payment Methods)** - 4 phương thức
   - Credit Card
   - Debit Card
   - Bank Transfer
   - E-Wallet

### 12. **Thanh Toán (Payments)** - 3 lần thanh toán
   - Liên kết với đơn hàng

### 13. **Khuyến Mãi (Promotions)** - 3 khuyến mãi
   - New Year Sale: 20% off
   - Electronics Week: 15% off
   - Spring Bundle: $50 off

### 14. **Hóa Đơn (Invoices)** - 3 hóa đơn
   - Liên kết với đơn hàng

---

## 💾 Cách Sử Dụng File SQL

### Yêu Cầu
- PostgreSQL 12+
- Database: `db_invest`
- Schema: `store`
- User: `postgres` / Password: `12345`

### Các Bước Thực Hiện
1. Mở PostgreSQL GUI (pgAdmin) hoặc Command Line
2. Kết nối đến database `db_invest`
3. Chạy file `INSERT_DATA.sql` toàn bộ
4. Kiểm tra dữ liệu bằng các query xác nhận (ở cuối file)

### Query Xác Nhận
```sql
-- Xem số lượng sản phẩm
SELECT COUNT(*) FROM store.products;  -- Kết quả: 51

-- Xem số lượng danh mục
SELECT COUNT(*) FROM store.categories;  -- Kết quả: 43

-- Xem sản phẩm theo danh mục
SELECT c.name, COUNT(p.id) as product_count
FROM store.categories c
LEFT JOIN store.products p ON c.id = p.category_id
GROUP BY c.id, c.name
ORDER BY product_count DESC;

-- Xem các sản phẩm với giá cao nhất
SELECT name, price, brand FROM store.products
ORDER BY price DESC
LIMIT 10;

-- Xem tồn kho
SELECT p.name, i.quantity, p.price
FROM store.products p
LEFT JOIN store.inventory i ON p.id = i.product_id
ORDER BY i.quantity ASC;
```

---

## 📊 Thống Kê

| Mục | Số Lượng |
|-----|----------|
| Người Dùng | 4 |
| Vai Trò | 3 |
| Danh Mục | 43 |
| **Sản Phẩm** | **51** ✅ |
| Hình Ảnh Sản Phẩm | 53 |
| Kho Hàng | 51 |
| Giỏ Hàng | 3 |
| Mục Giỏ | 4 |
| Đơn Hàng | 3 |
| Chi Tiết Đơn | 4 |
| Phương Thức Thanh Toán | 4 |
| Thanh Toán | 3 |
| Khuyến Mãi | 3 |
| Hóa Đơn | 3 |

---

## 🎯 Các Danh Mục Sản Phẩm

### Điện Tử (Electronics)
- Smartphones
- Laptops  
- Tablets
- Headphones & Audio
- Cameras
- Smart Home
- Gaming Devices

### Quần Áo (Clothing)
- Men Clothing
- Women Clothing
- Kids Clothing
- Shoes
- Accessories

### Nhà & Vườn (Home & Garden)
- Furniture
- Gardening Tools
- Kitchen Appliances
- Lighting

### Sách (Books)
- Cookbooks
- Fiction
- Non-Fiction
- Self-Help

### Thể Thao & Ngoài Trời (Sports & Outdoors)
- Running
- Cycling
- Fitness Equipment
- Outdoor Gear

### Làm Đẹp & Chăm Sóc (Beauty & Personal Care)
- Skincare
- Hair Care
- Makeup
- Fragrances

### Thực Phẩm & Đồ Uống (Food & Beverages)
- Coffee & Tea
- Snacks
- Beverages

### Đồ Chơi & Trò Chơi (Toys & Games)
- Action Figures
- Board Games
- Video Games
- Puzzles

---

## ✨ Các Tính Năng Dữ Liệu

✅ **UUID cho các entity chính** (Users, Products, Orders, Carts, etc.)
✅ **IDENTITY cho các entity phụ** (Categories, Roles, etc.)
✅ **JSON Data** cho mô tả chi tiết sản phẩm (màu sắc, dung lượng, v.v.)
✅ **Timestamp** tự động (now())
✅ **Liên kết Foreign Key** đầy đủ
✅ **Dữ liệu test thực tế** với giá cả, ảnh, mô tả

---

**Tạo bởi:** GitHub Copilot  
**Ngày:** March 6, 2026  
**Dự án:** DATN_BE - E-Commerce Backend API

