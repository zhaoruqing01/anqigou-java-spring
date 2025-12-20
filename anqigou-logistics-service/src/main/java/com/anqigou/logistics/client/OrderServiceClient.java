package com.anqigou.logistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.logistics.dto.OrderInfoDTO;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "anqigou-order-service", url = "${feign.client.order.url:http://localhost:8083}")
public interface OrderServiceClient {
    
    /**
     * 获取订单信息（用于物流服务）
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/order/internal/{orderId}")
    ApiResponse<OrderInfoDTO> getOrderInfo(@PathVariable("orderId") String orderId);
}
