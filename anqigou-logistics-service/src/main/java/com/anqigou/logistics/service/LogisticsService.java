package com.anqigou.logistics.service;

import com.anqigou.logistics.dto.LogisticsDetailDTO;
import com.anqigou.logistics.dto.LogisticsTrackDTO;

import java.util.List;

/**
 * 物流服务接口
 */
public interface LogisticsService {
    
    /**
     * 获取物流详情
     */
    LogisticsDetailDTO getLogisticsDetail(String orderId, String userId);
    
    /**
     * 获取物流轨迹
     */
    List<LogisticsTrackDTO> getLogisticsTracks(String logisticsId);
    
    /**
     * 更新物流状态
     */
    void updateLogisticsStatus(String logisticsId, String status, String description);
    
    /**
     * 添加物流轨迹
     */
    void addLogisticsTrack(String logisticsId, String trackingNo, String operateCity, 
                          String operateLocation, String description);
    
    /**
     * 发货（创建物流）
     */
    void shipOrder(String orderId, String courierCompany, String trackingNo);
    
    /**
     * 确认收货
     */
    void confirmReceipt(String orderId);
    
    /**
     * 评价物流
     */
    void evaluateLogistics(String logisticsId, Integer speedRating, Integer serviceRating, 
                           Integer qualityRating, String content, String images);
}
