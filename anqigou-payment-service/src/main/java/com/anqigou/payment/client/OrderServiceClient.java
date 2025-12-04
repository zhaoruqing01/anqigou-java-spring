package com.anqigou.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.anqigou.common.response.ApiResponse;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "anqigou-order-service", url = "${feign.client.order.url:http://localhost:8081}")
public interface OrderServiceClient {
    
    /**
     * 获取订单信息
     */
    @GetMapping("/api/orders/{orderId}")
    ApiResponse<Object> getOrderInfo(@PathVariable("orderId") String orderId);
    
    /**
     * 更新订单状态为已支付
     */
    @GetMapping("/api/orders/{orderId}/pay/{paymentNo}")
    ApiResponse<Void> updateOrderPaymentStatus(@PathVariable("orderId") String orderId, @PathVariable("paymentNo") String paymentNo);
}
