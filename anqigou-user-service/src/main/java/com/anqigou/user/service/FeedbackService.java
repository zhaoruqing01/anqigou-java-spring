package com.anqigou.user.service;

import com.anqigou.user.dto.FeedbackDTO;
import com.anqigou.user.entity.Feedback;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 意见反馈服务接口
 */
public interface FeedbackService {
    
    /**
     * 提交意见反馈
     * 
     * @param userId 用户ID
     * @param feedbackDTO 反馈信息DTO
     * @return 反馈ID
     */
    String submitFeedback(String userId, FeedbackDTO feedbackDTO);
    
    /**
     * 获取反馈详情
     * 
     * @param userId 用户ID
     * @param feedbackId 反馈ID
     * @return 反馈详情
     */
    Feedback getFeedbackDetail(String userId, String feedbackId);
    
    /**
     * 获取用户反馈列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 反馈列表
     */
    Page<Feedback> getUserFeedbackList(String userId, int page, int size);
    
    /**
     * 更新反馈状态
     * 
     * @param feedbackId 反馈ID
     * @param status 状态
     */
    void updateFeedbackStatus(String feedbackId, String status);
    
    /**
     * 回复反馈
     * 
     * @param feedbackId 反馈ID
     * @param replyContent 回复内容
     */
    void replyFeedback(String feedbackId, String replyContent);
}