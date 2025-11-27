package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 登录令牌
     */
    private String token;
    
    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 令牌过期时间（毫秒）
     */
    private Long expiresIn;
}
