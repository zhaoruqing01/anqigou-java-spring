package com.anqigou.common.config;

/**
 * 微服务CORS跨域配置
 * 解决前端跨域访问后端API的问题
 * 
 * 注意：由于网关已配置CORS，此配置已完全禁用
 * 避免CORS头重复导致的浏览器错误
 */
// @Configuration - 完全禁用此类，避免被扫描到
public class WebCorsConfig {
    
    // 注意：由于网关已配置CORS，此Bean已完全禁用
    // 避免CORS头重复导致的浏览器错误
    /*
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 允许所有源访问（可根据实际需求限制）
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedOrigin("http://localhost:5174");
        
        // 允许所有请求头
        corsConfiguration.addAllowedHeader("*");
        
        // 允许所有HTTP方法
        corsConfiguration.addAllowedMethod("*");
        
        // 允许携带credentials
        corsConfiguration.setAllowCredentials(false);
        
        // 预检请求的缓存时间（秒）
        corsConfiguration.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }
    */
}