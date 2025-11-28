package com.anqigou.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.dto.CreateOrderRequest;
import com.anqigou.order.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@Validated
@Slf4j
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public ApiResponse<String> createOrder(@RequestAttribute String userId,
                                          @RequestBody CreateOrderRequest request) {
        String orderId = orderService.createOrder(userId, request);
        return ApiResponse.success("订单创建成功", orderId);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<Object> getOrderDetail(@PathVariable String orderId,
                                             @RequestAttribute String userId) {
        Object orderDetail = orderService.getOrderDetail(orderId, userId);
        return ApiResponse.success(orderDetail);
    }
    
    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<Object>> getOrderList(@RequestAttribute String userId,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Page<Object> orders = orderService.getOrderList(userId, pageNum, pageSize);
        return ApiResponse.success(orders);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable String orderId,
                                        @RequestAttribute String userId) {
        orderService.cancelOrder(orderId, userId);
        return ApiResponse.success("订单取消成功");
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/{orderId}/confirm-receipt")
    public ApiResponse<String> confirmReceipt(@PathVariable String orderId,
                                           @RequestAttribute String userId) {
        orderService.confirmReceipt(orderId, userId);
        return ApiResponse.success("订单已签收");
    }
    
    /**
     * 更新订单支付状态（供支付服务调用）
     */
    @GetMapping("/{orderId}/pay/{paymentNo}")
    public ApiResponse<Void> updateOrderPaymentStatus(@PathVariable String orderId,
                                                      @PathVariable String paymentNo) {
        orderService.updatePaymentStatus(orderId, paymentNo);
        return ApiResponse.success("订单支付状态已更新");
    }
    
    /**
     * 订单发货（供商家使用）
     */
    @PostMapping("/{orderId}/ship")
    public ApiResponse<String> shipOrder(@PathVariable String orderId,
                                        @RequestParam String courierCompany,
                                        @RequestParam String trackingNo) {
        orderService.shipOrder(orderId, courierCompany, trackingNo);
        return ApiResponse.success("订单已发货");
    }
}
