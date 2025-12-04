package com.anqigou.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.exception.BizException;
import com.anqigou.order.entity.AfterSaleApply;
import com.anqigou.order.entity.AfterSaleLog;
import com.anqigou.order.mapper.AfterSaleApplyMapper;
import com.anqigou.order.mapper.AfterSaleLogMapper;
import com.anqigou.order.service.AfterSaleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 售后服务实现类
 */
@Service
@Slf4j
public class AfterSaleServiceImpl implements AfterSaleService {
    
    @Autowired
    private AfterSaleApplyMapper afterSaleApplyMapper;
    
    @Autowired
    private AfterSaleLogMapper afterSaleLogMapper;
    
    @Override
    @Transactional
    public String createAfterSaleApply(AfterSaleApply afterSaleApply) {
        // 设置默认值
        afterSaleApply.setStatus("pending");
        afterSaleApply.setCreatedAt(LocalDateTime.now());
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApply.setDeleted(0);
        
        // 保存售后申请
        afterSaleApplyMapper.insert(afterSaleApply);
        
        // 添加售后日志
        addAfterSaleLog(afterSaleApply.getId(), afterSaleApply.getUserId(), "user", "create", "创建售后申请");
        
        log.info("创建售后申请成功，afterSaleId: {}, userId: {}", afterSaleApply.getId(), afterSaleApply.getUserId());
        return afterSaleApply.getId();
    }
    
    @Override
    public AfterSaleApply getAfterSaleApply(String afterSaleId, String userId) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 验证用户权限
        if (!afterSaleApply.getUserId().equals(userId)) {
            throw new BizException(403, "无权访问该售后申请");
        }
        
        return afterSaleApply;
    }
    
    @Override
    public Page<AfterSaleApply> getUserAfterSaleList(String userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<AfterSaleApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AfterSaleApply::getUserId, userId)
                .eq(AfterSaleApply::getDeleted, 0)
                .orderByDesc(AfterSaleApply::getCreatedAt);
        
        Page<AfterSaleApply> page = new Page<>(pageNum, pageSize);
        afterSaleApplyMapper.selectPage(page, queryWrapper);
        
        return page;
    }
    
    @Override
    public Page<AfterSaleApply> getSellerAfterSaleList(String sellerId, int pageNum, int pageSize, String status) {
        LambdaQueryWrapper<AfterSaleApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AfterSaleApply::getSellerId, sellerId)
                .eq(AfterSaleApply::getDeleted, 0)
                .orderByDesc(AfterSaleApply::getCreatedAt);
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(AfterSaleApply::getStatus, status);
        }
        
        Page<AfterSaleApply> page = new Page<>(pageNum, pageSize);
        afterSaleApplyMapper.selectPage(page, queryWrapper);
        
        return page;
    }
    
    @Override
    @Transactional
    public void auditAfterSaleApply(String afterSaleId, String sellerId, boolean approved, String remark) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 验证商家权限
        if (!afterSaleApply.getSellerId().equals(sellerId)) {
            throw new BizException(403, "无权处理该售后申请");
        }
        
        // 只有待审核状态的申请才能被审核
        if (!"pending".equals(afterSaleApply.getStatus())) {
            throw new BizException(400, "该售后申请已处理，无法重复审核");
        }
        
        // 更新售后状态
        afterSaleApply.setStatus(approved ? "approved" : "rejected");
        afterSaleApply.setSellerRemark(remark);
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApplyMapper.updateById(afterSaleApply);
        
        // 添加售后日志
        String logContent = approved ? "同意售后申请" : "拒绝售后申请";
        if (remark != null && !remark.isEmpty()) {
            logContent += "，备注：" + remark;
        }
        addAfterSaleLog(afterSaleId, sellerId, "seller", "audit", logContent);
        
        log.info("审核售后申请成功，afterSaleId: {}, sellerId: {}, approved: {}", afterSaleId, sellerId, approved);
    }
    
    @Override
    @Transactional
    public void cancelAfterSaleApply(String afterSaleId, String userId) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 验证用户权限
        if (!afterSaleApply.getUserId().equals(userId)) {
            throw new BizException(403, "无权取消该售后申请");
        }
        
        // 只有待审核和已同意状态的申请才能被取消
        if (!"pending".equals(afterSaleApply.getStatus()) && !"approved".equals(afterSaleApply.getStatus())) {
            throw new BizException(400, "该售后申请已处理，无法取消");
        }
        
        // 更新售后状态
        afterSaleApply.setStatus("cancelled");
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApplyMapper.updateById(afterSaleApply);
        
        // 添加售后日志
        addAfterSaleLog(afterSaleId, userId, "user", "cancel", "取消售后申请");
        
        log.info("取消售后申请成功，afterSaleId: {}, userId: {}", afterSaleId, userId);
    }
    
    @Override
    @Transactional
    public void fillReturnExpressInfo(String afterSaleId, String userId, String expressCompany, String expressNo) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 验证用户权限
        if (!afterSaleApply.getUserId().equals(userId)) {
            throw new BizException(403, "无权操作该售后申请");
        }
        
        // 只有已同意状态的退货退款申请才能填写物流信息
        if (!"approved".equals(afterSaleApply.getStatus()) || !"return_refund".equals(afterSaleApply.getType())) {
            throw new BizException(400, "该售后申请无法填写物流信息");
        }
        
        // 更新物流信息
        afterSaleApply.setExpressCompany(expressCompany);
        afterSaleApply.setExpressNo(expressNo);
        afterSaleApply.setStatus("processing");
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApplyMapper.updateById(afterSaleApply);
        
        // 添加售后日志
        addAfterSaleLog(afterSaleId, userId, "user", "fill_express", "填写退货物流信息，快递公司：" + expressCompany + "，快递单号：" + expressNo);
        
        log.info("填写退货物流信息成功，afterSaleId: {}, userId: {}", afterSaleId, userId);
    }
    
    @Override
    @Transactional
    public void confirmReturnGoods(String afterSaleId, String sellerId) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 验证商家权限
        if (!afterSaleApply.getSellerId().equals(sellerId)) {
            throw new BizException(403, "无权处理该售后申请");
        }
        
        // 只有处理中状态的退货退款申请才能确认收货
        if (!"processing".equals(afterSaleApply.getStatus()) || !"return_refund".equals(afterSaleApply.getType())) {
            throw new BizException(400, "该售后申请无法确认收货");
        }
        
        // 更新售后状态
        afterSaleApply.setStatus("processing");
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApplyMapper.updateById(afterSaleApply);
        
        // 添加售后日志
        addAfterSaleLog(afterSaleId, sellerId, "seller", "confirm_return", "确认收到退货");
        
        log.info("确认收到退货成功，afterSaleId: {}, sellerId: {}", afterSaleId, sellerId);
    }
    
    @Override
    @Transactional
    public void processRefund(String afterSaleId, String sellerId, Long refundAmount) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 验证商家权限
        if (!afterSaleApply.getSellerId().equals(sellerId)) {
            throw new BizException(403, "无权处理该售后申请");
        }
        
        // 只有已同意或处理中状态的申请才能处理退款
        if (!"approved".equals(afterSaleApply.getStatus()) && !"processing".equals(afterSaleApply.getStatus())) {
            throw new BizException(400, "该售后申请无法处理退款");
        }
        
        // 更新退款信息
        afterSaleApply.setRefundAmount(refundAmount);
        afterSaleApply.setStatus("completed");
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApplyMapper.updateById(afterSaleApply);
        
        // 添加售后日志
        addAfterSaleLog(afterSaleId, sellerId, "seller", "refund", "处理退款，退款金额：" + refundAmount);
        
        log.info("处理退款成功，afterSaleId: {}, sellerId: {}, refundAmount: {}", afterSaleId, sellerId, refundAmount);
    }
    
    @Override
    @Transactional
    public void completeAfterSaleApply(String afterSaleId) {
        AfterSaleApply afterSaleApply = afterSaleApplyMapper.selectById(afterSaleId);
        if (afterSaleApply == null) {
            throw new BizException(404, "售后申请不存在");
        }
        
        // 更新售后状态
        afterSaleApply.setStatus("completed");
        afterSaleApply.setUpdatedAt(LocalDateTime.now());
        afterSaleApplyMapper.updateById(afterSaleApply);
        
        // 添加售后日志
        addAfterSaleLog(afterSaleId, "system", "system", "complete", "售后申请完成");
        
        log.info("售后申请完成，afterSaleId: {}", afterSaleId);
    }
    
    @Override
    public List<AfterSaleLog> getAfterSaleLogs(String afterSaleId) {
        LambdaQueryWrapper<AfterSaleLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AfterSaleLog::getAfterSaleId, afterSaleId)
                .orderByDesc(AfterSaleLog::getCreatedAt);
        
        return afterSaleLogMapper.selectList(queryWrapper);
    }
    
    /**
     * 添加售后日志
     */
    private void addAfterSaleLog(String afterSaleId, String operatorId, String operatorType, String action, String content) {
        AfterSaleLog afterSaleLog = AfterSaleLog.builder()
                .afterSaleId(afterSaleId)
                .operatorId(operatorId)
                .operatorType(operatorType)
                .action(action)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        
        afterSaleLogMapper.insert(afterSaleLog);
    }
}