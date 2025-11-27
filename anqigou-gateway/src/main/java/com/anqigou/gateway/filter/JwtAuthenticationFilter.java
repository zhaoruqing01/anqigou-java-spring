package com.anqigou.gateway.filter;

import com.anqigou.common.constant.AppConstants;
import com.anqigou.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT 令牌验证过滤器
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // 不需要认证的路径
    private static final String[] EXCLUDE_PATHS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/login-with-code",
            "/api/auth/send-code",
            "/api/auth/wechat-login",
            "/api/products/",
            "/api/payment/wechat/notify",
            "/api/payment/alipay/notify"
    };
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // 检查是否在排除列表中
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }
        
        // 获取 Authorization 头
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleUnauthorized(exchange);
        }
        
        try {
            // 提取 Token
            String token = authHeader.substring(7);
            
            // 验证 Token
            if (!jwtUtil.validateToken(token)) {
                return handleUnauthorized(exchange);
            }
            
            // 从 Token 中提取 userId
            String userId = jwtUtil.getUserIdFromToken(token);
            
            // 将 userId 添加到请求头中，传递给后端服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .build();
            
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();
            
            return chain.filter(mutatedExchange);
            
        } catch (Exception e) {
            log.error("Token 验证失败", e);
            return handleUnauthorized(exchange);
        }
    }
    
    /**
     * 处理未授权请求
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory()
                        .wrap("{\"code\": 401, \"message\": \"Unauthorized\"}".getBytes())));
    }
    
    /**
     * 检查路径是否在排除列表中
     */
    private boolean isExcluded(String path) {
        for (String excludePath : EXCLUDE_PATHS) {
            if (path.startsWith(excludePath)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
}
