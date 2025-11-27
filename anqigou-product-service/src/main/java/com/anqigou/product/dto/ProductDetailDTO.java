package com.anqigou.product.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品详情 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailDTO {
    
    /**
     * 商品ID
     */
    private String id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 价格（单位：分）
     */
    private Long price;
    
    /**
     * 原价（单位：分）
     */
    private Long originalPrice;
    
    /**
     * 商品图片列表
     */
    private List<String> images;
    
    /**
     * 商品详情HTML
     */
    private String detailHtml;
    
    /**
     * SKU列表
     */
    private List<ProductSkuDTO> skus;
    
    /**
     * 销售数量
     */
    private Integer soldCount;
    
    /**
     * 评分
     */
    private Double rating;
    
    /**
     * 评价数
     */
    private Integer ratingCount;
    
    /**
     * 主图
     */
    private String mainImage;
}
