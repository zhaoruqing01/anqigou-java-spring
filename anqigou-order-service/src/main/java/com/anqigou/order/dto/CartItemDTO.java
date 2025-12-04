package com.anqigou.order.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private String skuId;
    private String productId;
    private String productName;
    private String mainImage;
    private String specInfo;
    private Long price;
    private Integer quantity;
    private Integer stock;
}
