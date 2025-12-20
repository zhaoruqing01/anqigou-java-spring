package com.anqigou.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单信息DTO（用于物流服务）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    
    /**
     * 订单ID
     */
    private String id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货人省份
     */
    private String receiverProvince;
    
    /**
     * 收货人城市
     */
    private String receiverCity;
    
    /**
     * 收货人区县
     */
    private String receiverDistrict;
    
    /**
     * 收货人详细地址
     */
    private String receiverDetailAddress;
    
    /**
     * 获取完整收货地址
     */
    public String getFullReceiverAddress() {
        return receiverProvince + receiverCity + receiverDistrict + receiverDetailAddress;
    }
}
