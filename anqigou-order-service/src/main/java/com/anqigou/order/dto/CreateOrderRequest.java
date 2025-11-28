package com.anqigou.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String addressId;
    private Integer paymentMethod;
    private String shippingMethod;
    private String remark;
    private List<OrderItemDTO> items;
}
