package com.anqigou.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品列表项DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListItemDTO {
    
    /**
     * 商品ID
     */
    private String id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 价格（单位：分）
     */
    private Long price;
    
    /**
     * 原价（单位：分）
     */
    private Long originalPrice;
    
    /**
     * 主图
     */
    private String mainImage;
    
    /**
     * 销售数量
     */
    private Integer soldCount;
    
    /**
     * 评分
     */
    private Double rating;
}
