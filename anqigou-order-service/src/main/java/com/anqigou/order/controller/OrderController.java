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
    public ApiResponse<String> createOrder(@RequestAttribute(required = false) String userId,
                                          @RequestBody CreateOrderRequest request) {
        try {
            if (userId == null) {
                // 如果拦截器未生效，尝试从Header获取作为临时修复
                log.warn("UserId attribute missing, interceptor might not be working");
                return ApiResponse.failure(401, "User ID missing");
            }
            String orderId = orderService.createOrder(userId, request);
            return ApiResponse.success("订单创建成功", orderId);
        } catch (Exception e) {
            log.error("Create order failed", e);
            return ApiResponse.failure(500, "Create order failed: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<Object> getOrderDetail(@PathVariable String orderId,
                                             @RequestAttribute(required = false) String userId) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            Object orderDetail = orderService.getOrderDetail(orderId, userId);
            return ApiResponse.success(orderDetail);
        } catch (Exception e) {
            log.error("Get order detail failed", e);
            return ApiResponse.failure(500, "Get order detail failed: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<Object>> getOrderList(@RequestAttribute(required = false) String userId,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false) String status) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            Page<Object> orders = orderService.getOrderList(userId, pageNum, pageSize, status);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            log.error("Get order list failed", e);
            return ApiResponse.failure(500, "Get order list failed: " + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable String orderId,
                                        @RequestAttribute(required = false) String userId) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            orderService.cancelOrder(orderId, userId);
            return ApiResponse.success("订单取消成功");
        } catch (Exception e) {
            log.error("Cancel order failed", e);
            return ApiResponse.failure(500, "Cancel order failed: " + e.getMessage());
        }
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/{orderId}/confirm-receipt")
    public ApiResponse<String> confirmReceipt(@PathVariable String orderId,
                                           @RequestAttribute(required = false) String userId) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            orderService.confirmReceipt(orderId, userId);
            return ApiResponse.success("订单已签收");
        } catch (Exception e) {
            log.error("Confirm receipt failed", e);
            return ApiResponse.failure(500, "Confirm receipt failed: " + e.getMessage());
        }
    }
    
    /**
     * 更新订单支付状态（供支付服务调用）
     */
    @GetMapping("/{orderId}/pay/{paymentNo}")
    public ApiResponse<Void> updateOrderPaymentStatus(@PathVariable String orderId,
                                                      @PathVariable String paymentNo) {
        try {
            orderService.updatePaymentStatus(orderId, paymentNo);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Update payment status failed", e);
            return ApiResponse.failure(500, "Update payment status failed: " + e.getMessage());
        }
    }
    
    /**
     * 订单发货（供商家使用）
     */
    @PostMapping("/{orderId}/ship")
    public ApiResponse<String> shipOrder(@PathVariable String orderId,
                                        @RequestParam String courierCompany,
                                        @RequestParam String trackingNo) {
        try {
            orderService.shipOrder(orderId, courierCompany, trackingNo);
            return ApiResponse.success("订单已发货");
        } catch (Exception e) {
            log.error("Ship order failed", e);
            return ApiResponse.failure(500, "Ship order failed: " + e.getMessage());
        }
    }
}
