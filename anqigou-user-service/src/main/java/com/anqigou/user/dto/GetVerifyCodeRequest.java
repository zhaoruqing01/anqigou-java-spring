package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取验证码请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetVerifyCodeRequest {
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 用途（register-注册，login-登录，reset-重置密码）
     */
    private String purpose;
}
