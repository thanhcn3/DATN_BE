package com.datn.datn_be.repository;

import com.datn.datn_be.entity.OrderPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotion, OrderPromotion.OrderPromotionPK> {
    List<OrderPromotion> findByOrderId(UUID orderId);
    List<OrderPromotion> findByPromotionId(UUID promotionId);
}
