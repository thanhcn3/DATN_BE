package com.datn.datn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String productId;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private String brand;
    private String status;
    private String createdAt;
    private Map<String, Object> descriptionDetail;
    private List<String> imageUrls;
}

