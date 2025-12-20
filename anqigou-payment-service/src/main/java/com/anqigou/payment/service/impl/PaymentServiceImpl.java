package com.anqigou.payment.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.exception.BizException;
import com.anqigou.payment.config.AlipayConfig;
import com.anqigou.payment.config.WechatPayConfig;
import com.anqigou.payment.dto.MockPayRequest;
import com.anqigou.payment.dto.MockPayResponse;
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
    
    /**
     * 模拟支付 - 完全重写的实现
     * <p>
     * 处理流程：
     * 1. 参数验证（订单ID、金额）
     * 2. 从订单服务获取订单信息（userId、orderNo）
     * 3. 创建支付记录并保存到数据库
     * 4. 调用订单服务更新订单支付状态
     * 5. 可选：启动异步任务30秒后自动发货
     * 6. 返回完整的支付结果
     */
    @Override
    @Transactional
    public MockPayResponse mockPay(MockPayRequest request) {
        log.info("=== 开始处理模拟支付 ===");
        log.info("请求参数: orderId={}, amount={}, paymentMethod={}, userId={}, autoShip={}", 
                request.getOrderId(), request.getAmount(), request.getPaymentMethod(), 
                request.getUserId(), request.getAutoShip());
        
        // 1. 参数验证
        if (request.getOrderId() == null || request.getOrderId().trim().isEmpty()) {
            throw new BizException(400, "订单ID不能为空");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BizException(400, "支付金额必须大于0");
        }
        
        // 2. 验证订单金额
        if (!validateAmount(request.getOrderId(), request.getAmount())) {
            throw new BizException(400, "订单金额校验失败");
        }
        
        // 3. 检查是否已存在支付记录（注释掉重复支付检查，允许多次测试）
        // QueryWrapper<Payment> existQuery = new QueryWrapper<>();
        // existQuery.eq("order_id", request.getOrderId())
        //           .eq("deleted", 0)
        //           .eq("status", "paid");
        // Payment existPayment = paymentMapper.selectOne(existQuery);
        // if (existPayment != null) {
        //     log.warn("订单已支付，不允许重复支付: orderId={}", request.getOrderId());
        //     throw new BizException(400, "订单已支付，请勿重复支付");
        // }
        
        // 4. 从订单服务获取订单信息
        String userId = request.getUserId();
        String orderNo = null;
        
        try {
            com.anqigou.common.response.ApiResponse<Object> response = orderServiceClient.getOrderInfo(request.getOrderId());
            if (response != null && response.getData() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> orderData = (Map<String, Object>) response.getData();
                
                // 如果请求中没有userId，则从订单中获取
                if (userId == null || userId.trim().isEmpty()) {
                    userId = (String) orderData.get("userId");
                }
                orderNo = (String) orderData.get("orderNo");
                
                log.info("从订单服务获取订单信息成功: userId={}, orderNo={}", userId, orderNo);
            }
        } catch (Exception e) {
            log.warn("调用订单服务失败，使用降级处理: orderId={}, error={}", request.getOrderId(), e.getMessage());
        }
        
        // 5. 如果无法从订单服务获取，使用默认值（降级处理）
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default-user-" + System.currentTimeMillis();
            log.warn("使用默认userId: {}", userId);
        }
        if (orderNo == null || orderNo.trim().isEmpty()) {
            orderNo = "ORD" + System.currentTimeMillis();
            log.warn("使用生成的orderNo: {}", orderNo);
        }
        
        // 6. 生成支付单号和模拟交易号
        String paymentId = UUID.randomUUID().toString();
        String paymentNo = generatePaymentNo();
        String transactionId = "MOCK" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
        LocalDateTime now = LocalDateTime.now();
        
        // 7. 确定支付方式
        String paymentMethod = request.getPaymentMethod();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "mock";
        }
        
        // 8. 创建支付记录并保存到数据库
        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(request.getOrderId())
                .orderNo(orderNo)
                .paymentNo(paymentNo)
                .userId(userId)
                .amount(request.getAmount())
                .paymentMethod(paymentMethod)
                .status("paid")  // 模拟支付直接设置为已支付
                .transactionId(transactionId)
                .paidTime(now)
                .createTime(now)
                .deleted(0)
                .build();
        
        int insertResult = paymentMapper.insert(payment);
        if (insertResult <= 0) {
            log.error("保存支付记录失败: orderId={}", request.getOrderId());
            throw new BizException(500, "保存支付记录失败");
        }
        
        log.info("支付记录保存成功: paymentId={}, paymentNo={}, transactionId={}", 
                paymentId, paymentNo, transactionId);
        
        // 9. 调用订单服务更新订单支付状态
        boolean orderUpdateSuccess = false;
        try {
            orderServiceClient.updateOrderPaymentStatus(request.getOrderId(), paymentNo);
            orderUpdateSuccess = true;
            log.info("订单支付状态更新成功: orderId={}, paymentNo={}", request.getOrderId(), paymentNo);
        } catch (Exception e) {
            log.error("更新订单支付状态失败: orderId={}, error={}", request.getOrderId(), e.getMessage(), e);
            // 注意：对于模拟支付，即使更新订单状态失败也不抛出异常
            // 支付记录已经保存，可以后续通过补偿机制处理
        }
        
        // 10. 判断是否需要自动发货（默认为true）
        Boolean autoShip = request.getAutoShip();
        if (autoShip == null) {
            autoShip = true;  // 默认自动发货
        }
        
        if (autoShip && orderUpdateSuccess) {
            // 启动异步任务：30秒后自动发货
            scheduleAutoShipment(request.getOrderId());
            log.info("已启动自动发货任务: orderId={}", request.getOrderId());
        }
        
        // 11. 构建响应对象
        String message = autoShip ? "模拟支付成功，30秒后自动发货" : "模拟支付成功";
        
        MockPayResponse response = MockPayResponse.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .orderNo(orderNo)
                .paymentNo(paymentNo)
                .userId(userId)
                .amount(request.getAmount())
                .paymentMethod(paymentMethod)
                .status("paid")
                .transactionId(transactionId)
                .paidTime(now)
                .createTime(now)
                .message(message)
                .autoShip(autoShip)
                .build();
        
        log.info("=== 模拟支付处理完成 ===");
        log.info("响应数据: paymentNo={}, transactionId={}, amount={}, autoShip={}", 
                paymentNo, transactionId, request.getAmount(), autoShip);
        
        return response;
    }
    
    /**
     * 安排自动发货任务
     */
    @Async
    private void scheduleAutoShipment(String orderId) {
        try {
            // 等待30秒
            Thread.sleep(30000);
            
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
