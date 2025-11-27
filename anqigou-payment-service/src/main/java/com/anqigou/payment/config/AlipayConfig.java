package com.anqigou.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付配置
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayConfig {
    
    /**
     * 应用 ID
     */
    private String appId;
    
    /**
     * 商户私钥
     */
    private String privateKey;
    
    /**
     * 支付宝公钥
     */
    private String publicKey;
    
    /**
     * 支付宝网关
     */
    private String gateway = "https://openapi.alipay.com/gateway.do";
    
    /**
     * 回调URL
     */
    private String notifyUrl;
    
    /**
     * 返回URL
     */
    private String returnUrl;
}
