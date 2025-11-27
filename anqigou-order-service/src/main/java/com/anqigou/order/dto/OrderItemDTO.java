package com.anqigou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单项DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    
    /**
     * 订单项ID
     */
    private String id;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * SKU ID
     */
    private String skuId;
    
    /**
     * 规格信息
     */
    private String specInfo;
    
    /**
     * 单价（单位：分）
     */
    private Long unitPrice;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 小计（单位：分）
     */
    private Long subtotal;
    
    /**
     * 商品主图
     */
    private String mainImage;
}
