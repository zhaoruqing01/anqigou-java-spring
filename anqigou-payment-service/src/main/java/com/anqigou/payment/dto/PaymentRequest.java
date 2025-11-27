package com.anqigou.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付请求 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    
    /**
     * 订单 ID
     */
    private String orderId;
    
    /**
     * 支付金额（单位：分）
     */
    private Long amount;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 用户 ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 用户电话
     */
    private String userPhone;
    
    /**
     * 支付方式（wechat、alipay）
     */
    private String paymentMethod;
}
