package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.CartItemRequest;
import com.datn.datn_be.dto.CartResponse;
import com.datn.datn_be.dto.UpdateCartItemRequest;
import com.datn.datn_be.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    /** Lấy giỏ hàng của user hiện tại */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader("Authorization") String authHeader) {
        try {
            CartResponse cart = cartService.getCart(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy giỏ hàng thành công", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Thêm sản phẩm vào giỏ hàng (tự tăng qty nếu đã có) */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CartItemRequest request) {
        try {
            CartResponse cart = cartService.addItem(authHeader, request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Thêm vào giỏ hàng thành công", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Cập nhật số lượng 1 item */
    @PutMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer itemId,
            @RequestBody UpdateCartItemRequest request) {
        try {
            CartResponse cart = cartService.updateItem(authHeader, itemId, request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Cập nhật giỏ hàng thành công", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Xóa 1 item khỏi giỏ hàng */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer itemId) {
        try {
            CartResponse cart = cartService.removeItem(authHeader, itemId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Xóa sản phẩm khỏi giỏ hàng thành công", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Xóa toàn bộ giỏ hàng */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestHeader("Authorization") String authHeader) {
        try {
            cartService.clearCart(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Xóa giỏ hàng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
