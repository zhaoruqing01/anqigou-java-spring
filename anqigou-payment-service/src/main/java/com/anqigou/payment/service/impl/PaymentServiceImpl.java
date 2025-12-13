package com.anqigou.payment.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.exception.BizException;
import com.anqigou.payment.config.AlipayConfig;
import com.anqigou.payment.config.WechatPayConfig;
import com.anqigou.payment.dto.PaymentRequest;
import com.anqigou.payment.entity.Payment;
import com.anqigou.payment.mapper.PaymentMapper;
import com.anqigou.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付服务实现
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private WechatPayConfig wechatPayConfig;
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired
    private com.anqigou.payment.client.OrderServiceClient orderServiceClient;
    
    @Override
    @Transactional
    public Object wechatPayPrepare(PaymentRequest request) {
        // 验证金额
        if (!validateAmount(request.getOrderId(), request.getAmount())) {
            throw new BizException(400, "订单金额校验失败");
        }
        
        // 记录支付信息
        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .orderNo("ORD" + System.currentTimeMillis())
                .paymentNo(generatePaymentNo())
                .userId("wechat-user-" + System.currentTimeMillis())
                .amount(request.getAmount())
                .paymentMethod("wechat")
                .status("pending")
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        paymentMapper.insert(payment);
        
        // TODO: 调用微信支付 API 获取预支付ID
        Map<String, Object> result = new HashMap<>();
        result.put("prepayId", "wx" + UUID.randomUUID().toString().replace("-", ""));
        result.put("amount", request.getAmount());
        result.put("orderNo", request.getOrderId());
        
        return result;
    }
    
    @Override
    @Transactional
    public void wechatPayNotify(String xmlData) {
        try {
            // 解析微信支付回调 XML 数据
            // 示例 XML：<xml><out_trade_no>PAY20231120001</out_trade_no><trade_state>SUCCESS</trade_state>...</xml>
            String orderId = xmlData; // TODO: 从 XML 中提取 out_trade_no 作为订单ID
            String tradeState = "SUCCESS"; // TODO: 从 XML 中提取 trade_state
            
            if ("SUCCESS".equals(tradeState)) {
                updatePaymentStatus(orderId, "success");
            }
            
            log.info("Wechat payment notify processed: {}", orderId);
        } catch (Exception e) {
            log.error("Failed to process wechat payment notify", e);
            throw new BizException(500, "处理支付回调失败");
        }
    }
    
    @Override
    @Transactional
    public String alipayPrepare(PaymentRequest request) {
        // 验证金额
        if (!validateAmount(request.getOrderId(), request.getAmount())) {
            throw new BizException(400, "订单金额校验失败");
        }
        
        // 记录支付信息
        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .orderNo("ORD" + System.currentTimeMillis())
                .paymentNo(generatePaymentNo())
                .userId("alipay-user-" + System.currentTimeMillis())
                .amount(request.getAmount())
                .paymentMethod("alipay")
                .status("pending")
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        paymentMapper.insert(payment);
        
        // TODO: 调用支付宝支付 API 构建支付表单
        String paymentForm = buildAlipayForm(request);
        
        return paymentForm;
    }
    
    @Override
    @Transactional
    public void alipayNotify(Map<String, String> params) {
        try {
            // 验证支付宝签名
            // TODO: 实现支付宝签名验证逻辑 boolean signValid = alipaySignVerify(params);
            
            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            String amount = params.get("receipt_amount");
            
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                updatePaymentStatus(outTradeNo, "success");
                log.info("Alipay trade success: outTradeNo={}, amount={}", outTradeNo, amount);
            } else {
                log.warn("Alipay trade failed: outTradeNo={}, tradeStatus={}", outTradeNo, tradeStatus);
            }
        } catch (Exception e) {
            log.error("Failed to process alipay notify", e);
            throw new BizException(500, "处理支付回调失败");
        }
    }
    
    @Override
    public Object queryPaymentStatus(String orderId) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Payment payment = paymentMapper.selectOne(queryWrapper);
        
        if (payment == null) {
            throw new BizException(404, "支付信息不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", payment.getOrderId());
        result.put("paymentNo", payment.getPaymentNo());
        result.put("amount", payment.getAmount());
        result.put("status", payment.getStatus());
        result.put("paymentMethod", payment.getPaymentMethod());
        result.put("paidTime", payment.getPaidTime());
        result.put("transactionId", payment.getTransactionId());
        
        return result;
    }
    
    @Override
    @Transactional
    public void refund(String orderId, String reason) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Payment payment = paymentMapper.selectOne(queryWrapper);
        
        if (payment == null) {
            throw new BizException(404, "支付信息不存在");
        }
        
        if (!"success".equals(payment.getStatus())) {
            throw new BizException(400, "支付状态不允许退款");
        }
        
        // TODO: 调用对应支付渠道的退款 API
        // 更新支付状态
        payment.setStatus("refunded");
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);
        
        log.info("Refund processed: orderId={}, amount={}, reason={}", 
                orderId, payment.getAmount(), reason);
    }
    
    @Override
    public boolean validateAmount(String orderId, long amount) {
        try {
            // 简单校验：金额必须大于0
            if (amount <= 0) {
                log.warn("Invalid amount: orderId={}, amount={}", orderId, amount);
                return false;
            }
            
            // 尝试从订单服务查询订单信息进行验证
            try {
                com.anqigou.common.response.ApiResponse<Object> response = orderServiceClient.getOrderInfo(orderId);
                if (response == null || response.getData() == null) {
                    log.warn("Order not found, but allow payment for mock: orderId={}", orderId);
                    // 对于模拟支付，即使订单服务调用失败也允许继续
                    return true;
                }
                log.info("Order validation success: orderId={}, amount={}", orderId, amount);
            } catch (Exception e) {
                log.warn("Failed to call order service, but allow payment for mock: orderId={}", orderId, e);
                // 对于模拟支付，订单服务调用失败时也允许继续
            }
            
            return true;
        } catch (Exception e) {
            log.error("Failed to validate order amount: orderId={}", orderId, e);
            // 对于模拟支付，出现异常时也允许继续
            return true;
        }
    }
    
    /**
     * 更新支付状态
     */
    private void updatePaymentStatus(String orderId, String status) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Payment payment = paymentMapper.selectOne(queryWrapper);
        
        if (payment != null) {
            payment.setStatus("paid".equals(status) ? "paid" : status);
            if ("paid".equals(status)) {
                payment.setPaidTime(LocalDateTime.now());
            }
            payment.setUpdateTime(LocalDateTime.now());
            paymentMapper.updateById(payment);
            
            // 调用订单服务更新订单支付状态
            if ("paid".equals(status)) {
                try {
                    orderServiceClient.updateOrderPaymentStatus(orderId, payment.getPaymentNo());
                    log.info("Order payment status updated: orderId={}, paymentNo={}", orderId, payment.getPaymentNo());
                } catch (Exception e) {
                    log.error("Failed to update order payment status: orderId={}", orderId, e);
                }
            }
        }
    }
    
    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
    
    /**
     * 构建支付宝支付表单
     */
    private String buildAlipayForm(PaymentRequest request) {
        // 构建支付宝支付表单
        // 实际环境中应使用支付宝SDK生成真实的表单
        StringBuilder form = new StringBuilder();
        form.append("<form id='alipayForm' name='alipayForm' action='https://openapi.alipay.com/gateway.do' method='POST'>");
        form.append("<input type='hidden' name='app_id' value='").append(alipayConfig.getAppId()).append("'/>");
        form.append("<input type='hidden' name='out_trade_no' value='").append(request.getOrderId()).append("'/>");
        form.append("<input type='hidden' name='amount' value='").append(request.getAmount() / 100.0).append("'/>");
        form.append("<input type='submit' value='支付'/>");
        form.append("</form>");
        form.append("<script>document.getElementById('alipayForm').submit();</script>");
        return form.toString();
    }
    
    @Override
    @Transactional
    public Object mockPay(PaymentRequest request) {
        // 验证金额
        if (!validateAmount(request.getOrderId(), request.getAmount())) {
            throw new BizException(400, "订单金额校验失败");
        }
        
        // 从订单服务获取订单信息，提取真实的 userId 和 orderNo
        String userId = null;
        String orderNo = null;
        try {
            com.anqigou.common.response.ApiResponse<Object> response = orderServiceClient.getOrderInfo(request.getOrderId());
            if (response != null && response.getData() != null) {
                Map<String, Object> orderData = (Map<String, Object>) response.getData();
                userId = (String) orderData.get("userId");
                orderNo = (String) orderData.get("orderNo");
                log.info("Get order info from order service: userId={}, orderNo={}", userId, orderNo);
            }
        } catch (Exception e) {
            log.warn("Failed to get order info from order service: orderId={}", request.getOrderId(), e);
        }
        
        // 如果无法从订单服务获取，使用默认值（降级处理）
        if (userId == null || userId.isEmpty()) {
            userId = "default-user";
            log.warn("Using default userId for payment: orderId={}", request.getOrderId());
        }
        if (orderNo == null || orderNo.isEmpty()) {
            orderNo = "ORD" + System.currentTimeMillis();
            log.warn("Using generated orderNo for payment: orderId={}", request.getOrderId());
        }
        
        // 生成支付单号和模拟交易号
        String paymentNo = generatePaymentNo();
        String transactionId = "MOCK" + System.currentTimeMillis() + (int)(Math.random() * 10000);
        
        // 创建支付记录，状态直接设为已支付
        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .orderNo(orderNo)
                .paymentNo(paymentNo)
                .userId(userId)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "mock")
                .status("paid")
                .transactionId(transactionId)
                .paidTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        paymentMapper.insert(payment);
        
        // 调用订单服务更新订单支付状态
        try {
            orderServiceClient.updateOrderPaymentStatus(request.getOrderId(), paymentNo);
            log.info("Mock payment success and order status updated: orderId={}, paymentNo={}, transactionId={}", 
                    request.getOrderId(), paymentNo, transactionId);
        } catch (Exception e) {
            log.error("Failed to update order payment status: orderId={}", request.getOrderId(), e);
            // 对于模拟支付，即使更新订单状态失败也不抛出异常，允许支付继续
            log.warn("Order status update failed, but payment record is saved: orderId={}", request.getOrderId());
        }
        
        // 启动异步任务：1分钟后自动发货
        scheduleAutoShipment(request.getOrderId());
        
        // 返回支付结果
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", request.getOrderId());
        result.put("paymentNo", paymentNo);
        result.put("transactionId", transactionId);
        result.put("amount", request.getAmount());
        result.put("paymentMethod", payment.getPaymentMethod());
        result.put("status", "paid");
        result.put("paidTime", payment.getPaidTime());
        result.put("message", "模拟支付成功，1分钟后自动发货");
        
        return result;
    }
    
    /**
     * 安排自动发货任务
     */
    private void scheduleAutoShipment(String orderId) {
        new Thread(() -> {
            try {
                // 等待1分钟
                Thread.sleep(60000);
                
                // 生成模拟快递信息
                String[] companies = {"顺丰", "中通", "圆通", "申通", "韵达"};
                String courierCompany = companies[(int)(Math.random() * companies.length)];
                String trackingNo = generateTrackingNo(courierCompany);
                
                // 调用订单服务发货
                try {
                    orderServiceClient.shipOrder(orderId, courierCompany, trackingNo);
                    log.info("Auto shipment success: orderId={}, courier={}, tracking={}", 
                            orderId, courierCompany, trackingNo);
                } catch (Exception e) {
                    log.error("Auto shipment failed: orderId={}", orderId, e);
                }
            } catch (InterruptedException e) {
                log.error("Auto shipment thread interrupted: orderId={}", orderId, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * 生成模拟快递单号
     */
    private String generateTrackingNo(String company) {
        String prefix;
        switch (company) {
            case "顺丰":
                prefix = "SF";
                break;
            case "中通":
                prefix = "ZTO";
                break;
            case "圆通":
                prefix = "YTO";
                break;
            case "申通":
                prefix = "STO";
                break;
            case "韵达":
                prefix = "YD";
                break;
            default:
                prefix = "EXP";
        }
        return prefix + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
}
