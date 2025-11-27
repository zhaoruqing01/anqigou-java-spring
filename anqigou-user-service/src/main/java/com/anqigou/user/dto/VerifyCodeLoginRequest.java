package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 验证码登录请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyCodeLoginRequest {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
