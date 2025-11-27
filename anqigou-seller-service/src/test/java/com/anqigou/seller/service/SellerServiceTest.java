package com.anqigou.seller.service;

import com.anqigou.seller.entity.Seller;
import com.anqigou.seller.mapper.SellerMapper;
import com.anqigou.seller.service.impl.SellerServiceImpl;
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
 * 商家服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SellerServiceTest {
    
    @Mock
    private SellerMapper sellerMapper;
    
    @InjectMocks
    private SellerServiceImpl sellerService;
    
    private Seller testSeller;
    
    @BeforeEach
    void setUp() {
        testSeller = Seller.builder()
                .id("seller-001")
                .userId("user-001")
                .shopName("测试店铺")
                .licenseNo("123456789")
                .status("pending")
                .build();
    }
    
    @Test
    void testRegisterSeller() {
        // 测试商家注册
        when(sellerMapper.insert(any(Seller.class))).thenReturn(1);
        
        assertDoesNotThrow(() -> {
            sellerService.registerSeller("user-001", "测试店铺", "123456789", "http://example.com/license.jpg");
        });
    }
    
    @Test
    void testGetSellerInfo() {
        // 测试获取商家信息
        when(sellerMapper.selectById("seller-001")).thenReturn(testSeller);
        
        Object result = sellerService.getSellerInfo("seller-001");
        assertNotNull(result);
    }
}
