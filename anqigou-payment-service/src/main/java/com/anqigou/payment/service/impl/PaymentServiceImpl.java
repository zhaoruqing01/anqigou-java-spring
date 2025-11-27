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
                .paymentNo(generatePaymentNo())
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
                .paymentNo(generatePaymentNo())
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
        // TODO: 从订单服务查询订单金额进行校验
        // 暂时返回 true
        return amount > 0;
    }
    
    /**
     * 更新支付状态
     */
    private void updatePaymentStatus(String orderId, String status) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Payment payment = paymentMapper.selectOne(queryWrapper);
        
        if (payment != null) {
            payment.setStatus(status);
            payment.setPaidTime(LocalDateTime.now());
            payment.setUpdateTime(LocalDateTime.now());
            paymentMapper.updateById(payment);
            
            // TODO: 发送消息到订单服务，更新订单状态
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
}
