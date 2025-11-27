package com.anqigou.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("payment")
public class Payment {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 支付单号
     */
    private String paymentNo;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 支付金额（单位：分）
     */
    private Long amount;
    
    /**
     * 支付方式（weixin、alipay）
     */
    private String paymentMethod;
    
    /**
     * 支付状态（pending-待支付，paid-已支付，failed-失败，refunded-已退款）
     */
    private String status;
    
    /**
     * 第三方交易ID
     */
    private String transactionId;
    
    /**
     * 支付完成时间
     */
    private LocalDateTime paidTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
