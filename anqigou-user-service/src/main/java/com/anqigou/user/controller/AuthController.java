package com.anqigou.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.dto.LoginRequest;
import com.anqigou.user.dto.LoginResponse;
import com.anqigou.user.dto.RegisterRequest;
import com.anqigou.user.dto.UserInfoDTO;
import com.anqigou.user.dto.VerifyCodeLoginRequest;
import com.anqigou.user.service.AuthService;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 获取验证码
     */
    @PostMapping("/send-code")
    public ApiResponse<String> sendVerifyCode(@RequestParam String phone) {
        authService.sendVerifyCode(phone);
        return ApiResponse.success("验证码已发送");
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody @Validated RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ApiResponse.success("注册成功", response);
    }
    
    /**
     * 密码登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 验证码登录
     */
    @PostMapping("/login-with-code")
    public ApiResponse<LoginResponse> loginWithVerifyCode(@RequestBody @Validated VerifyCodeLoginRequest request) {
        LoginResponse response = authService.loginWithVerifyCode(request);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 微信登录
     */
    @PostMapping("/wechat-login")
    public ApiResponse<LoginResponse> wechatLogin(@RequestParam String code) {
        LoginResponse response = authService.wechatLogin(code);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/user-info")
    public ApiResponse<UserInfoDTO> getUserInfo(@RequestAttribute String userId) {
        UserInfoDTO userInfo = authService.getUserInfo(userId);
        return ApiResponse.success(userInfo);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/user-info")
    public ApiResponse<String> updateUserInfo(@RequestAttribute String userId, @RequestBody UserInfoDTO userInfo) {
        authService.updateUserInfo(userId, userInfo);
        return ApiResponse.success("更新成功");
    }
}
