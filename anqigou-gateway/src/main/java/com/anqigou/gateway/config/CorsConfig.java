package com.anqigou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 网关CORS跨域配置
 * 解决前端跨域访问后端API的问题
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        // 创建CORS配置
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源 - 只允许特定源，不使用通配符，避免重复设置
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        
        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许的HTTP方法
        config.setAllowedMethods(Arrays.asList("*"));
        
        // 允许携带credentials
        config.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);
        
        // 配置源匹配模式
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        // 创建并返回CorsWebFilter
        return new CorsWebFilter(source);
    }
}