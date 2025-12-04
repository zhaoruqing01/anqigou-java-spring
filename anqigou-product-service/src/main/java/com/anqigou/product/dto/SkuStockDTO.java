package com.anqigou.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKU库存信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuStockDTO {
    
    /**
     * SKU ID
     */
    private String skuId;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 规格名称
     */
    private String specName;
    
    /**
     * 规格值JSON
     */
    private String specValueJson;
    
    /**
     * 价格（单位：分）
     */
    private Long price;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 商家ID
     */
    private String sellerId;
    
    /**
     * 主图URL
     */
    private String mainImage;
}
