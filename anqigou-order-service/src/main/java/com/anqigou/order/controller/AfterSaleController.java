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
import com.anqigou.order.entity.AfterSaleApply;
import com.anqigou.order.service.AfterSaleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 售后控制器
 */
@RestController
@RequestMapping("/api/after-sale")
@Validated
@Slf4j
public class AfterSaleController {
    
    @Autowired
    private AfterSaleService afterSaleService;
    
    /**
     * 创建售后申请
     */
    @PostMapping("/apply")
    public ApiResponse<String> createAfterSaleApply(@RequestBody AfterSaleApply afterSaleApply,
                                                  @RequestAttribute String userId) {
        afterSaleApply.setUserId(userId);
        String afterSaleId = afterSaleService.createAfterSaleApply(afterSaleApply);
        return ApiResponse.success("售后申请创建成功", afterSaleId);
    }
    
    /**
     * 获取售后申请详情
     */
    @GetMapping("/{afterSaleId}")
    public ApiResponse<AfterSaleApply> getAfterSaleApply(@PathVariable String afterSaleId,
                                                      @RequestAttribute String userId) {
        AfterSaleApply afterSaleApply = afterSaleService.getAfterSaleApply(afterSaleId, userId);
        return ApiResponse.success(afterSaleApply);
    }
    
    /**
     * 获取用户售后申请列表
     */
    @GetMapping("/user/list")
    public ApiResponse<Page<AfterSaleApply>> getUserAfterSaleList(@RequestAttribute String userId,
                                                               @RequestParam(defaultValue = "1") int pageNum,
                                                               @RequestParam(defaultValue = "10") int pageSize) {
        Page<AfterSaleApply> afterSaleList = afterSaleService.getUserAfterSaleList(userId, pageNum, pageSize);
        return ApiResponse.success(afterSaleList);
    }
    
    /**
     * 取消售后申请
     */
    @PostMapping("/{afterSaleId}/cancel")
    public ApiResponse<String> cancelAfterSaleApply(@PathVariable String afterSaleId,
                                                 @RequestAttribute String userId) {
        afterSaleService.cancelAfterSaleApply(afterSaleId, userId);
        return ApiResponse.success("售后申请已取消");
    }
    
    /**
     * 填写退货物流信息
     */
    @PostMapping("/{afterSaleId}/fill-express")
    public ApiResponse<String> fillReturnExpressInfo(@PathVariable String afterSaleId,
                                                  @RequestAttribute String userId,
                                                  @RequestParam String expressCompany,
                                                  @RequestParam String expressNo) {
        afterSaleService.fillReturnExpressInfo(afterSaleId, userId, expressCompany, expressNo);
        return ApiResponse.success("物流信息填写成功");
    }
    
    /**
     * 商家审核售后申请
     */
    @PostMapping("/{afterSaleId}/audit")
    public ApiResponse<String> auditAfterSaleApply(@PathVariable String afterSaleId,
                                                @RequestAttribute String sellerId,
                                                @RequestParam boolean approved,
                                                @RequestParam(required = false) String remark) {
        afterSaleService.auditAfterSaleApply(afterSaleId, sellerId, approved, remark);
        return ApiResponse.success("审核成功");
    }
    
    /**
     * 商家确认收到退货
     */
    @PostMapping("/{afterSaleId}/confirm-return")
    public ApiResponse<String> confirmReturnGoods(@PathVariable String afterSaleId,
                                               @RequestAttribute String sellerId) {
        afterSaleService.confirmReturnGoods(afterSaleId, sellerId);
        return ApiResponse.success("确认收到退货");
    }
    
    /**
     * 商家处理退款
     */
    @PostMapping("/{afterSaleId}/refund")
    public ApiResponse<String> processRefund(@PathVariable String afterSaleId,
                                           @RequestAttribute String sellerId,
                                           @RequestParam Long refundAmount) {
        afterSaleService.processRefund(afterSaleId, sellerId, refundAmount);
        return ApiResponse.success("退款处理成功");
    }
}
