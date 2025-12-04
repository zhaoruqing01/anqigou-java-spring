package com.anqigou.user.dto;

import java.util.Date;

import lombok.Data;

@Data
public class FavoriteItemDTO {
    // 收藏信息
    private String id; // 收藏ID
    private String userId; // 用户ID
    private String productId; // 商品ID
    private Date createTime; // 收藏时间
    
    // 商品信息
    private String productName; // 商品名称
    private String mainImage; // 主图
    private Long price; // 售价（单位：分）
    private Long originalPrice; // 原价（单位：分）
    private Integer stock; // 库存
    private Integer soldCount; // 销售数量
    private Double rating; // 评分（0-5分）
    private Integer ratingCount; // 评价数
}
