package com.anqigou.payment.service;

import com.anqigou.payment.dto.MockPayRequest;
import com.anqigou.payment.dto.MockPayResponse;
import com.anqigou.payment.dto.PaymentRequest;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 微信支付 - 获取预支付ID
     */
    Object wechatPayPrepare(PaymentRequest request);
    
    /**
     * 微信支付 - 回调处理
     */
    void wechatPayNotify(String xmlData);
    
    /**
     * 支付宝支付 - 获取支付表单
     */
    String alipayPrepare(PaymentRequest request);
    
    /**
     * 支付宝支付 - 回调处理
     */
    void alipayNotify(java.util.Map<String, String> params);
    
    /**
     * 查询订单支付状态
     */
    Object queryPaymentStatus(String orderId);
    
    /**
     * 退款
     */
    void refund(String orderId, String reason);
    
    /**
     * 金额校验
     */
    boolean validateAmount(String orderId, long amount);
    
    /**
     * 模拟支付 - 直接完成支付
     * 
     * @param request 模拟支付请求
     * @return 支付结果
     */
    MockPayResponse mockPay(MockPayRequest request);
}
