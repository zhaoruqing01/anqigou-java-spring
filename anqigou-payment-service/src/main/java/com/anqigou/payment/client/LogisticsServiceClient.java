package com.anqigou.payment.client;

import com.anqigou.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 物流服务 Feign 客户端
 */
@FeignClient(name = "anqigou-logistics-service", url = "${feign.client.logistics.url:http://localhost:8083}")
public interface LogisticsServiceClient {
    
    /**
     * 获取物流详情
     */
    @GetMapping("/api/logistics/order/{orderId}")
    ApiResponse<Object> getLogisticsDetail(@PathVariable String orderId);
    
    /**
     * 发货（创建物流）
     */
    @PostMapping("/api/logistics/ship")
    ApiResponse<Void> shipOrder(@RequestParam String orderId, 
                                @RequestParam String courierCompany,
                                @RequestParam String trackingNo);
}
