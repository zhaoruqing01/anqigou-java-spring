package com.anqigou.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.anqigou.common.interceptor.UserIdInterceptor;

/**
 * Web MVC 配置
 * 注册拦截器和其他 MVC 相关配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private UserIdInterceptor userIdInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 userId 拦截器
        registry.addInterceptor(userIdInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/login",
                    "/auth/register",
                    "/auth/send-code",
                    "/auth/login-with-code",
                    "/auth/wechat-login",
                    "/product/list",
                    "/product/search",
                    "/product/hot",
                    "/product/recommended",
                    "/product/categories",
                    "/product/categories/first-level",
                    "/product/categories/sub",
                    "/error",
                    "/actuator/**"
                );
    }
}
