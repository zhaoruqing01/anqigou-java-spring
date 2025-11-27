package com.anqigou.user.service;

import com.anqigou.user.dto.*;
import com.anqigou.user.entity.User;

/**
 * 用户认证服务接口
 */
public interface AuthService {
    
    /**
     * 获取验证码
     */
    void sendVerifyCode(String phone);
    
    /**
     * 注册用户
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * 密码登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 验证码登录
     */
    LoginResponse loginWithVerifyCode(VerifyCodeLoginRequest request);
    
    /**
     * 微信登录
     */
    LoginResponse wechatLogin(String code);
    
    /**
     * 获取用户信息
     */
    UserInfoDTO getUserInfo(String userId);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(String userId, UserInfoDTO userInfo);
    
    /**
     * 验证token
     */
    String validateToken(String token);
}
