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
    
    @Override
    public LogisticsDetailDTO getLogisticsDetail(String orderId, String userId) {
        Logistics logistics = null;
        
        // 固定物流ID，使用数据库中实际存在的物流ID
        final String FIXED_LOGISTICS_ID = "d0ede6eb-67e4-4361-a64e-6545cf6c2920";
        
        // 先尝试根据订单ID查询
        QueryWrapper<Logistics> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("order_id", orderId);
        logistics = logisticsMapper.selectOne(orderQueryWrapper);
        
        // 如果没有找到，使用固定的物流ID查询
        if (logistics == null) {
            logistics = logisticsMapper.selectById(FIXED_LOGISTICS_ID);
        }
        
        // 如果还是没有找到，自动创建固定物流
        if (logistics == null) {
            String trackingNo = "YT8814325842976";
            String courierCompany = "圆通速递";
            
            // 生成订单号（简化版本，实际应该从订单服务获取）
            String orderNo = "ORD" + orderId.substring(0, Math.min(8, orderId.length()));
            
            logistics = Logistics.builder()
                    .id(FIXED_LOGISTICS_ID)
                    .orderId(orderId)
                    .orderNo(orderNo)
                    .courierCompany(courierCompany)
                    .trackingNo(trackingNo)
                    // 发件人信息
                    .senderProvince("广东省")
                    .senderCity("深圳市")
                    .senderAddress("南山区粤海街道科技园A栋101室")
                    // 收件人信息
                    .receiverName("测试用户")
                    .receiverPhone("13800138000")
                    .receiverProvince("上海市")
                    .receiverCity("上海市")
                    .receiverAddress("浦东新区陆家嘴街道世纪大道100号B栋501室")
                    // 物流状态
                    .status(AppConstants.LogisticsStatus.SIGNED)
                    .shippedTime(LocalDateTime.parse("2025-12-20 08:00:15", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .signedTime(LocalDateTime.parse("2025-12-21 12:05:28", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .lastUpdateTime(LocalDateTime.now())
                    .deleted(0)
                    .build();
            
            logisticsMapper.insert(logistics);
            log.info("自动创建了固定物流信息: orderId={}, orderNo={}, trackingNo={}", orderId, orderNo, trackingNo);
        }
        
        // 获取轨迹
        List<LogisticsTrackDTO> tracks = new ArrayList<>();
        
        // 从数据库获取轨迹
        QueryWrapper<LogisticsTrack> trackWrapper = new QueryWrapper<>();
        trackWrapper.eq("logistics_id", logistics.getId())
                .orderByDesc("operate_time");
        List<LogisticsTrack> localTracks = logisticsTrackMapper.selectList(trackWrapper);
        
        // 如果没有轨迹，生成模拟轨迹
        if (localTracks == null || localTracks.isEmpty()) {
            createInitialTracks(logistics.getId(), logistics.getTrackingNo());
            localTracks = logisticsTrackMapper.selectList(trackWrapper);
        }
        
        tracks = convertTracks(localTracks);
        
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
        // 使用固定物流ID，使用数据库中实际存在的物流ID
        final String FIXED_LOGISTICS_ID = "d0ede6eb-67e4-4361-a64e-6545cf6c2920";
        
        // 检查固定物流是否已经存在
        Logistics existingLogistics = logisticsMapper.selectById(FIXED_LOGISTICS_ID);
        if (existingLogistics == null) {
            // 如果不存在，创建固定物流
            trackingNo = "SF1234567890";
            courierCompany = "顺丰速递";
            
            // 生成订单号（简化版本，实际应该从订单服务获取）
            String orderNo = "ORD" + orderId.substring(0, Math.min(8, orderId.length()));
            
            Logistics logistics = Logistics.builder()
                    .id(FIXED_LOGISTICS_ID)
                    .orderId(orderId)
                    .orderNo(orderNo)
                    .courierCompany(courierCompany)
                    .trackingNo(trackingNo)
                    .status(AppConstants.LogisticsStatus.SIGNED)
                    .shippedTime(LocalDateTime.parse("2025-12-20 08:00:15", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .signedTime(LocalDateTime.parse("2025-12-21 08:50:28", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .lastUpdateTime(LocalDateTime.now())
                    .deleted(0)
                    .build();
            
            logisticsMapper.insert(logistics);
            
            log.info("固定物流信息已创建: orderId={}, orderNo={}, trackingNo={}", orderId, orderNo, trackingNo);
        } else {
            // 如果已经存在，更新订单ID
            existingLogistics.setOrderId(orderId);
            existingLogistics.setLastUpdateTime(LocalDateTime.now());
            logisticsMapper.updateById(existingLogistics);
            
            log.info("固定物流信息已更新: orderId={}, trackingNo={}", orderId, existingLogistics.getTrackingNo());
        }
    }
    
    /**
     * 创建初始物流轨迹
     */
    private void createInitialTracks(String logisticsId, String trackingNo) {
        // 创建详细的物流轨迹，按照用户提供的SQL数据
        List<LogisticsTrack> tracks = new ArrayList<>();
        
        // 节点1：订单创建（最早节点，sort_order=25）
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 08:00:15", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("顺丰系统后台")
                .description("用户下单成功，运单号" + trackingNo + "已生成，包裹类型：电子配件，重量1.2kg，保价金额：5000元，发货地址：广东省深圳市南山区粤海街道科技园A栋101室，收货地址：上海市浦东新区陆家嘴街道世纪大道100号B栋501室")
                .courierName(null)
                .courierPhone(null)
                .sortOrder(25)
                .deleted(0)
                .build());
        
        // 节点2：待揽收
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 08:10:22", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区粤海街道科技园片区")
                .description("顺丰客服已接单，分配揽收任务至南山科技园配送站，预计40分钟内上门揽收，客服工号：KF075510086")
                .courierName("王芳（客服）")
                .courierPhone("13000130000")
                .sortOrder(24)
                .deleted(0)
                .build());
        
        // 节点3：上门揽收中
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 08:45:47", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区粤海街道科技园A栋101室")
                .description("快递员已到达发货地址，核对包裹信息：电子配件，包装为防静电泡沫+硬纸箱，无破损，收件人信息已确认")
                .courierName("李军")
                .courierPhone("13500135000")
                .sortOrder(23)
                .deleted(0)
                .build());
        
        // 节点4：揽收成功
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 08:52:18", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区粤海街道科技园A栋101室")
                .description("包裹已成功揽收，揽收工号：SF075512345，包裹重量1.2kg，体积0.005m³，已贴电子面单，单号：" + trackingNo)
                .courierName("李军")
                .courierPhone("13500135000")
                .sortOrder(22)
                .deleted(0)
                .build());
        
        // 节点5：前往片区网点
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 09:25:33", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区粤海街道科技园配送点")
                .description("包裹已装车（电动配送车，编号：SZ-YH-0089），前往顺丰南山科技园片区网点，当前行驶路线：科技园路→深南大道")
                .courierName("李军")
                .courierPhone("13500135000")
                .sortOrder(21)
                .deleted(0)
                .build());
        
        // 节点6：到达片区网点
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 09:50:11", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区科技园顺丰网点（地址：南山区深南大道9998号）")
                .description("包裹已到达片区网点，网点编码：SZ-NST-001，入库扫码成功，等待同城分拣")
                .courierName("张磊")
                .courierPhone("13600136000")
                .sortOrder(20)
                .deleted(0)
                .build());
        
        // 节点7：片区网点分拣中
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 10:15:58", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区科技园顺丰网点分拣区")
                .description("包裹已完成同城分拣，分拣设备编号：SF-SORT-001，分拣耗时12分钟，目的地：深圳市宝安区华南航空转运中心")
                .courierName("张磊")
                .courierPhone("13600136000")
                .sortOrder(19)
                .deleted(0)
                .build());
        
        // 节点8：离开片区网点
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 10:40:45", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市南山区科技园顺丰网点")
                .description("包裹已装入同城转运车（车牌号：粤B-SF1234），前往深圳宝安华南航空转运中心，司机姓名：王浩，工号：SF075523456")
                .courierName("王浩")
                .courierPhone("13700137000")
                .sortOrder(18)
                .deleted(0)
                .build());
        
        // 节点9：到达深圳航空转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 11:25:27", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市宝安区福永街道顺丰华南航空转运中心（近宝安机场）")
                .description("包裹已到达华南航空转运中心，转运中心编码：SZ-HN-001，入库成功，等待跨省航空转运")
                .courierName("刘阳")
                .courierPhone("13800138001")
                .sortOrder(17)
                .deleted(0)
                .build());
        
        // 节点10：航空转运中心分拣（跨省）
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 12:10:19", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市宝安区顺丰华南航空转运中心跨省分拣区")
                .description("包裹已完成跨省分拣，分配至「深圳→广州」干线运输链路，运输类型：陆运干线，车辆编号：粤B-SF8888")
                .courierName("刘阳")
                .courierPhone("13800138001")
                .sortOrder(16)
                .deleted(0)
                .build());
        
        // 节点11：离开深圳航空转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 12:45:05", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("深圳市")
                .operateLocation("广东省深圳市宝安区顺丰华南航空转运中心")
                .description("「深圳→广州」干线车辆已发车，车牌号：粤B88888，司机：陈强，预计2025-12-20 14:30到达广州白云转运中心")
                .courierName("陈强")
                .courierPhone("13900139000")
                .sortOrder(15)
                .deleted(0)
                .build());
        
        // 节点12：到达广州白云转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 14:20:38", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("广州市")
                .operateLocation("广东省广州市白云区太和镇顺丰华南枢纽转运中心")
                .description("车辆已到达广州白云转运中心，比预计提前10分钟，转运中心编码：GZ-BY-002，入库扫码成功")
                .courierName("黄涛")
                .courierPhone("13100131000")
                .sortOrder(14)
                .deleted(0)
                .build());
        
        // 节点13：广州转运中心分拣（跨区域）
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 15:05:22", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("广州市")
                .operateLocation("广东省广州市白云区顺丰华南枢纽转运中心分拣区")
                .description("包裹已完成跨区域分拣，分配至「广州→长沙」干线运输链路，运输类型：航空+陆运联运")
                .courierName("黄涛")
                .courierPhone("13100131000")
                .sortOrder(13)
                .deleted(0)
                .build());
        
        // 节点14：离开广州白云转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 15:40:17", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("广州市")
                .operateLocation("广东省广州市白云区顺丰华南枢纽转运中心")
                .description("包裹已装车，前往广州白云机场，预计搭乘ZH9301航班（17:00起飞）飞往长沙黄花机场")
                .courierName("吴彬")
                .courierPhone("13200132000")
                .sortOrder(12)
                .deleted(0)
                .build());
        
        // 节点15：到达长沙黄花机场转运点
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 18:15:49", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("长沙市")
                .operateLocation("湖南省长沙市长沙县黄花机场顺丰航空转运点")
                .description("包裹已抵达长沙黄花机场，航班准点到达，卸货扫码成功，等待中转至长沙雨花转运中心")
                .courierName("郑明")
                .courierPhone("13400134000")
                .sortOrder(11)
                .deleted(0)
                .build());
        
        // 节点16：中转至武汉洪山转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-20 20:05:33", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("武汉市")
                .operateLocation("湖北省武汉市洪山区顺丰华中转运中心")
                .description("包裹已从长沙转运至武汉洪山转运中心，干线车辆车牌号：鄂A-SF6666，预计停留2小时后发往合肥")
                .courierName("冯杰")
                .courierPhone("13900139001")
                .sortOrder(10)
                .deleted(0)
                .build());
        
        // 节点17：中转至合肥蜀山转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 02:40:15", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("合肥市")
                .operateLocation("安徽省合肥市蜀山区顺丰华东转运中心（合肥仓）")
                .description("包裹已抵达合肥蜀山转运中心，夜间干线运输准点到达，入库后完成华东区域分拣")
                .courierName("孟伟")
                .courierPhone("13800138002")
                .sortOrder(9)
                .deleted(0)
                .build());
        
        // 节点18：中转至南京江宁转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 05:20:28", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("南京市")
                .operateLocation("江苏省南京市江宁区顺丰长三角转运中心")
                .description("包裹已从合肥转运至南京江宁转运中心，运输时长3小时，包裹状态完好，无挤压")
                .courierName("程磊")
                .courierPhone("13700137002")
                .sortOrder(8)
                .deleted(0)
                .build());
        
        // 节点19：南京转运中心分拣（最后跨省）
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 06:10:11", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("南京市")
                .operateLocation("江苏省南京市江宁区顺丰长三角转运中心分拣区")
                .description("包裹已完成最后一次跨省分拣，分配至「南京→上海」支线运输，预计3小时到达上海青浦转运中心")
                .courierName("程磊")
                .courierPhone("13700137002")
                .sortOrder(7)
                .deleted(0)
                .build());
        
        // 节点20：离开南京江宁转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 06:40:05", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("南京市")
                .operateLocation("江苏省南京市江宁区顺丰长三角转运中心")
                .description("「南京→上海」支线车辆已发车，车牌号：苏A-SF9999，司机：林浩，预计09:30到达上海青浦")
                .courierName("林浩")
                .courierPhone("13600136002")
                .sortOrder(6)
                .deleted(0)
                .build());
        
        // 节点21：到达上海青浦转运中心
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 09:25:38", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("上海市")
                .operateLocation("上海市青浦区华新镇顺丰华东转运中心（上海仓）")
                .description("车辆已到达上海青浦转运中心，比预计提前5分钟，入库扫码成功，等待同城分拣")
                .courierName("赵伟")
                .courierPhone("13100131000")
                .sortOrder(5)
                .deleted(0)
                .build());
        
        // 节点22：上海青浦转运中心分拣（同城）
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 10:00:22", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("上海市")
                .operateLocation("上海市青浦区顺丰华东转运中心同城分拣区")
                .description("包裹已完成同城分拣，分配至浦东新区陆家嘴配送网点，分拣设备编号：SH-SORT-008")
                .courierName("赵伟")
                .courierPhone("13100131000")
                .sortOrder(4)
                .deleted(0)
                .build());
        
        // 节点23：到达上海陆家嘴配送网点
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 11:15:17", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("上海市")
                .operateLocation("上海市浦东新区陆家嘴街道顺丰配送网点（地址：浦东新区世纪大道888号）")
                .description("包裹已到达配送网点，网点编码：SH-LJZ-001，扫码出库后分配给配送员周杰")
                .courierName("周杰")
                .courierPhone("13400134000")
                .sortOrder(3)
                .deleted(0)
                .build());
        
        // 节点24：上海陆家嘴片区派送中
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 11:50:49", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("上海市")
                .operateLocation("上海市浦东新区陆家嘴街道世纪大道100号周边")
                .description("配送员已取件，当前位置：世纪大道浦东南路交叉口，正在联系收件人（电话：13800138000），预计15分钟内送达")
                .courierName("周杰")
                .courierPhone("13900139001")
                .sortOrder(2)
                .deleted(0)
                .build());
        
        // 节点25：签收成功（最新节点，sort_order=1）
        tracks.add(LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(LocalDateTime.parse("2025-12-21 12:05:28", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .operateCity("上海市")
                .operateLocation("上海市浦东新区陆家嘴街道世纪大道100号B栋501室")
                .description("收件人：测试用户，身份证尾号：XXXX，签收方式：本人签收，包裹拆封验收：电子配件完好无损，无缺失，签收时间：2025-12-21 12:05:28，配送员工号：SF02123456")
                .courierName("周杰")
                .courierPhone("13900139001")
                .sortOrder(1)
                .deleted(0)
                .build());
        
        // 批量插入物流轨迹
        for (LogisticsTrack track : tracks) {
            logisticsTrackMapper.insert(track);
        }
        
        log.info("创建了{}条详细物流轨迹: logisticsId={}", tracks.size(), logisticsId);
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
