package com.anqigou.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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
    
    // Ant路径匹配器，用于匹配请求路径
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 无需认证的路径
    private static final String[] ALLOW_PATHS = {
            // 认证相关
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/send-code",
            "/api/auth/login-with-code",
            "/api/auth/wechat-login",
            
            // 商品相关（支持单数和复数形式）
            "/api/product",
            "/api/product/**",
            "/api/products",
            "/api/products/**",
            
            // 购物车相关
            "/api/cart",
            "/api/cart/**",
            
            // 订单相关
            "/api/order",
            "/api/order/**",
            "/api/orders",
            "/api/orders/**",
            
            // 支付相关
            "/api/payment/**",
            
            // 物流相关
            "/api/logistics",
            "/api/logistics/**",
            
            // 商家相关
            "/api/seller",
            "/api/seller/**",
            
            // 管理员相关
            "/api/admin",
            "/api/admin/**",
            
            // 其他
            "/api/feedback",
            "/api/feedback/**",
            "/api/user/favorite",
            "/api/user/favorite/**",
            "/payment/notify",
            "/wechat/notify",
            "/alipay/notify",
            "/error",
            "/actuator/**"
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
        // 记录所有匹配尝试，便于调试
        log.debug("JwtAuthFilter - Checking if path {} is allowed", path);
        
        // 直接添加特殊处理，确保/api/payment/mock/pay路径总是允许访问
        if ("/api/payment/mock/pay".equals(path)) {
            log.debug("JwtAuthFilter - Path {} is explicitly allowed", path);
            return true;
        }
        
        for (String allowedPath : ALLOW_PATHS) {
            // 使用AntPathMatcher进行路径匹配，确保与Spring Cloud Gateway的Path谓词使用相同的匹配逻辑
            if (pathMatcher.match(allowedPath, path)) {
                log.debug("JwtAuthFilter - Path {} matches allowed path {}", path, allowedPath);
                return true;
            }
        }
        log.warn("JwtAuthFilter - Path {} does not match any allowed path, access denied", path);
        return false;
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
}
