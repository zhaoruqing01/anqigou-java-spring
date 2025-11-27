package com.anqigou.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品SKU实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("product_sku")
public class ProductSku {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 规格名称（如颜色+尺寸）
     */
    private String specName;
    
    /**
     * 规格值JSON（如{color: 红色, size: L}）
     */
    private String specValueJson;
    
    /**
     * 该规格价格（单位：分）
     */
    private Long price;
    
    /**
     * 该规格库存
     */
    private Integer stock;
    
    /**
     * 条码
     */
    private String barcode;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
