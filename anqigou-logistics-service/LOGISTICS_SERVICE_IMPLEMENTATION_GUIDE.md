# 动态物流数据生成 - 实现指南

## 已完成的工作

### 1. 创建了 OrderServiceClient (✅)

- 位置: `src/main/java/com/anqigou/logistics/client/OrderServiceClient.java`
- 功能: 用于物流服务调用订单服务获取订单信息

### 2. 创建了 OrderInfoDTO (✅)

- 位置: `src/main/java/com/anqigou/logistics/dto/OrderInfoDTO.java`
- 功能: 订单信息传输对象,包含收货地址等信息

### 3. 创建了 LogisticsTrackGenerator (✅)

- 位置: `src/main/java/com/anqigou/logistics/util/LogisticsTrackGenerator.java`
- 功能: 完整的物流轨迹数据生成器,支持 20-25 个真实节点

## 需要修改的代码

### LogisticsServiceImpl.java 关键修改点

#### 1. 添加 OrderServiceClient 注入

```java
@Autowired
private OrderServiceClient orderServiceClient;
```

#### 2. 修改 getLogisticsDetail 方法

在 getLogisticsDetail 方法中:

```java
@Override
public LogisticsDetailDTO getLogisticsDetail(String orderId, String userId) {
    Logistics logistics = null;

    // 先查询是否已存在物流记录
    QueryWrapper<Logistics> orderQueryWrapper = new QueryWrapper<>();
    orderQueryWrapper.eq("order_id", orderId);
    logistics = logisticsMapper.selectOne(orderQueryWrapper);

    // 如果不存在,动态生成
    if (logistics == null) {
        logistics = generateDynamicLogistics(orderId);
    }

    // 获取轨迹
    List<LogisticsTrackDTO> tracks = new ArrayList<>();
    QueryWrapper<LogisticsTrack> trackWrapper = new QueryWrapper<>();
    trackWrapper.eq("logistics_id", logistics.getId())
            .orderByDesc("operate_time");
    List<LogisticsTrack> localTracks = logisticsTrackMapper.selectList(trackWrapper);

    // 如果没有轨迹,生成轨迹
    if (localTracks == null || localTracks.isEmpty()) {
        createDynamicTracks(logistics);
        localTracks = logisticsTrackMapper.selectList(trackWrapper);
    }

    tracks = convertTracks(localTracks);

    // 转换为DTO并返回
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
            .hasEvaluated(false)
            .build();

    return dto;
}
```

#### 3. 添加 generateDynamicLogistics 方法

```java
/**
 * 动态生成物流信息
 */
private Logistics generateDynamicLogistics(String orderId) {
    // 从订单服务获取订单信息
    OrderInfoDTO orderInfo = null;
    try {
        ApiResponse<OrderInfoDTO> response = orderServiceClient.getOrderInfo(orderId);
        if (response != null && response.getCode() == 0 && response.getData() != null) {
            orderInfo = response.getData();
        }
    } catch (Exception e) {
        log.error("获取订单信息失败: orderId={}", orderId, e);
    }

    if (orderInfo == null) {
        throw new BizException(404, "订单信息不存在");
    }

    // 生成唯一物流ID
    String logisticsId = UUID.randomUUID().toString();

    // 生成快递公司和快递单号
    String courierCompany = LogisticsTrackGenerator.generateCourierCompany();
    String trackingNo = LogisticsTrackGenerator.generateTrackingNo(courierCompany);

    // 生成发件城市
    String senderCity = LogisticsTrackGenerator.generateSenderCity();
    String senderProvince = getCityProvince(senderCity);
    String senderDistrict = generateSenderDistrict(senderCity);
    String senderAddress = generateSenderAddress(senderCity, senderDistrict);

    // 从订单获取收件信息
    String receiverName = orderInfo.getReceiverName();
    String receiverPhone = orderInfo.getReceiverPhone();
    String receiverProvince = orderInfo.getReceiverProvince();
    String receiverCity = orderInfo.getReceiverCity();
    String receiverDistrict = orderInfo.getReceiverDistrict();
    String receiverAddress = orderInfo.getReceiverDetailAddress();

    // 创建物流记录
    Logistics logistics = Logistics.builder()
            .id(logisticsId)
            .orderId(orderId)
            .orderNo(orderInfo.getOrderNo())
            .courierCompany(courierCompany)
            .trackingNo(trackingNo)
            .senderProvince(senderProvince)
            .senderCity(senderCity)
            .senderAddress(senderDistrict + senderAddress)
            .receiverName(receiverName)
            .receiverPhone(receiverPhone)
            .receiverProvince(receiverProvince)
            .receiverCity(receiverCity)
            .receiverAddress(receiverDistrict + receiverAddress)
            .status(AppConstants.LogisticsStatus.SIGNED)
            .shippedTime(LocalDateTime.now().minusDays(2))
            .signedTime(LocalDateTime.now())
            .lastUpdateTime(LocalDateTime.now())
            .deleted(0)
            .build();

    logisticsMapper.insert(logistics);
    log.info("动态创建物流信息: logisticsId={}, orderId={}, trackingNo={}", logisticsId, orderId, trackingNo);

    return logistics;
}
```

#### 4. 添加 createDynamicTracks 方法

```java
/**
 * 动态创建物流轨迹
 */
private void createDynamicTracks(Logistics logistics) {
    // 解析收件地址
    String receiverProvince = logistics.getReceiverProvince();
    String receiverCity = logistics.getReceiverCity();
    String receiverAddress = logistics.getReceiverAddress();

    // 分离区县和详细地址
    String receiverDistrict = "";
    String receiverDetailAddress = receiverAddress;
    if (receiverAddress.contains("区") || receiverAddress.contains("县")) {
        int idx = Math.max(receiverAddress.indexOf("区"), receiverAddress.indexOf("县"));
        if (idx > 0) {
            receiverDistrict = receiverAddress.substring(0, idx + 1);
            receiverDetailAddress = receiverAddress.substring(idx + 1);
        }
    }

    // 使用LogisticsTrackGenerator生成轨迹
    List<LogisticsTrack> tracks = LogisticsTrackGenerator.generateTracks(
            logistics.getId(),
            logistics.getTrackingNo(),
            logistics.getSenderCity(),
            receiverProvince,
            receiverCity,
            receiverDistrict,
            receiverDetailAddress,
            logistics.getReceiverName(),
            logistics.getReceiverPhone()
    );

    // 批量插入轨迹
    for (LogisticsTrack track : tracks) {
        logisticsTrackMapper.insert(track);
    }

    log.info("动态创建物流轨迹: logisticsId={}, trackCount={}", logistics.getId(), tracks.size());
}
```

#### 5. 修改 shipOrder 方法

```java
@Override
@Transactional
public void shipOrder(String orderId, String courierCompany, String trackingNo) {
    // 先检查是否已存在物流记录
    QueryWrapper<Logistics> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("order_id", orderId);
    Logistics existingLogistics = logisticsMapper.selectOne(queryWrapper);

    if (existingLogistics != null) {
        // 已存在,更新信息
        existingLogistics.setCourierCompany(courierCompany);
        existingLogistics.setTrackingNo(trackingNo);
        existingLogistics.setLastUpdateTime(LocalDateTime.now());
        logisticsMapper.updateById(existingLogistics);
        log.info("更新物流信息: orderId={}, trackingNo={}", orderId, trackingNo);
    } else {
        // 不存在,动态生成
        Logistics logistics = generateDynamicLogistics(orderId);
        createDynamicTracks(logistics);
        log.info("创建新物流信息: orderId={}, trackingNo={}", orderId, logistics.getTrackingNo());
    }
}
```

## 需要添加的辅助方法

```java
private String getCityProvince(String city) {
    if (city.contains("深圳") || city.contains("广州") || city.contains("东莞")) {
        return "广东省";
    } else if (city.contains("上海")) {
        return "上海市";
    } else if (city.contains("北京")) {
        return "北京市";
    }
    // ... 更多城市映射
    return "广东省";
}

private String generateSenderDistrict(String city) {
    if (city.contains("深圳")) {
        String[] districts = {"南山区", "福田区", "龙岗区", "宝安区", "龙华区"};
        return districts[new Random().nextInt(districts.length)];
    }
    // ... 更多城市映射
    return "中心区";
}

private String generateSenderAddress(String city, String district) {
    String[] streets = {"科技园", "工业园", "商业街", "中心路", "创业大道"};
    String street = streets[new Random().nextInt(streets.length)];
    String building = String.format("%c栋%d室", (char)('A' + new Random().nextInt(5)), 100 + new Random().nextInt(900));
    return street + building;
}
```

## 总结

完成以上修改后,系统将具备:

1. ✅ 为每个订单生成唯一的物流 ID
2. ✅ 从订单服务获取真实收货地址
3. ✅ 生成 20-25 个完整真实的物流轨迹节点
4. ✅ 支持多种快递公司和真实城市路线
5. ✅ 自动化的物流数据生成机制
