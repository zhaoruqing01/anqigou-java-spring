package com.anqigou.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("product_category")
public class ProductCategory {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 父分类ID
     */
    private String parentId;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类图标URL
     */
    private String iconUrl;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 分类级别（1-一级，2-二级，3-三级）
     */
    private Integer level;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
