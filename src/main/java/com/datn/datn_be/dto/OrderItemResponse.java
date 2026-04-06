package com.datn.datn_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    private Integer id;
    private String productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
}
