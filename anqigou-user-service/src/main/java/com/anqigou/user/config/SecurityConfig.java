package com.anqigou.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 * 配置用户服务的安全策略
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护
            .csrf().disable()
            // 禁用session管理
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置CORS
            .cors().configurationSource(corsConfigurationSource())
            .and()
            // 配置请求授权规则
            .authorizeHttpRequests(authz -> authz
                // 允许匿名访问的路径
                .requestMatchers(
                    new AntPathRequestMatcher("/api/auth/register"),
                    new AntPathRequestMatcher("/api/auth/login"),
                    new AntPathRequestMatcher("/api/auth/send-code"),
                    new AntPathRequestMatcher("/api/auth/login-with-code"),
                    new AntPathRequestMatcher("/api/auth/wechat-login"),
                    new AntPathRequestMatcher("/api/user/address/**"),
                    new AntPathRequestMatcher("/error")
                ).permitAll()
                // 健康检查端点
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 禁用表单登录
            .formLogin().disable()
            // 禁用HTTP基本认证
            .httpBasic().disable()
            // 禁用默认的登录页面
            .logout().disable();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}