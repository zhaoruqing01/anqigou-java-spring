package com.anqigou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品SKU DTO（用于订单服务）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuDTO {
    
    /**
     * SKU ID
     */
    private String id;
    
    /**
     * 规格名称（显示用）
     */
    private String specValue;
    
    /**
     * 价格（单位：分）
     */
    private Long price;
    
    /**
     * 库存
     */
    private Integer stock;
}