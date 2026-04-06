package com.datn.datn_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private String fullName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String address;
    private String paymentMethod; // COD | VNPAY
    private BigDecimal totalAmount;
    private List<OrderItemRequest> items;
}
