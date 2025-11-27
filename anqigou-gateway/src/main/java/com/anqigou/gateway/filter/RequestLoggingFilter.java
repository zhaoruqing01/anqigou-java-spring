package com.anqigou.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求日志记录过滤器
 */
@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        long startTime = System.currentTimeMillis();
        
        log.info("请求开始 - 方法: {}, 路径: {}, 远程IP: {}", 
                request.getMethod(), 
                request.getPath(),
                request.getRemoteAddress());
        
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("请求结束 - 路径: {}, 耗时: {}ms", 
                            request.getPath(), 
                            duration);
                });
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
}
