package com.anqigou.logistics.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.anqigou.logistics.entity.LogisticsTrack;

import lombok.extern.slf4j.Slf4j;

/**
 * 物流轨迹数据生成器
 * 用于生成完整、真实的物流轨迹数据
 */
@Slf4j
public class LogisticsTrackGenerator {
    
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 真实城市数据
    private static final List<String> SENDER_CITIES = Arrays.asList(
        "深圳市", "广州市", "杭州市", "上海市", "北京市", "成都市", "武汉市", "南京市", "苏州市", "东莞市"
    );
    
    // 快递公司数据
    private static final List<String> COURIER_COMPANIES = Arrays.asList(
        "顺丰速递", "圆通速递", "中通快递", "申通快递", "韵达速递", "极兔速递", "京东物流", "邮政EMS"
    );
    
    // 快递员姓名池
    private static final List<String> COURIER_NAMES = Arrays.asList(
        "李军", "王芳", "张磊", "刘阳", "陈强", "黄涛", "吴彬", "郑明", "冯杰", "孟伟",
        "程磊", "林浩", "赵伟", "周杰", "孙涛", "马超", "朱明", "何峰", "曹勇", "董伟"
    );
    
    // 客服姓名池
    private static final List<String> SERVICE_NAMES = Arrays.asList(
        "王芳（客服）", "李娜（客服）", "张敏（客服）", "刘静（客服）", "陈丽（客服）"
    );
    
    /**
     * 生成完整的物流轨迹
     * 
     * @param logisticsId 物流ID
     * @param trackingNo 快递单号
     * @param senderCity 发件城市
     * @param receiverProvince 收件省份
     * @param receiverCity 收件城市
     * @param receiverDistrict 收件区县
     * @param receiverDetailAddress 收件详细地址
     * @param receiverName 收件人姓名
     * @param receiverPhone 收件人电话
     * @return 物流轨迹列表
     */
    public static List<LogisticsTrack> generateTracks(
            String logisticsId, 
            String trackingNo,
            String senderCity,
            String receiverProvince,
            String receiverCity,
            String receiverDistrict,
            String receiverDetailAddress,
            String receiverName,
            String receiverPhone) {
        
        List<LogisticsTrack> tracks = new ArrayList<>();
        
        // 基准时间：2天前开始
        LocalDateTime baseTime = LocalDateTime.now().minusDays(2);
        
        // 生成发件城市的详细信息
        String senderProvince = getCityProvince(senderCity);
        String senderDistrict = generateSenderDistrict(senderCity);
        String senderDetailAddress = generateSenderAddress(senderCity, senderDistrict);
        
        int sortOrder = 25; // 从25开始倒序
        
        // 1. 订单创建
        tracks.add(createTrack(logisticsId, trackingNo, baseTime, 
            senderCity, senderProvince + senderCity + "顺丰系统后台",
            String.format("用户下单成功,运单号%s已生成,包裹类型:电子配件,重量1.2kg,保价金额:5000元,发货地址:%s%s%s%s,收货地址:%s%s%s%s",
                trackingNo, senderProvince, senderCity, senderDistrict, senderDetailAddress,
                receiverProvince, receiverCity, receiverDistrict, receiverDetailAddress),
            null, null, sortOrder--));
        
        // 2. 待揽收
        baseTime = baseTime.plusMinutes(10);
        String courierName1 = getRandomCourier();
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + "片区",
            String.format("顺丰客服已接单,分配揽收任务至%s配送站,预计40分钟内上门揽收,客服工号:KF%s",
                senderDistrict, generateWorkId()),
            getRandomServiceName(), generatePhone(), sortOrder--));
        
        // 3. 上门揽收中
        baseTime = baseTime.plusMinutes(35);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + senderDetailAddress,
            "快递员已到达发货地址,核对包裹信息:电子配件,包装为防静电泡沫+硬纸箱,无破损,收件人信息已确认",
            courierName1, generatePhone(), sortOrder--));
        
        // 4. 揽收成功
        baseTime = baseTime.plusMinutes(7);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + senderDetailAddress,
            String.format("包裹已成功揽收,揽收工号:SF%s,包裹重量1.2kg,体积0.005m³,已贴电子面单,单号:%s",
                generateWorkId(), trackingNo),
            courierName1, generatePhone(), sortOrder--));
        
        // 5. 前往片区网点
        baseTime = baseTime.plusMinutes(33);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + "配送点",
            String.format("包裹已装车(电动配送车,编号:%s),前往顺丰%s片区网点",
                generateVehicleId(senderCity), senderDistrict),
            courierName1, generatePhone(), sortOrder--));
        
        // 6. 到达片区网点
        baseTime = baseTime.plusMinutes(25);
        String courierName2 = getRandomCourier();
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + "顺丰网点",
            String.format("包裹已到达片区网点,网点编码:%s,入库扫码成功,等待同城分拣",
                generateSiteCode(senderCity, senderDistrict)),
            courierName2, generatePhone(), sortOrder--));
        
        // 7. 片区网点分拣
        baseTime = baseTime.plusMinutes(25);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + "顺丰网点分拣区",
            String.format("包裹已完成同城分拣,分拣设备编号:SF-SORT-%03d,分拣耗时12分钟,目的地:%s航空转运中心",
                RANDOM.nextInt(100), senderCity),
            courierName2, generatePhone(), sortOrder--));
        
        // 8. 离开片区网点
        baseTime = baseTime.plusMinutes(25);
        String courierName3 = getRandomCourier();
        String vehiclePlate = generateVehiclePlate(senderCity);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            senderCity, senderProvince + senderCity + senderDistrict + "顺丰网点",
            String.format("包裹已装入同城转运车(车牌号:%s),前往%s航空转运中心,司机姓名:%s,工号:SF%s",
                vehiclePlate, senderCity, courierName3, generateWorkId()),
            courierName3, generatePhone(), sortOrder--));
        
        // 9-20. 生成干线运输路线
        List<String> transitRoute = generateTransitRoute(senderCity, receiverCity);
        sortOrder = generateTransitTracks(tracks, logisticsId, trackingNo, transitRoute, baseTime, sortOrder);
        
        // 21. 到达目的地配送网点
        baseTime = baseTime.plusHours(2).plusMinutes(25);
        String finalCourierName = getRandomCourier();
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            receiverCity, receiverProvince + receiverCity + receiverDistrict + "顺丰配送网点",
            String.format("包裹已到达配送网点,网点编码:%s,扫码出库后分配给配送员%s",
                generateSiteCode(receiverCity, receiverDistrict), finalCourierName),
            finalCourierName, generatePhone(), sortOrder--));
        
        // 22. 派送中
        baseTime = baseTime.plusMinutes(35);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            receiverCity, receiverProvince + receiverCity + receiverDistrict + "周边",
            String.format("配送员已取件,正在联系收件人(电话:%s),预计15分钟内送达",
                maskPhone(receiverPhone)),
            finalCourierName, generatePhone(), sortOrder--));
        
        // 23. 签收成功
        baseTime = baseTime.plusMinutes(15);
        tracks.add(createTrack(logisticsId, trackingNo, baseTime,
            receiverCity, receiverProvince + receiverCity + receiverDistrict + receiverDetailAddress,
            String.format("收件人:%s,签收方式:本人签收,包裹拆封验收:电子配件完好无损,无缺失,签收时间:%s,配送员工号:SF%s",
                receiverName, baseTime.format(FORMATTER), generateWorkId()),
            finalCourierName, generatePhone(), sortOrder--));
        
        log.info("生成了{}条物流轨迹: logisticsId={}, trackingNo={}", tracks.size(), logisticsId, trackingNo);
        
        return tracks;
    }
    
    /**
     * 生成干线运输轨迹
     */
    private static int generateTransitTracks(List<LogisticsTrack> tracks, String logisticsId, 
            String trackingNo, List<String> route, LocalDateTime baseTime, int sortOrder) {
        
        for (int i = 0; i < route.size() - 1; i++) {
            String fromCity = route.get(i);
            String toCity = route.get(i + 1);
            String fromProvince = getCityProvince(fromCity);
            String toProvince = getCityProvince(toCity);
            
            // 到达转运中心
            baseTime = baseTime.plusHours(1).plusMinutes(5 + RANDOM.nextInt(20));
            String courierName = getRandomCourier();
            tracks.add(createTrack(logisticsId, trackingNo, baseTime,
                fromCity, fromProvince + fromCity + "顺丰转运中心",
                String.format("包裹已到达%s转运中心,转运中心编码:%s,入库成功,等待跨省转运",
                    fromCity, generateTransitCode(fromCity)),
                courierName, generatePhone(), sortOrder--));
            
            // 转运中心分拣
            baseTime = baseTime.plusMinutes(35 + RANDOM.nextInt(20));
            tracks.add(createTrack(logisticsId, trackingNo, baseTime,
                fromCity, fromProvince + fromCity + "顺丰转运中心分拣区",
                String.format("包裹已完成分拣,分配至「%s→%s」干线运输链路,运输类型:%s",
                    fromCity, toCity, i % 2 == 0 ? "陆运干线" : "航空运输"),
                courierName, generatePhone(), sortOrder--));
            
            // 离开转运中心
            baseTime = baseTime.plusMinutes(25 + RANDOM.nextInt(15));
            String driver = getRandomCourier();
            String vehicle = generateVehiclePlate(fromCity);
            tracks.add(createTrack(logisticsId, trackingNo, baseTime,
                fromCity, fromProvince + fromCity + "顺丰转运中心",
                String.format("「%s→%s」干线车辆已发车,车牌号:%s,司机:%s,预计%d小时后到达%s",
                    fromCity, toCity, vehicle, driver, 2 + RANDOM.nextInt(3), toCity),
                driver, generatePhone(), sortOrder--));
        }
        
        return sortOrder;
    }
    
    /**
     * 生成运输路线
     */
    private static List<String> generateTransitRoute(String fromCity, String toCity) {
        List<String> route = new ArrayList<>();
        route.add(fromCity);
        
        // 根据起止城市生成中转路线
        if (!fromCity.equals(toCity)) {
            // 添加1-3个中转城市
            List<String> transitCities = getTransitCities(fromCity, toCity);
            route.addAll(transitCities);
        }
        
        route.add(toCity);
        return route;
    }
    
    /**
     * 获取中转城市
     */
    private static List<String> getTransitCities(String from, String to) {
        List<String> transits = new ArrayList<>();
        
        // 简化的路由规则
        if (from.contains("深圳") && to.contains("上海")) {
            transits.add("广州市");
            transits.add("长沙市");
            transits.add("武汉市");
            transits.add("合肥市");
            transits.add("南京市");
        } else if (from.contains("广州") && to.contains("北京")) {
            transits.add("长沙市");
            transits.add("武汉市");
            transits.add("郑州市");
        } else if (from.contains("上海") && to.contains("成都")) {
            transits.add("杭州市");
            transits.add("武汉市");
            transits.add("重庆市");
        } else {
            // 默认添加1-2个中转城市
            List<String> cities = Arrays.asList("武汉市", "长沙市", "郑州市", "南京市");
            int count = 1 + RANDOM.nextInt(2);
            for (int i = 0; i < count && i < cities.size(); i++) {
                transits.add(cities.get(i));
            }
        }
        
        return transits;
    }
    
    /**
     * 创建物流轨迹
     */
    private static LogisticsTrack createTrack(String logisticsId, String trackingNo, 
            LocalDateTime operateTime, String city, String location, String description,
            String courierName, String courierPhone, int sortOrder) {
        return LogisticsTrack.builder()
                .id(UUID.randomUUID().toString())
                .logisticsId(logisticsId)
                .trackingNo(trackingNo)
                .operateTime(operateTime)
                .operateCity(city)
                .operateLocation(location)
                .description(description)
                .courierName(courierName)
                .courierPhone(courierPhone)
                .sortOrder(sortOrder)
                .deleted(0)
                .build();
    }
    
    /**
     * 获取随机快递员
     */
    private static String getRandomCourier() {
        return COURIER_NAMES.get(RANDOM.nextInt(COURIER_NAMES.size()));
    }
    
    /**
     * 获取随机客服
     */
    private static String getRandomServiceName() {
        return SERVICE_NAMES.get(RANDOM.nextInt(SERVICE_NAMES.size()));
    }
    
    /**
     * 生成工号
     */
    private static String generateWorkId() {
        return String.format("%08d", RANDOM.nextInt(100000000));
    }
    
    /**
     * 生成车辆编号
     */
    private static String generateVehicleId(String city) {
        String prefix = city.substring(0, 2);
        return String.format("%s-%s-%04d", getPinyin(prefix), "YH", RANDOM.nextInt(10000));
    }
    
    /**
     * 生成网点编码
     */
    private static String generateSiteCode(String city, String district) {
        String cityCode = getCityCode(city);
        String districtCode = district.substring(0, Math.min(2, district.length()));
        return String.format("%s-%s-%03d", cityCode, getPinyin(districtCode), RANDOM.nextInt(1000));
    }
    
    /**
     * 生成转运中心编码
     */
    private static String generateTransitCode(String city) {
        return String.format("%s-TC-%03d", getCityCode(city), RANDOM.nextInt(100));
    }
    
    /**
     * 生成车牌号
     */
    private static String generateVehiclePlate(String city) {
        String provinceCode = getProvinceCode(city);
        return String.format("%s-SF%04d", provinceCode, RANDOM.nextInt(10000));
    }
    
    /**
     * 生成电话号码
     */
    private static String generatePhone() {
        return String.format("13%09d", RANDOM.nextInt(1000000000));
    }
    
    /**
     * 掩码电话
     */
    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 获取城市所属省份
     */
    private static String getCityProvince(String city) {
        if (city.contains("深圳") || city.contains("广州") || city.contains("东莞")) {
            return "广东省";
        } else if (city.contains("上海")) {
            return "上海市";
        } else if (city.contains("北京")) {
            return "北京市";
        } else if (city.contains("杭州") || city.contains("宁波")) {
            return "浙江省";
        } else if (city.contains("成都")) {
            return "四川省";
        } else if (city.contains("武汉")) {
            return "湖北省";
        } else if (city.contains("长沙")) {
            return "湖南省";
        } else if (city.contains("南京") || city.contains("苏州")) {
            return "江苏省";
        } else if (city.contains("合肥")) {
            return "安徽省";
        } else if (city.contains("郑州")) {
            return "河南省";
        } else if (city.contains("重庆")) {
            return "重庆市";
        } else {
            return "广东省";
        }
    }
    
    /**
     * 获取城市代码
     */
    private static String getCityCode(String city) {
        if (city.contains("深圳")) return "SZ";
        if (city.contains("广州")) return "GZ";
        if (city.contains("上海")) return "SH";
        if (city.contains("北京")) return "BJ";
        if (city.contains("杭州")) return "HZ";
        if (city.contains("成都")) return "CD";
        if (city.contains("武汉")) return "WH";
        if (city.contains("长沙")) return "CS";
        if (city.contains("南京")) return "NJ";
        if (city.contains("苏州")) return "SZ";
        if (city.contains("合肥")) return "HF";
        if (city.contains("郑州")) return "ZZ";
        return "SZ";
    }
    
    /**
     * 获取省份代码(车牌)
     */
    private static String getProvinceCode(String city) {
        if (city.contains("深圳") || city.contains("广州") || city.contains("东莞")) return "粤";
        if (city.contains("上海")) return "沪";
        if (city.contains("北京")) return "京";
        if (city.contains("浙江") || city.contains("杭州")) return "浙";
        if (city.contains("四川") || city.contains("成都")) return "川";
        if (city.contains("湖北") || city.contains("武汉")) return "鄂";
        if (city.contains("湖南") || city.contains("长沙")) return "湘";
        if (city.contains("江苏") || city.contains("南京") || city.contains("苏州")) return "苏";
        if (city.contains("安徽") || city.contains("合肥")) return "皖";
        if (city.contains("河南") || city.contains("郑州")) return "豫";
        return "粤";
    }
    
    /**
     * 简化的拼音转换
     */
    private static String getPinyin(String chinese) {
        // 简化处理，实际应使用拼音库
        if (chinese.contains("深圳")) return "SZ";
        if (chinese.contains("南山")) return "NS";
        if (chinese.contains("福田")) return "FT";
        if (chinese.contains("龙岗")) return "LG";
        if (chinese.contains("宝安")) return "BA";
        if (chinese.contains("浦东")) return "PD";
        if (chinese.contains("黄浦")) return "HP";
        if (chinese.contains("徐汇")) return "XH";
        if (chinese.contains("朝阳")) return "CY";
        if (chinese.contains("海淀")) return "HD";
        return "QT";
    }
    
    /**
     * 生成发件人区县
     */
    private static String generateSenderDistrict(String city) {
        if (city.contains("深圳")) {
            String[] districts = {"南山区", "福田区", "龙岗区", "宝安区", "龙华区"};
            return districts[RANDOM.nextInt(districts.length)];
        } else if (city.contains("上海")) {
            String[] districts = {"浦东新区", "黄浦区", "徐汇区", "静安区", "杨浦区"};
            return districts[RANDOM.nextInt(districts.length)];
        } else if (city.contains("北京")) {
            String[] districts = {"朝阳区", "海淀区", "东城区", "西城区", "丰台区"};
            return districts[RANDOM.nextInt(districts.length)];
        } else {
            return "中心区";
        }
    }
    
    /**
     * 生成发件人详细地址
     */
    private static String generateSenderAddress(String city, String district) {
        String[] streets = {"科技园", "工业园", "商业街", "中心路", "创业大道"};
        String street = streets[RANDOM.nextInt(streets.length)];
        String building = String.format("%c栋%d室", (char)('A' + RANDOM.nextInt(5)), 100 + RANDOM.nextInt(900));
        return street + building;
    }
    
    /**
     * 生成随机快递公司
     */
    public static String generateCourierCompany() {
        return COURIER_COMPANIES.get(RANDOM.nextInt(COURIER_COMPANIES.size()));
    }
    
    /**
     * 生成随机快递单号
     */
    public static String generateTrackingNo(String courierCompany) {
        if (courierCompany.contains("顺丰")) {
            return "SF" + System.currentTimeMillis() % 1000000000000L;
        } else if (courierCompany.contains("圆通")) {
            return "YT" + System.currentTimeMillis() % 1000000000000L;
        } else if (courierCompany.contains("中通")) {
            return "ZT" + System.currentTimeMillis() % 1000000000000L;
        } else if (courierCompany.contains("申通")) {
            return "ST" + System.currentTimeMillis() % 1000000000000L;
        } else if (courierCompany.contains("韵达")) {
            return "YD" + System.currentTimeMillis() % 1000000000000L;
        } else {
            return "EX" + System.currentTimeMillis() % 1000000000000L;
        }
    }
    
    /**
     * 生成随机发件城市
     */
    public static String generateSenderCity() {
        return SENDER_CITIES.get(RANDOM.nextInt(SENDER_CITIES.size()));
    }
}
