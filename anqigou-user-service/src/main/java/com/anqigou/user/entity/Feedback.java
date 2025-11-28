package com.anqigou.user.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 意见反馈实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("feedback")
public class Feedback {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
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
     * 凭证图片（JSON数组）
     */
    private String images;
    
    /**
     * 联系方式
     */
    private String contactInfo;
    
    /**
     * 处理状态（pending-待处理，processing-处理中，completed-已完成）
     */
    private String status;
    
    /**
     * 回复内容
     */
    private String replyContent;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private Integer deleted;
}