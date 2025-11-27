package com.anqigou.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物流详情 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogisticsDetailDTO {
    
    /**
     * 物流ID
     */
    private String id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 快递公司
     */
    private String courierCompany;
    
    /**
     * 快递单号
     */
    private String trackingNo;
    
    /**
     * 发货人信息
     */
    private String senderInfo;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话（打码显示）
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 物流状态
     */
    private String status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 发货时间
     */
    private Long shippedTime;
    
    /**
     * 预计送达时间
     */
    private Long estimatedDeliveryTime;
    
    /**
     * 签收时间
     */
    private Long signedTime;
    
    /**
     * 物流轨迹列表
     */
    private List<LogisticsTrackDTO> tracks;
    
    /**
     * 是否已评价
     */
    private Boolean hasEvaluated;
}
