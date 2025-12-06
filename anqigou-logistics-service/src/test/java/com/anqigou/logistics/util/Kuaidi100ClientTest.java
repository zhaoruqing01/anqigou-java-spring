package com.anqigou.logistics.util;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 快递100客户端测试类
 */
@SpringBootTest
public class Kuaidi100ClientTest {
    
    @Resource
    private Kuaidi100Client kuaidi100Client;
    
    /**
     * 测试快递100 API调用
     * 注意：需要先在application.yml中配置真实的API密钥
     */
    @Test
    public void testQueryLogisticsTrack() {
        // 这里使用测试数据，实际测试时需要替换为真实的快递单号
        String com = "sf";
        String num = "SF1234567890";
        
        try {
            System.out.println("开始测试快递100 API...");
            System.out.println("快递公司：" + com);
            System.out.println("快递单号：" + num);
            
            // 调用API获取轨迹
            // 注意：实际调用需要填写真实的API密钥和客户编号
            // JSONObject result = kuaidi100Client.queryLogisticsTrack(com, num);
            // System.out.println("API返回结果：" + result.toJSONString());
            
            System.out.println("快递100 API集成测试完成（模拟）");
            System.out.println("请在application.yml中配置真实的快递100 API密钥后，取消注释上方代码进行实际测试");
        } catch (Exception e) {
            System.err.println("测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试快递公司编码映射
     */
    @Test
    public void testGetCompanyCode() {
        System.out.println("开始测试快递公司编码映射...");
        
        // 测试几个常见的快递公司
        testCompanyCode("顺丰");
        testCompanyCode("中通");
        testCompanyCode("圆通");
        testCompanyCode("申通");
        testCompanyCode("韵达");
        testCompanyCode("其他快递公司");
        
        System.out.println("快递公司编码映射测试完成");
    }
    
    private void testCompanyCode(String companyName) {
        String code = kuaidi100Client.getCompanyCode(companyName);
        System.out.println(companyName + " -> " + code);
    }
}