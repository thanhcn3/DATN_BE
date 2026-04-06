package com.datn.datn_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class OrderPromotionId implements Serializable {
    private static final long serialVersionUID = -5157263423565751702L;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "promotion_id", nullable = false)
    private UUID promotionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderPromotionId entity = (OrderPromotionId) o;
        return Objects.equals(this.orderId, entity.orderId) &&
                Objects.equals(this.promotionId, entity.promotionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, promotionId);
    }

}