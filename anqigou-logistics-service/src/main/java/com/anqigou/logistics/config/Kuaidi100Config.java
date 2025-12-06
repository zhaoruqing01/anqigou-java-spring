package com.anqigou.logistics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 快递100 API配置类
 */
@Component
@ConfigurationProperties(prefix = "kuaidi100")
public class Kuaidi100Config {
    
    /**
     * 快递100 API Key
     */
    private String apiKey;
    
    /**
     * 快递100 客户编号
     */
    private String customer;
    
    /**
     * 快递100 密钥
     */
    private String secret;
    
    /**
     * 实时查询接口地址
     */
    private String queryUrl = "https://poll.kuaidi100.com/poll/query.do";
    
    /**
     * 快递100 编码映射表（快递公司编码）
     */
    private String companyCodeMap;
    
    // getter and setter
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getCustomer() {
        return customer;
    }
    
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public String getQueryUrl() {
        return queryUrl;
    }
    
    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }
    
    public String getCompanyCodeMap() {
        return companyCodeMap;
    }
    
    public void setCompanyCodeMap(String companyCodeMap) {
        this.companyCodeMap = companyCodeMap;
    }
}