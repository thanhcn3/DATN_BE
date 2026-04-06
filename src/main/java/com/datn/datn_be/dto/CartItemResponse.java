package com.datn.datn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Integer id;
    private String productId;
    private String productName;
    private String imageUrl;
    private String brand;
    private Integer quantity;
    private BigDecimal price;
}
