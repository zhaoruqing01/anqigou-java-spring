package com.anqigou.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                        .path("/api/auth/**", "/api/user/**")
                        .uri("lb://anqigou-user-service"))
                
                // 商品服务路由
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .uri("lb://anqigou-product-service"))
                
                // 订单服务路由
                .route("order-service", r -> r
                        .path("/api/order/**", "/api/orders/**", "/api/cart/**")
                        .uri("lb://anqigou-order-service"))
                
                // 支付服务路由
                .route("payment-service", r -> r
                        .path("/api/payment/**")
                        .uri("lb://anqigou-payment-service"))
                
                // 物流服务路由
                .route("logistics-service", r -> r
                        .path("/api/logistics/**")
                        .uri("lb://anqigou-logistics-service"))
                
                // 商家服务路由
                .route("seller-service", r -> r
                        .path("/api/seller/**")
                        .uri("lb://anqigou-seller-service"))
                
                // 管理员服务路由
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .uri("lb://anqigou-admin-service"))
                
                .build();
    }
}
