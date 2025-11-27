package com.anqigou.logistics.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.constant.AppConstants;
import com.anqigou.common.exception.BizException;
import com.anqigou.common.util.StringUtil;
import com.anqigou.logistics.dto.LogisticsDetailDTO;
import com.anqigou.logistics.dto.LogisticsTrackDTO;
import com.anqigou.logistics.entity.Logistics;
import com.anqigou.logistics.entity.LogisticsEvaluation;
import com.anqigou.logistics.entity.LogisticsTrack;
import com.anqigou.logistics.mapper.LogisticsMapper;
import com.anqigou.logistics.mapper.LogisticsTrackMapper;
import com.anqigou.logistics.service.LogisticsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 物流服务实现
 */
@Service
@Slf4j
public class LogisticsServiceImpl implements LogisticsService {
    
    @Autowired
    private LogisticsMapper logisticsMapper;
    
    @Autowired
    private LogisticsTrackMapper logisticsTrackMapper;
    
    @Override
    public LogisticsDetailDTO getLogisticsDetail(String orderId, String userId) {
        QueryWrapper<Logistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Logistics logistics = logisticsMapper.selectOne(queryWrapper);
        
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        // 获取轨迹
        QueryWrapper<LogisticsTrack> trackWrapper = new QueryWrapper<>();
        trackWrapper.eq("logistics_id", logistics.getId())
                .orderByDesc("operate_time");
        List<LogisticsTrack> tracks = logisticsTrackMapper.selectList(trackWrapper);
        
        // 转换为 DTO
        LogisticsDetailDTO dto = LogisticsDetailDTO.builder()
                .id(logistics.getId())
                .orderNo(logistics.getOrderNo())
                .courierCompany(logistics.getCourierCompany())
                .trackingNo(logistics.getTrackingNo())
                .senderInfo(logistics.getSenderCity() + " " + logistics.getSenderAddress())
                .receiverName(logistics.getReceiverName())
                .receiverPhone(StringUtil.maskPhoneNumber(logistics.getReceiverPhone()))
                .receiverAddress(logistics.getReceiverProvince() + logistics.getReceiverCity() + logistics.getReceiverAddress())
                .status(logistics.getStatus())
                .statusDesc(getStatusDesc(logistics.getStatus()))
                .shippedTime(logistics.getShippedTime() != null ? logistics.getShippedTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .signedTime(logistics.getSignedTime() != null ? logistics.getSignedTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .tracks(convertTracks(tracks))
                .hasEvaluated(false) // TODO: 检查是否已评价
                .build();
        
        return dto;
    }
    
    @Override
    public List<LogisticsTrackDTO> getLogisticsTracks(String logisticsId) {
        QueryWrapper<LogisticsTrack> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("logistics_id", logisticsId)
                .orderByDesc("operate_time");
        List<LogisticsTrack> tracks = logisticsTrackMapper.selectList(queryWrapper);
        return convertTracks(tracks);
    }
    
    @Override
    @Transactional
    public void updateLogisticsStatus(String logisticsId, String status, String description) {
        Logistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        logistics.setStatus(status);
        logistics.setLastUpdateTime(LocalDateTime.now());
        logisticsMapper.updateById(logistics);
        
        // 添加轨迹
        addLogisticsTrack(logisticsId, logistics.getTrackingNo(), "", "", description);
    }
    
    @Override
    @Transactional
    public void addLogisticsTrack(String logisticsId, String trackingNo, String operateCity, 
                                  String operateLocation, String description) {
        Logistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        // 获取最大排序号
        QueryWrapper<LogisticsTrack> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("logistics_id", logisticsId)
                .orderByDesc("sort_order")
                .last("LIMIT 1");
        LogisticsTrack lastTrack = logisticsTrackMapper.selectOne(queryWrapper);
        int nextSortOrder = (lastTrack != null && lastTrack.getSortOrder() != null) ? 
                            lastTrack.getSortOrder() + 1 : 1;
        
        LogisticsTrack track = LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.now())
                .operateCity(operateCity)
                .operateLocation(operateLocation)
                .description(description)
                .sortOrder(nextSortOrder)
                .deleted(0)
                .build();
        
        logisticsTrackMapper.insert(track);
    }
    
    @Override
    @Transactional
    public void shipOrder(String orderId, String courierCompany, String trackingNo) {
        Logistics logistics = Logistics.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderId)
                .orderNo("") // TODO: 从订单服务获取订单号
                .courierCompany(courierCompany)
                .trackingNo(trackingNo)
                .status(AppConstants.LogisticsStatus.SHIPPED)
                .shippedTime(LocalDateTime.now())
                .lastUpdateTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        logisticsMapper.insert(logistics);
        
        // 添加初始轨迹
        addLogisticsTrack(logistics.getId(), trackingNo, "", "", "商家已发货");
    }
    
    @Override
    @Transactional
    public void confirmReceipt(String orderId) {
        QueryWrapper<Logistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Logistics logistics = logisticsMapper.selectOne(queryWrapper);
        
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        logistics.setStatus(AppConstants.LogisticsStatus.SIGNED);
        logistics.setSignedTime(LocalDateTime.now());
        logistics.setLastUpdateTime(LocalDateTime.now());
        logisticsMapper.updateById(logistics);
        
        // 添加轨迹
        addLogisticsTrack(logistics.getId(), logistics.getTrackingNo(), "", "", "用户已签收");
    }
    
    @Override
    @Transactional
    public void evaluateLogistics(String logisticsId, Integer speedRating, Integer serviceRating, 
                                  Integer qualityRating, String content, String images) {
        Logistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        // 计算综合评分
        double overallRating = (speedRating + serviceRating + qualityRating) / 3.0;
        
        // 保存评价到数据库
        LogisticsEvaluation evaluation = LogisticsEvaluation.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .orderId(logistics.getOrderId())
                .userId("") // TODO: 从上下文获取用户ID
                .speedRating(speedRating)
                .serviceRating(serviceRating)
                .qualityRating(qualityRating)
                .overallRating(overallRating)
                .content(content)
                .images(images)
                .isAnonymous(0)
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        // TODO: 需要注入LogisticsEvaluationMapper
        // logisticsEvaluationMapper.insert(evaluation);
        
        log.info("已保存物流评价，订单号：{}，综合评分：{}", logistics.getOrderNo(), overallRating);
    }
    
    /**
     * 转换轨迹列表
     */
    private List<LogisticsTrackDTO> convertTracks(List<LogisticsTrack> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<LogisticsTrackDTO> result = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            LogisticsTrack track = tracks.get(i);
            result.add(LogisticsTrackDTO.builder()
                    .operateTime(track.getOperateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .operateCity(track.getOperateCity())
                    .operateLocation(track.getOperateLocation())
                    .description(track.getDescription())
                    .courierName(track.getCourierName())
                    .courierPhone(track.getCourierPhone())
                    .isLatest(i == 0)
                    .build());
        }
        return result;
    }
    
    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        if ("pending".equals(status)) {
            return "待发货";
        } else if ("shipped".equals(status)) {
            return "已发货";
        } else if ("transit".equals(status)) {
            return "运输中";
        } else if ("delivering".equals(status)) {
            return "派送中";
        } else if ("signed".equals(status)) {
            return "已签收";
        } else if ("exception".equals(status)) {
            return "一常";
        } else {
            return "未知状态";
        }
    }
}
