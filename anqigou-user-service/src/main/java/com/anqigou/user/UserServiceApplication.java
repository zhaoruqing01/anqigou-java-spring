package com.anqigou.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.anqigou"})
@EnableFeignClients(basePackages = {"com.anqigou.user.client"})
@MapperScan(basePackages = {"com.anqigou.user.mapper"})
public class UserServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
