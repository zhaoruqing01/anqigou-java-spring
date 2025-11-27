package com.anqigou.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {
    
    /**
     * 事件类型 (initiated, success, failed, refunded)
     */
    private String eventType;
    
    /**
     * 订单 ID
     */
    private String orderId;
    
    /**
     * 支付 ID
     */
    private String paymentId;
    
    /**
     * 支付金额（单位：分）
     */
    private Long amount;
    
    /**
     * 支付方式 (wechat, alipay)
     */
    private String paymentMethod;
    
    /**
     * 交易号
     */
    private String transactionId;
    
    /**
     * 事件时间戳
     */
    private Long timestamp;
}
