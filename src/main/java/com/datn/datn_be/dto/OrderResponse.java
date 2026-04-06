package com.datn.datn_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private String orderId;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String createdAt;
    private List<OrderItemResponse> items;
    /** Chỉ có khi paymentMethod=VNPAY và đơn hàng mới tạo */
    private String paymentUrl;
}
