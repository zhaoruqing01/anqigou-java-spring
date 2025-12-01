package com.anqigou.user.dto;

import lombok.Data;

/**
 * 商品SKU DTO
 */
@Data
public class ProductSkuDTO {
    private String id;
    private String productId;
    private String specName;
    private Integer price;
    private Integer stock;
}