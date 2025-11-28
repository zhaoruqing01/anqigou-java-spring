package com.anqigou.order.service;

import java.util.List;

import com.anqigou.order.entity.AfterSaleApply;
import com.anqigou.order.entity.AfterSaleLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 售后服务接口
 */
public interface AfterSaleService {
    
    /**
     * 创建售后申请
     * 
     * @param afterSaleApply 售后申请信息
     * @return 售后申请ID
     */
    String createAfterSaleApply(AfterSaleApply afterSaleApply);
    
    /**
     * 获取售后申请详情
     * 
     * @param afterSaleId 售后申请ID
     * @param userId 用户ID
     * @return 售后申请详情
     */
    AfterSaleApply getAfterSaleApply(String afterSaleId, String userId);
    
    /**
     * 获取用户售后申请列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 售后申请列表
     */
    Page<AfterSaleApply> getUserAfterSaleList(String userId, int pageNum, int pageSize);
    
    /**
     * 获取商家售后申请列表
     * 
     * @param sellerId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 售后状态
     * @return 售后申请列表
     */
    Page<AfterSaleApply> getSellerAfterSaleList(String sellerId, int pageNum, int pageSize, String status);
    
    /**
     * 商家审核售后申请
     * 
     * @param afterSaleId 售后申请ID
     * @param sellerId 商家ID
     * @param approved 是否同意
     * @param remark 备注
     */
    void auditAfterSaleApply(String afterSaleId, String sellerId, boolean approved, String remark);
    
    /**
     * 用户取消售后申请
     * 
     * @param afterSaleId 售后申请ID
     * @param userId 用户ID
     */
    void cancelAfterSaleApply(String afterSaleId, String userId);
    
    /**
     * 用户填写退货物流信息
     * 
     * @param afterSaleId 售后申请ID
     * @param userId 用户ID
     * @param expressCompany 快递公司
     * @param expressNo 快递单号
     */
    void fillReturnExpressInfo(String afterSaleId, String userId, String expressCompany, String expressNo);
    
    /**
     * 商家确认收到退货
     * 
     * @param afterSaleId 售后申请ID
     * @param sellerId 商家ID
     */
    void confirmReturnGoods(String afterSaleId, String sellerId);
    
    /**
     * 商家处理退款
     * 
     * @param afterSaleId 售后申请ID
     * @param sellerId 商家ID
     * @param refundAmount 退款金额
     */
    void processRefund(String afterSaleId, String sellerId, Long refundAmount);
    
    /**
     * 完成售后申请
     * 
     * @param afterSaleId 售后申请ID
     */
    void completeAfterSaleApply(String afterSaleId);
    
    /**
     * 获取售后申请日志
     * 
     * @param afterSaleId 售后申请ID
     * @return 售后申请日志列表
     */
    List<AfterSaleLog> getAfterSaleLogs(String afterSaleId);
}