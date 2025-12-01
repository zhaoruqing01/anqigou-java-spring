package com.anqigou.user.dto;

import java.util.List;

import lombok.Data;

/**
 * 商品详情DTO
 */
@Data
public class ProductDetailDTO {
    private String id;
    private String name;
    private Long price;
    private Long originalPrice;
    private List<ProductSkuDTO> skus;
    private Integer soldCount;
    private Double rating;
    private Integer ratingCount;
    private String mainImage;
    private String description;
}
