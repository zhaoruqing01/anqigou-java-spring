package com.anqigou.payment.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.payment.dto.PaymentRequest;
import com.anqigou.payment.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 微信支付 - 获取预支付信息
     */
    @PostMapping("/wechat/prepare")
    public ApiResponse<Object> wechatPayPrepare(@RequestBody PaymentRequest request) {
        Object prepayInfo = paymentService.wechatPayPrepare(request);
        return ApiResponse.success(prepayInfo);
    }
    
    /**
     * 微信支付 - 回调通知
     */
    @PostMapping("/wechat/notify")
    public String wechatPayNotify(@RequestBody String xmlData) {
        try {
            paymentService.wechatPayNotify(xmlData);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
        } catch (Exception e) {
            log.error("Wechat payment notify error", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
    }
    
    /**
     * 支付宝支付 - 获取支付表单
     */
    @PostMapping("/alipay/prepare")
    public ApiResponse<String> alipayPrepare(@RequestBody PaymentRequest request) {
        String paymentForm = paymentService.alipayPrepare(request);
        return ApiResponse.success(paymentForm);
    }
    
    /**
     * 支付宝支付 - 回调通知
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(@RequestParam Map<String, String> params) {
        try {
            paymentService.alipayNotify(params);
            return "success";
        } catch (Exception e) {
            log.error("Alipay payment notify error", e);
            return "failed";
        }
    }
    
    /**
     * 查询支付状态
     */
    @GetMapping("/{orderId}/status")
    public ApiResponse<Object> queryPaymentStatus(@PathVariable String orderId) {
        Object paymentStatus = paymentService.queryPaymentStatus(orderId);
        return ApiResponse.success(paymentStatus);
    }
    
    /**
     * 退款
     */
    @PostMapping("/{orderId}/refund")
    public ApiResponse<String> refund(@PathVariable String orderId, 
                                   @RequestParam String reason,
                                   @RequestAttribute String userId) {
        paymentService.refund(orderId, reason);
        return ApiResponse.success("退款申请已提交");
    }
}
