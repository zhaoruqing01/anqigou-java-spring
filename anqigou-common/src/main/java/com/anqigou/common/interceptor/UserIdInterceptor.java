package com.anqigou.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户ID拦截器
 * 从请求头中提取userId并设置为请求属性，供@RequestAttribute注入
 */
@Component
public class UserIdInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取 X-User-Id
        String userId = request.getHeader("X-User-Id");
        
        // 如果没有 X-User-Id，尝试从 Authorization token 中解析（简化处理）
        if (userId == null || userId.isEmpty()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // 这里应该真正验证并解析 JWT token
                // 为了简化演示，直接从 token 中提取（实际应使用 JWT 库）
                String token = authHeader.substring(7);
                // 简化处理：假设 userId 在 token 中可以解析
                // 实际应该使用 JWT 库来验证和解析 token
                userId = "user_" + token.substring(0, Math.min(8, token.length()));
            }
        }
        
        // 设置为请求属性，供 @RequestAttribute 注入
        // 如果没有认证信息，提供默认值避免 @RequestAttribute 抛出异常
        if (userId == null || userId.isEmpty()) {
            userId = "anonymous";
        }
        request.setAttribute("userId", userId);
        
        return true;
    }
}
