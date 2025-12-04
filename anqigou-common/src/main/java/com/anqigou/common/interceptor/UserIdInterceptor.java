package com.anqigou.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.anqigou.common.util.JwtUtil;

/**
 * 用户ID拦截器
 * 从请求头中提取userId并设置为请求属性，供@RequestAttribute注入
 */
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserIdInterceptor implements HandlerInterceptor {
    
    @Autowired(required = false)
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取 X-User-Id，网关层已经处理了 JWT 验证和 userId 提取
        String userId = request.getHeader("X-User-Id");
        log.info("UserIdInterceptor - Path: {}, X-User-Id: {}", request.getRequestURI(), userId);
        
        // 校验 X-User-Id 格式，防止前端传递错误的 userId（如将 token 当作 userId 传递）
        // UUID 长度为 36，如果长度过长或包含特殊前缀，则认为无效
        if (userId != null && (userId.length() > 40 || userId.startsWith("user_") || userId.startsWith("ey"))) {
            log.warn("UserIdInterceptor - Invalid X-User-Id format detected: {}, ignoring and falling back to token parsing", userId);
            userId = null;
        }
        
        // 如果没有 X-User-Id，尝试从 Authorization token 中解析
        if (userId == null || userId.isEmpty()) {
            String authHeader = request.getHeader("Authorization");
            log.info("UserIdInterceptor - Authorization: {}", authHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ") && jwtUtil != null) {
                // 从 token 中提取 userId
                String token = authHeader.substring(7);
                userId = jwtUtil.getUserIdFromToken(token);
                log.info("UserIdInterceptor - Extracted userId from token: {}", userId);
            }
        }
        
        // 设置为请求属性，供 @RequestAttribute 注入
        // 如果没有认证信息，提供默认值避免 @RequestAttribute 抛出异常
        if (userId == null || userId.isEmpty()) {
            userId = "anonymous";
        }
        log.info("UserIdInterceptor - Final userId: {}", userId);
        request.setAttribute("userId", userId);
        
        return true;
    }
}
