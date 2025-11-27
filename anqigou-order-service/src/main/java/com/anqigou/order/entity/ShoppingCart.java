package com.anqigou.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 购物车实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("shopping_cart")
public class ShoppingCart {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * SKU ID
     */
    private String skuId;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
