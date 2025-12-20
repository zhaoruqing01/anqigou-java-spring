package com.anqigou.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 物流服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.anqigou"})
@EnableFeignClients(basePackages = {"com.anqigou"})
public class LogisticsServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LogisticsServiceApplication.class, args);
    }
}
