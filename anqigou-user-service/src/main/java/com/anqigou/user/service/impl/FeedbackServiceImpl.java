package com.anqigou.user.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.exception.BizException;
import com.anqigou.user.dto.FeedbackDTO;
import com.anqigou.user.entity.Feedback;
import com.anqigou.user.mapper.FeedbackMapper;
import com.anqigou.user.service.FeedbackService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 意见反馈服务实现类
 */
@Service
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {
    
    @Autowired
    private FeedbackMapper feedbackMapper;
    
    @Override
    @Transactional
    public String submitFeedback(String userId, FeedbackDTO feedbackDTO) {
        // 转换DTO为实体
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setType(feedbackDTO.getType());
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setContent(feedbackDTO.getContent());
        feedback.setContactInfo(feedbackDTO.getContactInfo());
        feedback.setImages(feedbackDTO.getImages());
        
        // 设置默认值
        feedback.setStatus("pending");
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());
        feedback.setDeleted(0);
        
        // 保存反馈信息
        feedbackMapper.insert(feedback);
        
        log.info("提交意见反馈成功，feedbackId: {}, userId: {}", feedback.getId(), feedback.getUserId());
        return feedback.getId();
    }
    
    @Override
    public Feedback getFeedbackDetail(String userId, String feedbackId) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BizException(404, "反馈信息不存在");
        }
        
        // 验证用户权限
        if (!feedback.getUserId().equals(userId)) {
            throw new BizException(403, "无权访问该反馈信息");
        }
        
        return feedback;
    }
    
    @Override
    public Page<Feedback> getUserFeedbackList(String userId, int page, int size) {
        LambdaQueryWrapper<Feedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Feedback::getUserId, userId)
                .eq(Feedback::getDeleted, 0)
                .orderByDesc(Feedback::getCreatedAt);
        
        Page<Feedback> feedbackPage = new Page<>(page, size);
        feedbackMapper.selectPage(feedbackPage, queryWrapper);
        
        return feedbackPage;
    }
    
    @Override
    @Transactional
    public void updateFeedbackStatus(String feedbackId, String status) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BizException(404, "反馈信息不存在");
        }
        
        // 更新状态
        feedback.setStatus(status);
        feedback.setUpdatedAt(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
        
        log.info("更新反馈状态成功，feedbackId: {}, status: {}", feedbackId, status);
    }
    
    @Override
    @Transactional
    public void replyFeedback(String feedbackId, String replyContent) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BizException(404, "反馈信息不存在");
        }
        
        // 更新回复内容和状态
        feedback.setReplyContent(replyContent);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus("completed");
        feedback.setUpdatedAt(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
        
        log.info("回复反馈成功，feedbackId: {}", feedbackId);
    }
}