package com.anqigou.order.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("orders")
public class Order {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 商家ID
     */
    private String sellerId;
    
    /**
     * 收货人
     */
    private String receiverName;
    
    /**
     * 收货人手机号
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 商品总数
     */
    private Integer productCount;
    
    /**
     * 商品金额（单位：分）
     */
    private Long productAmount;
    
    /**
     * 配送费（单位：分）
     */
    private Long shippingFee;
    
    /**
     * 优惠金额（单位：分）
     */
    private Long discountAmount;
    
    /**
     * 订单总金额（单位：分）
     */
    private Long totalAmount;
    
    /**
     * 实际支付金额（单位：分）
     */
    private Long actualPayment;
    
    /**
     * 支付方式（weixin、alipay）
     */
    private String paymentMethod;
    
    /**
     * 配送方式
     */
    private String shippingMethod;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 支付时间
     */
    private LocalDateTime paidTime;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippedTime;
    
    /**
     * 签收时间
     */
    private LocalDateTime signedTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
