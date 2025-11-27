package com.anqigou.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流轨迹 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogisticsTrackDTO {
    
    /**
     * 操作时间
     */
    private Long operateTime;
    
    /**
     * 操作城市
     */
    private String operateCity;
    
    /**
     * 操作地点
     */
    private String operateLocation;
    
    /**
     * 物流描述
     */
    private String description;
    
    /**
     * 快递员名称
     */
    private String courierName;
    
    /**
     * 快递员电话
     */
    private String courierPhone;
    
    /**
     * 是否最新节点
     */
    private Boolean isLatest;
}
