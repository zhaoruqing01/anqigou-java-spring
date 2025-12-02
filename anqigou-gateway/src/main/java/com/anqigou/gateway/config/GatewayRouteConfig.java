package com.anqigou.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 网关路由配置
 */
@Configuration
public class GatewayRouteConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户服务路由
                .route("user-service", r -> r
                        .path("/api/auth/**", "/api/user/**", "/api/feedback/**", "/api/favorite/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-user-service"))
                
                // 商品服务路由
                .route("product-service", r -> r
                        .path("/api/products/**", "/api/product/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-product-service"))
                
                // 订单服务路由
                .route("order-service", r -> r
                        .path("/api/order/**", "/api/orders/**", "/api/cart/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-order-service"))
                
                // 支付服务路由
                .route("payment-service", r -> r
                        .path("/api/payment/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-payment-service"))
                
                // 物流服务路由
                .route("logistics-service", r -> r
                        .path("/api/logistics/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-logistics-service"))
                
                // 商家服务路由
                .route("seller-service", r -> r
                        .path("/api/seller/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-seller-service"))
                
                // 管理员服务路由
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://anqigou-admin-service"))
                
                .build();
    }
    
    /**
     * IP地址限流键解析器
     * @return KeyResolver
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }
}
