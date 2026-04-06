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
public class CartPromotionId implements Serializable {
    private static final long serialVersionUID = -8017535423722701480L;
    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Column(name = "promotion_id", nullable = false)
    private UUID promotionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CartPromotionId entity = (CartPromotionId) o;
        return Objects.equals(this.cartId, entity.cartId) &&
                Objects.equals(this.promotionId, entity.promotionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, promotionId);
    }

}