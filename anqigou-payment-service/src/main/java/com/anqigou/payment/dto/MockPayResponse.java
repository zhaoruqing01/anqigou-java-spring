package com.anqigou.payment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模拟支付响应 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MockPayResponse {
    
    /**
     * 支付记录ID
     */
    private String paymentId;
    
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
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 支付状态（paid-已支付）
     */
    private String status;
    
    /**
     * 模拟交易号
     */
    private String transactionId;
    
    /**
     * 支付完成时间
     */
    private LocalDateTime paidTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 提示消息
     */
    private String message;
    
    /**
     * 是否会自动发货
     */
    private Boolean autoShip;
}
