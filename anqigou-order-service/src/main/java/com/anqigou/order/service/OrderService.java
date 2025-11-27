package com.anqigou.order.service;

import com.anqigou.order.dto.OrderItemDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    String createOrder(String userId, String addressId, int paymentMethod, String remark);
    
    /**
     * 获取订单详情
     */
    Object getOrderDetail(String orderId, String userId);
    
    /**
     * 获取订单列表
     */
    Page<Object> getOrderList(String userId, int pageNum, int pageSize);
    
    /**
     * 取消订单
     */
    void cancelOrder(String orderId, String userId);
    
    /**
     * 确认收货
     */
    void confirmReceipt(String orderId, String userId);
    
    /**
     * 获取购物车项目
     */
    java.util.List<OrderItemDTO> getCartItems(String userId);
}
