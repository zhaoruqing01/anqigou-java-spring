package com.anqigou.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置类
 */
@Configuration
public class WechatConfig {
    
    @Value("${wechat.miniapp.appid}")
    private String appId;
    
    @Value("${wechat.miniapp.secret}")
    private String secret;
    
    public String getAppId() {
        return appId;
    }
    
    public String getSecret() {
        return secret;
    }
}