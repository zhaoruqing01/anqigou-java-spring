package com.anqigou.logistics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.logistics.dto.LogisticsDetailDTO;
import com.anqigou.logistics.dto.LogisticsTrackDTO;
import com.anqigou.logistics.service.LogisticsService;

import lombok.extern.slf4j.Slf4j;

/**
 * 物流控制器
 */
@RestController
@RequestMapping("/logistics")
@Validated
@Slf4j
public class LogisticsController {
    
    @Autowired
    private LogisticsService logisticsService;
    
    /**
     * 发货（供订单服务调用）
     */
    @PostMapping("/ship")
    public ApiResponse<Void> shipOrder(@RequestParam String orderId,
                                       @RequestParam String courierCompany,
                                       @RequestParam String trackingNo) {
        try {
            logisticsService.shipOrder(orderId, courierCompany, trackingNo);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Ship order failed: orderId={}", orderId, e);
            return ApiResponse.failure(500, "发货失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取物流详情
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<LogisticsDetailDTO> getLogisticsDetail(@PathVariable String orderId, 
                                                              @RequestAttribute String userId) {
        LogisticsDetailDTO logistics = logisticsService.getLogisticsDetail(orderId, userId);
        return ApiResponse.success(logistics);
    }
    
    /**
     * 获取物流轨迹
     */
    @GetMapping("/{logisticsId}/tracks")
    public ApiResponse<List<LogisticsTrackDTO>> getLogisticsTracks(@PathVariable String logisticsId) {
        List<LogisticsTrackDTO> tracks = logisticsService.getLogisticsTracks(logisticsId);
        return ApiResponse.success(tracks);
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/confirm-receipt/{orderId}")
    public ApiResponse<String> confirmReceipt(@PathVariable String orderId, @RequestAttribute String userId) {
        logisticsService.confirmReceipt(orderId);
        return ApiResponse.success("确认收货成功");
    }
    
    /**
     * 评价物流
     */
    @PostMapping("/{logisticsId}/evaluate")
    public ApiResponse<String> evaluateLogistics(@PathVariable String logisticsId,
                                              @RequestParam Integer speedRating,
                                              @RequestParam Integer serviceRating,
                                              @RequestParam Integer qualityRating,
                                              @RequestParam(required = false) String content,
                                              @RequestParam(required = false) String images) {
        logisticsService.evaluateLogistics(logisticsId, speedRating, serviceRating, qualityRating, content, images);
        return ApiResponse.success("评价成功");
    }
}
