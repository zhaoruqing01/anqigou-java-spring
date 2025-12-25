package com.anqigou.logistics.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anqigou.common.constant.AppConstants;
import com.anqigou.common.exception.BizException;
import com.anqigou.common.response.ApiResponse;
import com.anqigou.common.util.StringUtil;
import com.anqigou.logistics.client.OrderServiceClient;
import com.anqigou.logistics.dto.LogisticsDetailDTO;
import com.anqigou.logistics.dto.LogisticsTrackDTO;
import com.anqigou.logistics.dto.OrderInfoDTO;
import com.anqigou.logistics.entity.Logistics;
import com.anqigou.logistics.entity.LogisticsEvaluation;
import com.anqigou.logistics.entity.LogisticsTrack;
import com.anqigou.logistics.mapper.LogisticsEvaluationMapper;
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
    private LogisticsEvaluationMapper logisticsEvaluationMapper;
    
    @Autowired
    private Kuaidi100Client kuaidi100Client;
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    // 快递公司列表，用于随机选择
    private static final List<String> COURIER_COMPANIES = List.of("顺丰速递", "圆通速递", "中通快递", "申通快递", "韵达快递");
    
    // 发件城市列表，用于随机生成发件地址
    private static final List<String> SENDER_CITIES = List.of(
        "广东省深圳市", "上海市", "北京市", "浙江省杭州市", "江苏省南京市",
        "四川省成都市", "湖北省武汉市", "陕西省西安市", "山东省济南市", "广东省广州市"
    );
    
    // 随机生成器
    private static final Random RANDOM = new Random();
    
    @Override
    public LogisticsDetailDTO getLogisticsDetail(String orderId, String userId) {
        Logistics logistics = null;
        
        // 先尝试根据订单ID查询
        QueryWrapper<Logistics> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("order_id", orderId);
        logistics = logisticsMapper.selectOne(orderQueryWrapper);
        
        // 如果没有找到，动态生成物流信息
        if (logistics == null) {
            log.info("未找到物流信息，动态生成: orderId={}", orderId);
            logistics = generateDynamicLogistics(orderId, userId);
        }
        
        // 确保物流信息不为空
        if (logistics == null) {
            throw new BizException(500, "生成物流信息失败");
        }
        
        // 获取轨迹
        List<LogisticsTrackDTO> tracks = new ArrayList<>();
        
        // 从数据库获取轨迹
        QueryWrapper<LogisticsTrack> trackWrapper = new QueryWrapper<>();
        trackWrapper.eq("logistics_id", logistics.getId())
                .orderByDesc("operate_time");
        List<LogisticsTrack> localTracks = logisticsTrackMapper.selectList(trackWrapper);
        
        // 如果没有轨迹，生成动态轨迹
        if (localTracks == null || localTracks.isEmpty()) {
            log.info("未找到轨迹，生成动态轨迹: logisticsId={}", logistics.getId());
            createDynamicTracks(logistics);
            // 重新查询轨迹
            localTracks = logisticsTrackMapper.selectList(trackWrapper);
            // 如果还是没有轨迹，直接生成模拟轨迹
            if (localTracks == null || localTracks.isEmpty()) {
                log.warn("生成动态轨迹失败，使用模拟轨迹: logisticsId={}", logistics.getId());
                tracks = generateMockTracks();
            } else {
                tracks = convertTracks(localTracks);
            }
        } else {
            tracks = convertTracks(localTracks);
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
        
        log.info("返回物流详情: orderId={}, logisticsId={}, trackCount={}", orderId, logistics.getId(), tracks.size());
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
        // 先尝试根据订单ID查询
        QueryWrapper<Logistics> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("order_id", orderId);
        Logistics existingLogistics = logisticsMapper.selectOne(orderQueryWrapper);
        
        if (existingLogistics != null) {
            // 如果已经存在，更新物流信息
            existingLogistics.setCourierCompany(courierCompany);
            existingLogistics.setTrackingNo(trackingNo);
            existingLogistics.setStatus(AppConstants.LogisticsStatus.SHIPPED);
            existingLogistics.setShippedTime(LocalDateTime.now());
            existingLogistics.setLastUpdateTime(LocalDateTime.now());
            logisticsMapper.updateById(existingLogistics);
            log.info("已更新物流信息: orderId={}, courierCompany={}, trackingNo={}", 
                    orderId, courierCompany, trackingNo);
        } else {
            // 从订单服务获取真实订单信息
            OrderInfoDTO orderInfo = null;
            try {
                log.info("开始调用订单服务获取订单信息: orderId={}", orderId);
                ApiResponse<OrderInfoDTO> orderResponse = orderServiceClient.getOrderInfo(orderId);
                log.info("订单服务返回结果: code={}, message={}, data={}", 
                        orderResponse.getCode(), orderResponse.getMessage(), orderResponse.getData());
                
                if (orderResponse.getCode() == 0 && orderResponse.getData() != null) {
                    orderInfo = orderResponse.getData();
                } else {
                    log.error("订单服务返回错误: code={}, message={}", orderResponse.getCode(), orderResponse.getMessage());
                    throw new BizException(404, "获取订单信息失败");
                }
            } catch (BizException e) {
                throw e;
            } catch (Exception e) {
                log.error("调用订单服务失败: orderId={}, error={}", orderId, e.getMessage(), e);
                throw new BizException(500, "调用订单服务失败: " + e.getMessage());
            }
            
            // 生成唯一的物流ID
            String logisticsId = UUID.randomUUID().toString();
            
            // 如果没有提供快递公司和单号，生成随机的
            if (courierCompany == null || courierCompany.isEmpty()) {
                courierCompany = COURIER_COMPANIES.get(RANDOM.nextInt(COURIER_COMPANIES.size()));
            }
            
            if (trackingNo == null || trackingNo.isEmpty()) {
                trackingNo = generateTrackingNo(courierCompany);
            }
            
            // 使用真实订单号
            String orderNo = orderInfo.getOrderNo();
            
            // 随机生成发件人地址
            String senderCity = SENDER_CITIES.get(RANDOM.nextInt(SENDER_CITIES.size()));
            String[] senderCityParts = senderCity.split("\\s*");
            String senderProvince = senderCityParts[0];
            
            // 随机生成发件人详细地址
            String[] senderAddresses = {
                "科技园A栋101室", "工业园区B区202号", "商业区C座303室", 
                "物流园D区404号", "电商产业园E栋505室"
            };
            String senderAddress = senderAddresses[RANDOM.nextInt(senderAddresses.length)];
            
            // 使用真实收件人信息
            String receiverName = orderInfo.getReceiverName();
            String receiverPhone = orderInfo.getReceiverPhone();
            String receiverProvince = orderInfo.getReceiverProvince();
            String receiverCity = orderInfo.getReceiverCity();
            String receiverAddress = orderInfo.getReceiverDistrict() + orderInfo.getReceiverDetailAddress();
            
            // 创建物流信息
            Logistics logistics = Logistics.builder()
                    .id(logisticsId)
                    .orderId(orderId)
                    .orderNo(orderNo)
                    .courierCompany(courierCompany)
                    .trackingNo(trackingNo)
                    // 发件人信息
                    .senderProvince(senderProvince)
                    .senderCity(senderCity)
                    .senderAddress(senderAddress)
                    // 收件人信息
                    .receiverName(receiverName)
                    .receiverPhone(receiverPhone)
                    .receiverProvince(receiverProvince)
                    .receiverCity(receiverCity)
                    .receiverAddress(receiverAddress)
                    // 物流状态
                    .status(AppConstants.LogisticsStatus.SHIPPED)
                    .shippedTime(LocalDateTime.now())
                    .lastUpdateTime(LocalDateTime.now())
                    .deleted(0)
                    .build();
            
            logisticsMapper.insert(logistics);
            
            // 生成初始物流轨迹
            createDynamicTracks(logistics);
            
            log.info("已创建新的物流信息: orderId={}, logisticsId={}, courierCompany={}, trackingNo={}, receiverName={}", 
                    orderId, logisticsId, courierCompany, trackingNo, receiverName);
        }
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
    public void evaluateLogistics(String logisticsId, String userId, Integer speedRating, Integer serviceRating, 
                                  Integer qualityRating, String content, String images, Integer isAnonymous) {
        Logistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new BizException(404, "物流信息不存在");
        }
        
        // 检查是否已经评价过
        QueryWrapper<LogisticsEvaluation> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("logistics_id", logisticsId)
                .eq("user_id", userId)
                .eq("deleted", 0);
        Long count = logisticsEvaluationMapper.selectCount(checkWrapper);
        if (count > 0) {
            throw new BizException(400, "您已评价过该物流，不能重复评价");
        }
        
        // 验证评分范围
        if (speedRating < 1 || speedRating > 5 || 
            serviceRating < 1 || serviceRating > 5 || 
            qualityRating < 1 || qualityRating > 5) {
            throw new BizException(400, "评分必须在1-5星之间");
        }
        
        // 计算综合评分
        double overallRating = (speedRating + serviceRating + qualityRating) / 3.0;
        
        // 保存评价到数据库
        LogisticsEvaluation evaluation = LogisticsEvaluation.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .orderId(logistics.getOrderId())
                .userId(userId)
                .speedRating(speedRating)
                .serviceRating(serviceRating)
                .qualityRating(qualityRating)
                .overallRating(overallRating)
                .content(content)
                .images(images)
                .isAnonymous(isAnonymous != null ? isAnonymous : 0)
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        logisticsEvaluationMapper.insert(evaluation);
        
        log.info("物流评价保存成功: logisticsId={}, orderId={}, userId={}, overallRating={}", 
                logisticsId, logistics.getOrderId(), userId, overallRating);
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
     * 动态生成物流信息
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 生成的物流信息
     */
    private Logistics generateDynamicLogistics(String orderId, String userId) {
        // 从订单服务获取真实订单信息
        OrderInfoDTO orderInfo = null;
        try {
            log.info("开始调用订单服务获取订单信息: orderId={}", orderId);
            ApiResponse<OrderInfoDTO> orderResponse = orderServiceClient.getOrderInfo(orderId);
            log.info("订单服务返回结果: code={}, message={}, data={}", 
                    orderResponse.getCode(), orderResponse.getMessage(), orderResponse.getData());
            
            if (orderResponse.getCode() == 0 && orderResponse.getData() != null) {
                orderInfo = orderResponse.getData();
            } else {
                log.error("订单服务返回错误: code={}, message={}", orderResponse.getCode(), orderResponse.getMessage());
                // 如果获取不到订单信息，使用默认值但记录错误
                throw new BizException(404, "获取订单信息失败");
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用订单服务失败: orderId={}, error={}", orderId, e.getMessage(), e);
            throw new BizException(500, "调用订单服务失败: " + e.getMessage());
        }
        
        // 生成唯一的物流ID
        String logisticsId = UUID.randomUUID().toString();
        
        // 随机选择快递公司
        String courierCompany = COURIER_COMPANIES.get(RANDOM.nextInt(COURIER_COMPANIES.size()));
        
        // 生成唯一的物流单号
        String trackingNo = generateTrackingNo(courierCompany);
        
        // 使用真实订单号
        String orderNo = orderInfo.getOrderNo();
        
        // 随机生成发件人地址
        String senderCity = SENDER_CITIES.get(RANDOM.nextInt(SENDER_CITIES.size()));
        String[] senderCityParts = senderCity.split("\\s*");
        String senderProvince = senderCityParts[0];
        
        // 随机生成发件人详细地址
        String[] senderAddresses = {
            "科技园A栋101室", "工业园区B区202号", "商业区C座303室", 
            "物流园D区404号", "电商产业园E栋505室"
        };
        String senderAddress = senderAddresses[RANDOM.nextInt(senderAddresses.length)];
        
        // 使用真实收件人信息
        String receiverName = orderInfo.getReceiverName();
        String receiverPhone = orderInfo.getReceiverPhone();
        String receiverProvince = orderInfo.getReceiverProvince();
        String receiverCity = orderInfo.getReceiverCity();
        String receiverAddress = orderInfo.getReceiverDistrict() + orderInfo.getReceiverDetailAddress();
        
        // 随机选择物流状态（70%已签收，20%派送中，10%运输中）
        String[] statuses = {"signed", "signed", "signed", "signed", "signed", "signed", "signed", 
                           "delivering", "delivering", "transit"};
        String status = statuses[RANDOM.nextInt(statuses.length)];
        
        // 生成发货时间和签收时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime shippedTime = now.minusDays(RANDOM.nextInt(5) + 1); // 1-5天前发货
        LocalDateTime signedTime = null;
        
        if ("signed".equals(status)) {
            signedTime = shippedTime.plusDays(RANDOM.nextInt(3) + 1); // 发货后1-3天签收
        }
        
        // 构建物流信息
        Logistics logistics = Logistics.builder()
                .id(logisticsId)
                .orderId(orderId)
                .orderNo(orderNo)
                .courierCompany(courierCompany)
                .trackingNo(trackingNo)
                // 发件人信息
                .senderProvince(senderProvince)
                .senderCity(senderCity)
                .senderAddress(senderAddress)
                // 收件人信息
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverProvince(receiverProvince)
                .receiverCity(receiverCity)
                .receiverAddress(receiverAddress)
                // 物流状态
                .status(status)
                .shippedTime(shippedTime)
                .signedTime(signedTime)
                .lastUpdateTime(now)
                .deleted(0)
                .build();
        
        // 保存到数据库
        logisticsMapper.insert(logistics);
        log.info("动态生成了物流信息: orderId={}, logisticsId={}, courierCompany={}, trackingNo={}, status={}, receiverName={}", 
                orderId, logisticsId, courierCompany, trackingNo, status, receiverName);
        
        return logistics;
    }
    
    /**
     * 生成唯一的物流单号
     * @param courierCompany 快递公司
     * @return 物流单号
     */
    private String generateTrackingNo(String courierCompany) {
        // 根据不同快递公司生成不同格式的单号
        String prefix = "SF";
        if (courierCompany.contains("圆通")) {
            prefix = "YT";
        } else if (courierCompany.contains("中通")) {
            prefix = "ZT";
        } else if (courierCompany.contains("申通")) {
            prefix = "ST";
        } else if (courierCompany.contains("韵达")) {
            prefix = "YD";
        }
        
        // 生成12位数字
        String numbers = String.format("%012d", RANDOM.nextInt(1000000000));
        
        return prefix + numbers;
    }
    
    /**
     * 生成动态物流轨迹
     * @param logistics 物流信息
     */
    private void createDynamicTracks(Logistics logistics) {
        // 解析收货地址获取区县和详细地址
        String receiverDistrict = extractDistrict(logistics.getReceiverAddress());
        String receiverDetailAddress = extractDetailAddress(logistics.getReceiverAddress());
        
        // 使用 LogisticsTrackGenerator 生成完整详细的轨迹，使用真实的收件人信息
        List<LogisticsTrack> tracks = com.anqigou.logistics.util.LogisticsTrackGenerator.generateTracks(
            logistics.getId(),
            logistics.getTrackingNo(),
            logistics.getSenderCity(),
            logistics.getReceiverProvince(),
            logistics.getReceiverCity(),
            receiverDistrict,
            receiverDetailAddress,
            logistics.getReceiverName(),
            logistics.getReceiverPhone()
        );
        
        // 批量插入物流轨迹
        for (LogisticsTrack track : tracks) {
            logisticsTrackMapper.insert(track);
        }
        
        log.info("使用真实收件人信息生成了{}条物流轨迹: logisticsId={}, receiverName={}, receiverAddress={}{}{}", 
                tracks.size(), logistics.getId(), logistics.getReceiverName(),
                logistics.getReceiverProvince(), logistics.getReceiverCity(), logistics.getReceiverAddress());
    }
    
    /**
     * 从完整地址中提取区县信息
     * @param fullAddress 完整地址（不含省市）
     * @return 区县信息
     */
    private String extractDistrict(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }
        
        // 查找"区"或"县"
        int districtIdx = -1;
        if (fullAddress.contains("区")) {
            districtIdx = fullAddress.indexOf("区");
        } else if (fullAddress.contains("县")) {
            districtIdx = fullAddress.indexOf("县");
        }
        
        if (districtIdx > 0) {
            return fullAddress.substring(0, districtIdx + 1);
        }
        
        return "";
    }
    
    /**
     * 从完整地址中提取详细地址信息
     * @param fullAddress 完整地址（不含省市）
     * @return 详细地址信息
     */
    private String extractDetailAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return fullAddress;
        }
        
        // 查找"区"或"县"
        int districtIdx = -1;
        if (fullAddress.contains("区")) {
            districtIdx = fullAddress.indexOf("区");
        } else if (fullAddress.contains("县")) {
            districtIdx = fullAddress.indexOf("县");
        }
        
        if (districtIdx > 0 && districtIdx + 1 < fullAddress.length()) {
            return fullAddress.substring(districtIdx + 1);
        }
        
        return fullAddress;
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
