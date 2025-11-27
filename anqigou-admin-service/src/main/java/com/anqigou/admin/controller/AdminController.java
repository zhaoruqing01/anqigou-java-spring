package com.anqigou.admin.controller;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.admin.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@Validated
@Slf4j
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 获取待审核商家列表
     */
    @GetMapping("/sellers/pending")
    public ApiResponse<Object> getPendingSellerList(@RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "20") int pageSize,
                                                   @RequestAttribute String adminId) {
        Object sellerList = adminService.getPendingSellerList(pageNum, pageSize);
        return ApiResponse.success(sellerList);
    }
    
    /**
     * 审核商家
     */
    @PostMapping("/sellers/{sellerId}/approve")
    public ApiResponse<String> approveSeller(@PathVariable String sellerId,
                                          @RequestParam boolean approved,
                                          @RequestParam(required = false) String rejectReason,
                                          @RequestAttribute String adminId) {
        adminService.approveSeller(sellerId, approved, rejectReason);
        return ApiResponse.success("商家审核完成");
    }
    
    /**
     * 获取待审核商品列表
     */
    @GetMapping("/products/pending")
    public ApiResponse<Object> getPendingProductList(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "20") int pageSize,
                                                    @RequestAttribute String adminId) {
        Object productList = adminService.getPendingProductList(pageNum, pageSize);
        return ApiResponse.success(productList);
    }
    
    /**
     * 审核商品
     */
    @PostMapping("/products/{productId}/approve")
    public ApiResponse<String> approveProduct(@PathVariable String productId,
                                           @RequestParam boolean approved,
                                           @RequestParam(required = false) String rejectReason,
                                           @RequestAttribute String adminId) {
        adminService.approveProduct(productId, approved, rejectReason);
        return ApiResponse.success("商品审核完成");
    }
    
    /**
     * 获取平台数据统计
     */
    @GetMapping("/statistics")
    public ApiResponse<Object> getPlatformStatistics(@RequestAttribute String adminId) {
        Object statistics = adminService.getPlatformStatistics();
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public ApiResponse<Object> getUserList(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "20") int pageSize,
                                          @RequestAttribute String adminId) {
        Object userList = adminService.getUserList(pageNum, pageSize);
        return ApiResponse.success(userList);
    }
    
    /**
     * 处理用户反馈
     */
    @PostMapping("/feedback/{feedbackId}/reply")
    public ApiResponse<String> handleUserFeedback(@PathVariable String feedbackId,
                                               @RequestParam String reply,
                                               @RequestAttribute String adminId) {
        adminService.handleUserFeedback(feedbackId, reply);
        return ApiResponse.success("反馈已处理");
    }
}
