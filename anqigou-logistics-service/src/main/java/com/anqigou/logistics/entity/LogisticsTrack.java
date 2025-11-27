package com.anqigou.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流轨迹实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("logistics_track")
public class LogisticsTrack {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 物流ID
     */
    private String logisticsId;
    
    /**
     * 快递单号
     */
    private String trackingNo;
    
    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
    
    /**
     * 操作城市
     */
    private String operateCity;
    
    /**
     * 操作地点（详细）
     */
    private String operateLocation;
    
    /**
     * 物流描述
     */
    private String description;
    
    /**
     * 快递员名称（派送中显示）
     */
    private String courierName;
    
    /**
     * 快递员电话（派送中显示）
     */
    private String courierPhone;
    
    /**
     * 排序（倒序，最新的在前）
     */
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
