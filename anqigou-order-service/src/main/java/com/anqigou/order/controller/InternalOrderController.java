package com.anqigou.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.dto.OrderInfoForLogisticsDTO;
import com.anqigou.order.entity.Order;
import com.anqigou.order.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 订单服务内部接口
 * 供其他微服务调用
 */
@RestController
@RequestMapping("/order/internal")
@Slf4j
public class InternalOrderController {
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 获取订单信息(供物流服务调用)
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderInfoForLogisticsDTO> getOrderInfo(@PathVariable String orderId) {
        log.info("内部接口:获取订单信息: orderId={}", orderId);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return ApiResponse.failure(404, "订单不存在");
        }
        
        // 解析收货地址
        String fullAddress = order.getReceiverAddress();
        String receiverProvince = "";
        String receiverCity = "";
        String receiverDistrict = "";
        String receiverDetailAddress = fullAddress;
        
        // 简单解析省市区(实际应该在创建订单时分开存储)
        if (fullAddress != null) {
            if (fullAddress.contains("省")) {
                int idx = fullAddress.indexOf("省");
                receiverProvince = fullAddress.substring(0, idx + 1);
                fullAddress = fullAddress.substring(idx + 1);
            } else if (fullAddress.startsWith("北京市") || fullAddress.startsWith("上海市") || 
                       fullAddress.startsWith("天津市") || fullAddress.startsWith("重庆市")) {
                receiverProvince = fullAddress.substring(0, 3);
                fullAddress = fullAddress.substring(3);
            }
            
            if (fullAddress.contains("市")) {
                int idx = fullAddress.indexOf("市");
                receiverCity = fullAddress.substring(0, idx + 1);
                fullAddress = fullAddress.substring(idx + 1);
            }
            
            if (fullAddress.contains("区") || fullAddress.contains("县")) {
                int idx = Math.max(fullAddress.indexOf("区"), fullAddress.indexOf("县"));
                if (idx > 0) {
                    receiverDistrict = fullAddress.substring(0, idx + 1);
                    receiverDetailAddress = fullAddress.substring(idx + 1);
                }
            }
        }
        
        OrderInfoForLogisticsDTO dto = OrderInfoForLogisticsDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverProvince(receiverProvince)
                .receiverCity(receiverCity)
                .receiverDistrict(receiverDistrict)
                .receiverDetailAddress(receiverDetailAddress)
                .build();
        
        log.info("返回订单信息: orderId={}, receiverProvince={}, receiverCity={}, receiverDistrict={}", 
                orderId, receiverProvince, receiverCity, receiverDistrict);
        
        return ApiResponse.success(dto);
    }
}
