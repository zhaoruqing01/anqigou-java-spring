package com.anqigou.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {
    
    /**
     * 事件类型 (created, paid, shipped, delivered, cancelled)
     */
    private String eventType;
    
    /**
     * 订单 ID
     */
    private String orderId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户 ID
     */
    private String userId;
    
    /**
     * 事件时间戳
     */
    private Long timestamp;
    
    /**
     * 事件描述
     */
    private String description;
    
    /**
     * 额外数据
     */
    private String metadata;
}
