package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDTO {
    
    /**
     * 用户ID
     */
    private String id;
    
    /**
     * 手机号（中间4位打码）
     */
    private String phone;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 会员等级
     */
    private Integer memberLevel;
    
    /**
     * 累计消费金额
     */
    private Long totalConsumption;
    
    /**
     * 可用积分
     */
    private Long availablePoints;
    
    /**
     * 最后登录时间
     */
    private Long lastLoginTime;
}
