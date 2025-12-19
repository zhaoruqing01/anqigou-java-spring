package com.anqigou.logistics.dto;

import lombok.Data;

/**
 * 物流评价请求DTO
 */
@Data
public class LogisticsEvaluateRequest {
    
    /**
     * 物流速度评分（1-5星）
     */
    private Integer speedRating;
    
    /**
     * 快递员服务评分（1-5星）
     */
    private Integer serviceRating;
    
    /**
     * 商品完好度评分（1-5星）
     */
    private Integer qualityRating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片（JSON数组格式）
     */
    private String images;
    
    /**
     * 是否匿名（0-否，1-是）
     */
    private Integer isAnonymous;
}
