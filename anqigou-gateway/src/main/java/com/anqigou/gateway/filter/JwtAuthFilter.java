package com.anqigou.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import com.anqigou.common.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 网关JWT认证过滤器
 * 验证API请求的JWT令牌，保护后端服务
 */
@Component
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {
    
    @Autowired(required = false)
    private JwtUtil jwtUtil;
    
    // 无需认证的路径
    private static final String[] ALLOW_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/send-code",
            "/api/auth/login-with-code",
            "/api/auth/wechat-login",
            "/api/products",
            "/api/products/**",
            "/api/product",
            "/api/product/**",
            "/api/cart",
            "/api/cart/**",
            "/api/feedback",
            "/api/feedback/**",
            "/payment/notify",
            "/wechat/notify",
            "/alipay/notify",
    };
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        // 检查是否需要认证
        if (isAllowedPath(path)) {
            return chain.filter(exchange);
        }
        
        // 从请求头获取token
        String token = getToken(request);
        
        if (!StringUtils.hasText(token)) {
            log.warn("请求未提供token - 路径: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        try {
            if (jwtUtil != null) {
                Claims claims = jwtUtil.extractClaims(token);
                if (claims == null) {
                    log.warn("JWT token无效 - path: {}", path);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                String userId = claims.get("userId", String.class);
                
                // 将userId放入请求头，传递给后端服务
                ServerHttpRequest newRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .build();
                ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
                
                log.debug("JWT认证成功 - userId: {}, path: {}", userId, path);
                return chain.filter(newExchange);
            } else {
                log.warn("JwtUtil未初始化，跳过JWT验证");
                return chain.filter(exchange);
            }
        } catch (Exception ex) {
            log.error("JWT认证失败 - path: {}, error: {}", path, ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    
    /**
     * 从请求头获取token
     */
    private String getToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
    
    /**
     * 判断是否为无需认证的路径
     */
    private boolean isAllowedPath(String path) {
        for (String allowedPath : ALLOW_PATHS) {
            if (allowedPath.endsWith("/**")) {
                String prefix = allowedPath.substring(0, allowedPath.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(allowedPath)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
}
