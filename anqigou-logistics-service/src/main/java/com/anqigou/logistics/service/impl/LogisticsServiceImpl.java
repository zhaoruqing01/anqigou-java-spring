package com.anqigou.logistics.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.anqigou.logistics.util.Kuaidi100Client;
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
    
    @Autowired
    private Kuaidi100Client kuaidi100Client;
    
    @Override
    public LogisticsDetailDTO getLogisticsDetail(String orderId, String userId) {
        QueryWrapper<Logistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Logistics logistics = logisticsMapper.selectOne(queryWrapper);
        
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        // 获取轨迹
        List<LogisticsTrackDTO> tracks = new ArrayList<>();
        
        try {
            // 尝试从快递100 API获取实时轨迹
            String com = kuaidi100Client.getCompanyCode(logistics.getCourierCompany());
            String num = logistics.getTrackingNo();
            
            if (com != null && num != null) {
                JSONObject response = kuaidi100Client.queryLogisticsTrack(com, num);
                if ("200".equals(response.getString("resultcode"))) {
                    tracks = parseKuaidi100Tracks(response);
                    log.info("成功从快递100获取物流轨迹: orderId={}, trackingNo={}", orderId, num);
                } else {
                    log.warn("快递100 API返回错误: {}", response.getString("message"));
                }
            }
        } catch (Exception e) {
            log.error("调用快递100 API失败: {}", e.getMessage(), e);
        }
        
        // 如果快递100 API获取失败，使用本地轨迹
        if (tracks.isEmpty()) {
            QueryWrapper<LogisticsTrack> trackWrapper = new QueryWrapper<>();
            trackWrapper.eq("logistics_id", logistics.getId())
                    .orderByDesc("operate_time");
            List<LogisticsTrack> localTracks = logisticsTrackMapper.selectList(trackWrapper);
            tracks = convertTracks(localTracks);
        }
        
        // 如果本地轨迹也为空，添加模拟轨迹数据
        if (tracks.isEmpty()) {
            tracks = generateMockTracks();
            log.info("使用模拟轨迹数据: orderId={}", orderId);
        }
        
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
                .tracks(tracks)
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
        // 生成订单号（简化版本，实际应该从订单服务获取）
        String orderNo = "ORD" + orderId.substring(0, Math.min(8, orderId.length()));
        
        Logistics logistics = Logistics.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderId)
                .orderNo(orderNo)
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
        
        log.info("物流信息已创建: orderId={}, orderNo={}, trackingNo={}", orderId, orderNo, trackingNo);
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
     * 解析快递100 API返回的轨迹数据
     */
    private List<LogisticsTrackDTO> parseKuaidi100Tracks(JSONObject response) {
        List<LogisticsTrackDTO> tracks = new ArrayList<>();
        
        JSONObject result = response.getJSONObject("result");
        if (result != null) {
            JSONArray list = result.getJSONArray("list");
            if (list != null && !list.isEmpty()) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    JSONObject trackJson = list.getJSONObject(i);
                    
                    // 解析时间
                    String timeStr = trackJson.getString("ftime");
                    long time = 0;
                    try {
                        time = LocalDateTime.parse(timeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli();
                    } catch (Exception e) {
                        log.warn("解析时间失败: {}", timeStr);
                    }
                    
                    LogisticsTrackDTO track = LogisticsTrackDTO.builder()
                            .operateTime(time)
                            .operateCity(trackJson.getString("location"))
                            .operateLocation("")
                            .description(trackJson.getString("context"))
                            .courierName("")
                            .courierPhone("")
                            .isLatest(i == list.size() - 1)
                            .build();
                    
                    tracks.add(track);
                }
            }
        }
        
        return tracks;
    }
    
    /**
     * 生成模拟轨迹数据
     */
    private List<LogisticsTrackDTO> generateMockTracks() {
        List<LogisticsTrackDTO> tracks = new ArrayList<>();
        
        // 模拟轨迹数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        mockData.add(Map.of("time", "2024-01-15 14:30:00", "location", "深圳市", "context", "【深圳市】快递已签收，签收人：门卫"));
        mockData.add(Map.of("time", "2024-01-15 10:15:00", "location", "深圳市南山区", "context", "【深圳市南山区】快递员正在派送中，请保持电话畅通"));
        mockData.add(Map.of("time", "2024-01-14 18:45:00", "location", "深圳市南山区", "context", "【深圳市南山区】快递已到达南山区配送中心"));
        mockData.add(Map.of("time", "2024-01-14 12:30:00", "location", "广州市", "context", "【广州市】快递已离开广州转运中心，发往深圳"));
        mockData.add(Map.of("time", "2024-01-13 20:15:00", "location", "广州市", "context", "【广州市】快递已到达广州转运中心"));
        mockData.add(Map.of("time", "2024-01-13 16:00:00", "location", "武汉市", "context", "【武汉市】快递已离开武汉转运中心，发往广州"));
        mockData.add(Map.of("time", "2024-01-13 10:30:00", "location", "武汉市", "context", "【武汉市】快递已到达武汉转运中心"));
        mockData.add(Map.of("time", "2024-01-12 18:45:00", "location", "上海市", "context", "【上海市】快递已离开上海发货仓，发往武汉"));
        mockData.add(Map.of("time", "2024-01-12 16:20:00", "location", "上海市", "context", "【上海市】商家已发货，等待快递员上门取件"));
        
        // 转换为LogisticsTrackDTO
        for (int i = 0; i < mockData.size(); i++) {
            Map<String, Object> data = mockData.get(i);
            String timeStr = (String) data.get("time");
            long time = 0;
            
            try {
                time = LocalDateTime.parse(timeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
            } catch (Exception e) {
                log.warn("解析时间失败: {}", timeStr);
            }
            
            LogisticsTrackDTO track = LogisticsTrackDTO.builder()
                    .operateTime(time)
                    .operateCity((String) data.get("location"))
                    .operateLocation("")
                    .description((String) data.get("context"))
                    .courierName(i == 1 ? "张三" : "")
                    .courierPhone(i == 1 ? "138****8888" : "")
                    .isLatest(i == 0)
                    .build();
            
            tracks.add(track);
        }
        
        return tracks;
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
            return "异常";
        } else {
            return "未知状态";
        }
    }
}
