package com.datn.datn_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_promotions", schema = "store")
@IdClass(OrderPromotion.OrderPromotionPK.class)
public class OrderPromotion {
    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Id
    @Column(name = "promotion_id", nullable = false)
    private UUID promotionId;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    // Composite Primary Key class
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderPromotionPK implements Serializable {
        private UUID orderId;
        private UUID promotionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderPromotionPK that = (OrderPromotionPK) o;
            return Objects.equals(orderId, that.orderId) && Objects.equals(promotionId, that.promotionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderId, promotionId);
        }
    }
}