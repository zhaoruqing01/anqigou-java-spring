package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 反馈DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackDTO {
    
    /**
     * 反馈类型（bug-功能异常，suggestion-功能建议，complaint-投诉建议，other-其他）
     */
    private String type;
    
    /**
     * 反馈标题
     */
    private String title;
    
    /**
     * 反馈内容
     */
    private String content;
    
    /**
     * 联系方式（手机号或邮箱）
     */
    private String contactInfo;
    
    /**
     * 图片列表（JSON格式）
     */
    private String images;
}