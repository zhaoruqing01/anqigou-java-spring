package com.anqigou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private String id;
    private String productId;
    private String skuId;
    private String productName;
    private String specInfo;
    private Long unitPrice;
    private Integer quantity;
    private Long subtotal;
    private String mainImage;
}
