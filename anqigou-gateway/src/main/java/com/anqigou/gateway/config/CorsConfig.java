package com.anqigou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 网关CORS跨域配置
 * 解决前端跨域访问后端API的问题
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 允许所有源访问（使用 OriginPattern 而不是 Origin，以支持 credentials）
        // 生产环境建议指定具体的前端域名，如：
        // corsConfiguration.addAllowedOrigin("http://localhost:8080");
        // corsConfiguration.addAllowedOrigin("https://your-domain.com");
        corsConfiguration.addAllowedOriginPattern("*");
        
        // 允许所有请求头
        corsConfiguration.addAllowedHeader("*");
        
        // 允许所有HTTP方法
        corsConfiguration.addAllowedMethod("*");
        
        // 允许携带credentials（Cookie、Authorization等）
        corsConfiguration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        corsConfiguration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsWebFilter(source);
    }
}
