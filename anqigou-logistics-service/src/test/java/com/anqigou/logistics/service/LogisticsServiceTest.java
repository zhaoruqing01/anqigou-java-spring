package com.anqigou.logistics.service;

import com.anqigou.logistics.dto.LogisticsDetailDTO;
import com.anqigou.logistics.entity.Logistics;
import com.anqigou.logistics.mapper.LogisticsMapper;
import com.anqigou.logistics.service.impl.LogisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 物流服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class LogisticsServiceTest {
    
    @Mock
    private LogisticsMapper logisticsMapper;
    
    @InjectMocks
    private LogisticsServiceImpl logisticsService;
    
    private Logistics testLogistics;
    
    @BeforeEach
    void setUp() {
        testLogistics = Logistics.builder()
                .id("logistics-001")
                .orderId("order-001")
                .orderNo("ORD20231120001")
                .courierCompany("sf")
                .trackingNo("SF123456789")
                .status("transit")
                .build();
    }
    
    @Test
    void testGetLogisticsDetail() {
        // 测试获取物流详情
        when(logisticsMapper.selectById(anyString())).thenReturn(testLogistics);
        
        LogisticsDetailDTO result = logisticsService.getLogisticsDetail("order-001", "user-001");
        assertNotNull(result);
        assertEquals("SF123456789", result.getTrackingNo());
    }
    
    @Test
    void testShipOrder() {
        // 测试发货
        when(logisticsMapper.insert(any(Logistics.class))).thenReturn(1);
        
        assertDoesNotThrow(() -> {
            logisticsService.shipOrder("order-001", "sf", "SF123456789");
        });
    }
}
