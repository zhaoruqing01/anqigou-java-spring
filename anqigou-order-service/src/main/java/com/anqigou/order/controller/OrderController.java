package com.anqigou.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
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
    public ApiResponse<String> createOrder(@RequestParam String userId,
                                          @RequestParam String addressId,
                                          @RequestParam(defaultValue = "1") int paymentMethod,
                                          @RequestParam(required = false) String remark) {
        String orderId = orderService.createOrder(userId, addressId, paymentMethod, remark);
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
}
