package com.anqigou.seller.service;

import java.util.Map;

/**
 * 商家服务接口
 */
public interface SellerService {
    
    /**
     * 商家注册
     */
    void registerSeller(String userId, String shopName, String licenseNo, String licenseImage);
    
    /**
     * 获取商家信息
     */
    Object getSellerInfo(String sellerId);
    
    /**
     * 更新商家信息
     */
    void updateSellerInfo(String sellerId, Map<String, Object> sellerInfo);
    
    /**
     * 获取商家订单列表
     */
    Object getSellerOrders(String sellerId, int pageNum, int pageSize);
    
    /**
     * 发货
     */
    void shipOrder(String orderId, String courierCompany, String trackingNo);
    
    /**
     * 获取商家数据统计
     */
    Object getSellerStatistics(String sellerId);
}
