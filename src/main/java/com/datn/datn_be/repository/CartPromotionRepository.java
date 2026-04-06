package com.datn.datn_be.repository;

import com.datn.datn_be.entity.CartPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartPromotionRepository extends JpaRepository<CartPromotion, CartPromotion.CartPromotionPK> {
    List<CartPromotion> findByCartId(UUID cartId);
    List<CartPromotion> findByPromotionId(UUID promotionId);
}
