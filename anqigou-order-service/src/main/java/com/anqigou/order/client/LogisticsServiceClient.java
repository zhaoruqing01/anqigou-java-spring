package com.anqigou.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.anqigou.common.response.ApiResponse;

/**
 * 物流服务 Feign 客户端
 */
@FeignClient(name = "anqigou-logistics-service", url = "${feign.client.logistics.url:http://localhost:8085}")
public interface LogisticsServiceClient {
    
    /**
     * 发货
     * 
     * @param orderId 订单ID
     * @param courierCompany 快递公司
     * @param trackingNo 快递单号
     * @return 响应
     */
    @PostMapping("/api/logistics/ship")
    ApiResponse<Void> shipOrder(@RequestParam("orderId") String orderId,
                                @RequestParam("courierCompany") String courierCompany,
                                @RequestParam("trackingNo") String trackingNo);
    
    /**
     * 获取物流详情
     * 
     * @param orderId 订单ID
     * @return 物流详情
     */
    @GetMapping("/api/logistics/order/{orderId}")
    ApiResponse<Object> getLogisticsDetail(@PathVariable("orderId") String orderId);
    
    /**
     * 确认收货
     * 
     * @param orderId 订单ID
     * @return 响应
     */
    @PostMapping("/api/logistics/confirm-receipt/{orderId}")
    ApiResponse<Void> confirmReceipt(@PathVariable("orderId") String orderId);
}
