package com.anqigou.gateway.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 网关错误处理过滤器
 * 捕获所有错误并返回统一的错误响应
 */
@Component
@Slf4j
public class ErrorHandlingFilter implements GlobalFilter, Ordered {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(ex -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    
                    // 记录详细的错误日志，方便调试
                    log.error("网关错误 - 路径: {}, 方法: {}, 错误信息: {}", 
                            exchange.getRequest().getPath(),
                            exchange.getRequest().getMethod(),
                            ex.getMessage(), ex);
                    
                    // 构造错误响应体
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("code", 500);
                    responseBody.put("message", "服务暂时不可用,请稍后重试");
                    responseBody.put("path", exchange.getRequest().getPath().value());
                    responseBody.put("timestamp", System.currentTimeMillis());
                    responseBody.put("error", ex.getMessage());
                    
                    try {
                        byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
                        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
                        return response.writeWith(Mono.just(dataBuffer));
                    } catch (IOException ioEx) {
                        log.error("序列化错误响应失败", ioEx);
                        return Mono.error(ioEx);
                    }
                });
    }
    
    @Override
    public int getOrder() {
        // 在RequestLoggingFilter之后执行
        return -2;
    }
}
