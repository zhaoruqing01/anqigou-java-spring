package com.anqigou.seller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商家服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.anqigou"})
public class SellerServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SellerServiceApplication.class, args);
    }
}
