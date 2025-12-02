package com.anqigou.seller.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.seller.service.SellerService;

import lombok.extern.slf4j.Slf4j;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/seller")
@Validated
@Slf4j
public class SellerController {
    
    @Autowired
    private SellerService sellerService;
    
    /**
     * 商家注册申请
     */
    @PostMapping("/register")
    public ApiResponse<String> registerSeller(@RequestParam String shopName,
                                           @RequestParam String licenseNo,
                                           @RequestParam String licenseImage,
                                           @RequestAttribute String userId) {
        sellerService.registerSeller(userId, shopName, licenseNo, licenseImage);
        return ApiResponse.success("商家申请已提交，请等待审核");
    }
    
    /**
     * 获取商家信息
     */
    @GetMapping("/{sellerId}/info")
    public ApiResponse<Object> getSellerInfo(@PathVariable String sellerId) {
        Object sellerInfo = sellerService.getSellerInfo(sellerId);
        return ApiResponse.success(sellerInfo);
    }
    
    /**
     * 更新商家信息
     */
    @PutMapping("/{sellerId}/info")
    public ApiResponse<String> updateSellerInfo(@PathVariable String sellerId,
                                             @RequestBody Map<String, Object> sellerInfo) {
        sellerService.updateSellerInfo(sellerId, sellerInfo);
        return ApiResponse.success("商家信息已更新");
    }
    
    /**
     * 获取商家订单列表
     */
    @GetMapping("/{sellerId}/orders")
    public ApiResponse<Object> getSellerOrders(@PathVariable String sellerId,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "20") int pageSize) {
        Object orders = sellerService.getSellerOrders(sellerId, pageNum, pageSize);
        return ApiResponse.success(orders);
    }
    
    /**
     * 发货
     */
    @PostMapping("/orders/{orderId}/ship")
    public ApiResponse<String> shipOrder(@PathVariable String orderId,
                                      @RequestParam String courierCompany,
                                      @RequestParam String trackingNo,
                                      @RequestAttribute String userId) {
        sellerService.shipOrder(orderId, courierCompany, trackingNo);
        return ApiResponse.success("发货成功");
    }
    
    /**
     * 获取数据统计
     */
    @GetMapping("/{sellerId}/statistics")
    public ApiResponse<Object> getSellerStatistics(@PathVariable String sellerId) {
        Object statistics = sellerService.getSellerStatistics(sellerId);
        return ApiResponse.success(statistics);
    }
}
