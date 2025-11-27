package com.anqigou.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置
 */
@Component
@ConfigurationProperties(prefix = "wechat.pay")
@Data
public class WechatPayConfig {
    
    /**
     * 微信公众号 ID
     */
    private String appId;
    
    /**
     * 商户号
     */
    private String mchId;
    
    /**
     * 商户密钥
     */
    private String key;
    
    /**
     * API密钥 v3
     */
    private String keyV3;
    
    /**
     * 证书路径
     */
    private String certPath;
    
    /**
     * 回调URL
     */
    private String notifyUrl;
}
