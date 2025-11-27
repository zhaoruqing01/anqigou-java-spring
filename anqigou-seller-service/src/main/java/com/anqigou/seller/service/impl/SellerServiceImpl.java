package com.anqigou.seller.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.exception.BizException;
import com.anqigou.seller.entity.Seller;
import com.anqigou.seller.mapper.SellerMapper;
import com.anqigou.seller.service.SellerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 商家服务实现
 */
@Service
@Slf4j
public class SellerServiceImpl implements SellerService {
    
    @Autowired
    private SellerMapper sellerMapper;
    
    @Override
    @Transactional
    public void registerSeller(String userId, String shopName, String licenseNo, String licenseImage) {
        // 检查是否已存在
        QueryWrapper<Seller> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (sellerMapper.selectCount(wrapper) > 0) {
            throw new BizException(400, "该用户已申请商家认证");
        }
        
        Seller seller = Seller.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .shopName(shopName)
                .licenseNo(licenseNo)
                .licenseImage(licenseImage)
                .status("pending")  // 待审核
                .level(1)  // 初级
                .creditScore(100.0)
                .totalSales(0L)
                .totalOrders(0)
                .goodCommentRate(100.0)
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        sellerMapper.insert(seller);
        log.info("商家注册申请已提交：userId={}, shopName={}", userId, shopName);
    }
    
    @Override
    public Object getSellerInfo(String sellerId) {
        Seller seller = sellerMapper.selectById(sellerId);
        if (seller == null) {
            throw new BizException(404, "商家信息不存在");
        }
        
        // 检查审核状态
        if ("rejected".equals(seller.getStatus())) {
            throw new BizException(403, "您的商家申请已被拒绝");
        }
        
        if (!"approved".equals(seller.getStatus())) {
            throw new BizException(403, "商家认证尚未通过审核");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("sellerId", seller.getId());
        result.put("shopName", seller.getShopName());
        result.put("shopLogo", seller.getShopLogo());
        result.put("shopDescription", seller.getShopDescription());
        result.put("status", seller.getStatus());
        result.put("level", seller.getLevel());
        result.put("creditScore", seller.getCreditScore());
        result.put("totalSales", seller.getTotalSales());
        result.put("totalOrders", seller.getTotalOrders());
        result.put("goodCommentRate", seller.getGoodCommentRate());
        result.put("phone", seller.getPhone());
        result.put("email", seller.getEmail());
        result.put("businessAddress", seller.getBusinessAddress());
        result.put("createTime", seller.getCreateTime() != null ? seller.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        
        return result;
    }
    
    @Override
    @Transactional
    public void updateSellerInfo(String sellerId, Map<String, Object> sellerInfo) {
        Seller seller = sellerMapper.selectById(sellerId);
        if (seller == null) {
            throw new BizException(404, "商家信息不存在");
        }
        
        if (sellerInfo.containsKey("shopName")) {
            seller.setShopName((String) sellerInfo.get("shopName"));
        }
        if (sellerInfo.containsKey("shopLogo")) {
            seller.setShopLogo((String) sellerInfo.get("shopLogo"));
        }
        if (sellerInfo.containsKey("shopDescription")) {
            seller.setShopDescription((String) sellerInfo.get("shopDescription"));
        }
        if (sellerInfo.containsKey("phone")) {
            seller.setPhone((String) sellerInfo.get("phone"));
        }
        if (sellerInfo.containsKey("email")) {
            seller.setEmail((String) sellerInfo.get("email"));
        }
        if (sellerInfo.containsKey("businessAddress")) {
            seller.setBusinessAddress((String) sellerInfo.get("businessAddress"));
        }
        
        seller.setUpdateTime(LocalDateTime.now());
        sellerMapper.updateById(seller);
        log.info("商家信息已更新：sellerId={}", sellerId);
    }
    
    @Override
    public Object getSellerOrders(String sellerId, int pageNum, int pageSize) {
        // 从数据库查询商家的订单（实际环境中应使用Feign调用订单服务）
        // 临时模拟数据
        Page<Object> page = new Page<>(pageNum, pageSize);
        page.setTotal(5);
        
        // TODO: 实际环境中调用订单服务 Feign 客户端获取掠梨商的订单
        List<Object> orders = new ArrayList<>();
        page.setRecords(orders);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    @Override
    @Transactional
    public void shipOrder(String orderId, String courierCompany, String trackingNo) {
        // 调用物流服务创建物流记录（Feign客户端）
        // TODO: 调用订单服务更新订单状态为"已发货"
        
        log.info("订单已发货：orderId={}, courierCompany={}, trackingNo={}", 
                orderId, courierCompany, trackingNo);
    }
    
    @Override
    public Object getSellerStatistics(String sellerId) {
        Seller seller = sellerMapper.selectById(sellerId);
        if (seller == null) {
            throw new BizException(404, "商家信息不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("todaySales", 0);  // TODO: 从订单服务统计今日销收
        result.put("totalSales", seller.getTotalSales() != null ? seller.getTotalSales() : 0);
        result.put("totalOrders", seller.getTotalOrders() != null ? seller.getTotalOrders() : 0);
        result.put("completedOrders", 0);  // TODO: 统计已完成订单
        result.put("pendingOrders", 0);  // TODO: 统计待处理订单
        result.put("creditScore", seller.getCreditScore() != null ? seller.getCreditScore() : 0);
        result.put("goodCommentRate", seller.getGoodCommentRate() != null ? seller.getGoodCommentRate() : 0);
        result.put("level", seller.getLevel() != null ? seller.getLevel() : 1);
        
        return result;
    }
}
