package com.anqigou.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 支付服务启动类
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.anqigou.payment"})
@ComponentScan(basePackages = {"com.anqigou"})
public class PaymentServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
