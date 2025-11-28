package com.anqigou.order.service;

import java.util.List;

import com.anqigou.order.dto.CreateOrderRequest;
import com.anqigou.order.dto.OrderItemDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    String createOrder(String userId, CreateOrderRequest request);
    
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
     * 获取购物车商品(用于订单创建)
     */
    List<OrderItemDTO> getCartItems(String userId);
    
    /**
     * 更新订单支付状态
     */
    void updatePaymentStatus(String orderId, String paymentNo);
    
    /**
     * 订单发货
     * 
     * @param orderId 订单ID
     * @param courierCompany 快递公司
     * @param trackingNo 快递单号
     */
    void shipOrder(String orderId, String courierCompany, String trackingNo);
}
