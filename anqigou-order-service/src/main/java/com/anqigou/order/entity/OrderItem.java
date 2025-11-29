package com.anqigou.order.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单项实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("order_item")
public class OrderItem {
    
    /**
     * 订单项ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * SKU ID
     */
    private String skuId;
    
    /**
     * 规格信息
     */
    private String specInfo;
    
    /**
     * 单价（单位：分）
     */
    private Long unitPrice;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 小计（单位：分）
     */
    private Long subtotal;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
