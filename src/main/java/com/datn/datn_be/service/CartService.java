package com.datn.datn_be.service;

import com.datn.datn_be.dto.CartItemRequest;
import com.datn.datn_be.dto.CartItemResponse;
import com.datn.datn_be.dto.CartResponse;
import com.datn.datn_be.dto.UpdateCartItemRequest;
import com.datn.datn_be.entity.Cart;
import com.datn.datn_be.entity.CartItem;
import com.datn.datn_be.entity.Product;
import com.datn.datn_be.entity.ProductImage;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.CartItemRepository;
import com.datn.datn_be.repository.CartRepository;
import com.datn.datn_be.repository.ProductImageRepository;
import com.datn.datn_be.repository.ProductRepository;
import com.datn.datn_be.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final JwtUtil jwtUtil;

    /** Lấy hoặc tạo cart cho user */
    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setStatus("ACTIVE");
            cart.setCreatedAt(Instant.now());
            cart.setUpdatedAt(Instant.now());
            return cartRepository.save(cart);
        });
    }

    /** Parse userId từ Bearer token */
    private UUID getUserIdFromToken(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new ClientSideException(401, "Token không hợp lệ");
        }
        return UUID.fromString(userId);
    }

    /** Map CartItem sang CartItemResponse, đính kèm tên và ảnh sản phẩm */
    private CartItemResponse mapItem(CartItem item) {
        CartItemResponse res = new CartItemResponse();
        res.setId(item.getId());
        res.setProductId(item.getProductId().toString());
        res.setQuantity(item.getQuantity());
        res.setPrice(item.getPrice());

        Optional<Product> productOpt = productRepository.findById(item.getProductId());
        productOpt.ifPresent(p -> {
            res.setProductName(p.getName());
            res.setBrand(p.getBrand());
        });

        List<ProductImage> images = productImageRepository.findByProductId(item.getProductId());
        if (!images.isEmpty()) {
            res.setImageUrl(images.get(0).getImageUrl());
        }
        return res;
    }

    /** GET /v1/cart — lấy giỏ hàng */
    public CartResponse getCart(String authHeader) {
        UUID userId = getUserIdFromToken(authHeader);
        Cart cart = getOrCreateCart(userId);
        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId())
                .stream().map(this::mapItem).collect(Collectors.toList());
        return new CartResponse(cart.getId().toString(), items);
    }

    /** POST /v1/cart/add — thêm hoặc tăng số lượng */
    @Transactional
    public CartResponse addItem(String authHeader, CartItemRequest request) {
        UUID userId = getUserIdFromToken(authHeader);
        Cart cart = getOrCreateCart(userId);
        UUID productId = UUID.fromString(request.getProductId());

        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setProductId(productId);
            item.setQuantity(request.getQuantity());
            item.setPrice(request.getPrice());
            item.setCreatedAt(Instant.now());
            cartItemRepository.save(item);
        }
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return getCart(authHeader);
    }

    /** PUT /v1/cart/item/{itemId} — cập nhật số lượng */
    @Transactional
    public CartResponse updateItem(String authHeader, Integer itemId, UpdateCartItemRequest request) {
        UUID userId = getUserIdFromToken(authHeader);
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy sản phẩm trong giỏ hàng"));
        if (!item.getCartId().equals(cart.getId())) {
            throw new ClientSideException(403, "Không có quyền chỉnh sửa");
        }
        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return getCart(authHeader);
    }

    /** DELETE /v1/cart/item/{itemId} — xóa 1 item */
    @Transactional
    public CartResponse removeItem(String authHeader, Integer itemId) {
        UUID userId = getUserIdFromToken(authHeader);
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy sản phẩm trong giỏ hàng"));
        if (!item.getCartId().equals(cart.getId())) {
            throw new ClientSideException(403, "Không có quyền chỉnh sửa");
        }
        cartItemRepository.delete(item);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return getCart(authHeader);
    }

    /** DELETE /v1/cart/clear — xóa toàn bộ giỏ hàng */
    @Transactional
    public void clearCart(String authHeader) {
        UUID userId = getUserIdFromToken(authHeader);
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }
}
