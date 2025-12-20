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
import com.anqigou.payment.client.OrderServiceClient;
import com.anqigou.payment.dto.MockPayRequest;
import com.anqigou.payment.dto.MockPayResponse;
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
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
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
    
    /**
     * 模拟支付接口 - 直接完成支付并返回支付结果
     * <p>
     * 该接口用于测试环境，模拟支付成功的场景，可以：
     * 1. 验证订单金额并创建支付记录
     * 2. 直接将支付状态设置为已支付
     * 3. 自动调用订单服务更新订单状态
     * 4. 可选择是否30秒后自动发货
     * 
     * 支持两种调用方式：
     * 方式1（推荐）：传递完整的JSON对象
     * {
     *   "orderId": "xxx",
     *   "amount": 9900,
     *   "paymentMethod": "mock",
     *   "autoShip": true
     * }
     * 
     * 方式2（兼容旧版）：直接传递orderId字符串，amount会从订单服务获取
     * "order-id-string"
     * 
     * @param requestBody 请求体（可以是JSON对象或字符串）
     * @return 支付结果，包含支付单号、交易号等信息
     */
    @PostMapping("/mock/pay")
    public ApiResponse<MockPayResponse> mockPay(@RequestBody String requestBody) {
        try {
            log.info("收到模拟支付原始请求: {}", requestBody);
            
            MockPayRequest request;
            
            // 判断请求格式
            if (requestBody.trim().startsWith("{")) {
                // JSON对象格式，解析为MockPayRequest
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                request = mapper.readValue(requestBody, MockPayRequest.class);
                log.info("解析为JSON对象: orderId={}, amount={}, paymentMethod={}",
                        request.getOrderId(), request.getAmount(), request.getPaymentMethod());
            } else {
                // 字符串格式（只有orderId），兼容旧版本
                String orderId = requestBody.trim().replace("\"", "");
                log.info("解析为orderId字符串: {}", orderId);
                
                // 从订单服务获取订单信息以获取金额
                Long amount = null;
                try {
                    com.anqigou.common.response.ApiResponse<Object> orderResponse = 
                        orderServiceClient.getOrderInfo(orderId);
                    if (orderResponse != null && orderResponse.getData() != null) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> orderData = (java.util.Map<String, Object>) orderResponse.getData();
                        Object amountObj = orderData.get("totalAmount");
                        if (amountObj instanceof Number) {
                            amount = ((Number) amountObj).longValue();
                        }
                        log.info("从订单服务获取金额: {}", amount);
                    }
                } catch (Exception e) {
                    log.warn("从订单服务获取金额失败，使用默认值: {}", e.getMessage());
                }
                
                // 如果无法获取金额，使用默认值100分（1元）
                if (amount == null || amount <= 0) {
                    amount = 100L;
                    log.warn("使用默认金额: {}", amount);
                }
                
                // 构建请求对象
                request = MockPayRequest.builder()
                        .orderId(orderId)
                        .amount(amount)
                        .paymentMethod("mock")
                        .autoShip(true)
                        .build();
            }
            
            log.info("处理模拟支付请求: orderId={}, amount={}, paymentMethod={}",
                    request.getOrderId(), request.getAmount(), request.getPaymentMethod());

            MockPayResponse result = paymentService.mockPay(request);

            log.info("模拟支付成功: paymentNo={}, transactionId={}",
                    result.getPaymentNo(), result.getTransactionId());

            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("模拟支付失败: error={}", e.getMessage(), e);
            return ApiResponse.failure(500, "模拟支付失败: " + e.getMessage());
        }
    }
}
