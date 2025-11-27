package com.anqigou.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("product")
public class Product {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 商家ID
     */
    private String sellerId;
    
    /**
     * 分类ID
     */
    private String categoryId;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 品牌
     */
    private String brand;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 售价（单位：分）
     */
    private Long price;
    
    /**
     * 原价（单位：分）
     */
    private Long originalPrice;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 销售数量
     */
    private Integer soldCount;
    
    /**
     * 评分（0-5分）
     */
    private Double rating;
    
    /**
     * 评价数
     */
    private Integer ratingCount;
    
    /**
     * 主图URL
     */
    private String mainImage;
    
    /**
     * 商品图片（JSON数组）
     */
    private String images;
    
    /**
     * 商品视频URL
     */
    private String videoUrl;
    
    /**
     * 状态（0-下架，1-上架）
     */
    private Integer status;
    
    /**
     * 上架时间
     */
    private LocalDateTime shelfTime;
    
    /**
     * 下架时间
     */
    private LocalDateTime shelfOffTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
