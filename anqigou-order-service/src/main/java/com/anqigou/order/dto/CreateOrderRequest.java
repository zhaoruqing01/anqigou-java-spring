package com.anqigou.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String addressId;
    private String paymentMethod;
    private String shippingMethod;
    private String remark;
    private List<OrderItemRequest> items;
    
    @Data
    public static class OrderItemRequest {
        private String productId;
        private String skuId;
        private Integer quantity;
    }
}
