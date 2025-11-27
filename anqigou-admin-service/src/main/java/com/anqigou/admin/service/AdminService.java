package com.anqigou.admin.service;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 审核商家申请
     */
    void approveSeller(String sellerId, boolean approved, String rejectReason);
    
    /**
     * 获取待审核商家列表
     */
    Object getPendingSellerList(int pageNum, int pageSize);
    
    /**
     * 审核商品
     */
    void approveProduct(String productId, boolean approved, String rejectReason);
    
    /**
     * 获待审核商品列表
     */
    Object getPendingProductList(int pageNum, int pageSize);
    
    /**
     * 获取平台数据统计
     */
    Object getPlatformStatistics();
    
    /**
     * 获取用户列表
     */
    Object getUserList(int pageNum, int pageSize);
    
    /**
     * 处理用户反馈
     */
    void handleUserFeedback(String feedbackId, String reply);
}
