package com.anqigou.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流评价实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("logistics_evaluation")
public class LogisticsEvaluation {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 物流ID
     */
    private String logisticsId;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 物流速度评分（1-5星）
     */
    private Integer speedRating;
    
    /**
     * 快递员服务态度评分（1-5星）
     */
    private Integer serviceRating;
    
    /**
     * 商品完好度评分（1-5星）
     */
    private Integer qualityRating;
    
    /**
     * 综合评分（1-5星）
     */
    private Double overallRating;
    
    /**
     * 评价内容（文字）
     */
    private String content;
    
    /**
     * 评价图片（JSON数组）
     */
    private String images;
    
    /**
     * 是否匿名（0-否，1-是）
     */
    private Integer isAnonymous;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
