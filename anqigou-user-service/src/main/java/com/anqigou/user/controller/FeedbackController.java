package com.anqigou.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.dto.FeedbackDTO;
import com.anqigou.user.service.FeedbackService;

import lombok.extern.slf4j.Slf4j;

/**
 * 意见反馈控制器
 */
@RestController
@RequestMapping("/feedback")
@Validated
@Slf4j
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    /**
     * 提交反馈
     */
    @PostMapping
    public ApiResponse<String> submitFeedback(@RequestAttribute String userId, @RequestBody FeedbackDTO feedbackDTO) {
        feedbackService.submitFeedback(userId, feedbackDTO);
        return ApiResponse.success("反馈提交成功");
    }
    
    /**
     * 获取用户反馈列表
     */
    @GetMapping("/list")
    public ApiResponse<Object> getUserFeedbackList(@RequestAttribute String userId, 
                                                  @RequestParam(defaultValue = "1") int page, 
                                                  @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(feedbackService.getUserFeedbackList(userId, page, size));
    }
    
    /**
     * 获取反馈详情
     */
    @GetMapping("/detail")
    public ApiResponse<Object> getFeedbackDetail(@RequestAttribute String userId, @RequestParam String feedbackId) {
        return ApiResponse.success(feedbackService.getFeedbackDetail(userId, feedbackId));
    }
}