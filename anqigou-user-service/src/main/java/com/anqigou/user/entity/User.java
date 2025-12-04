package com.anqigou.user.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user")
public class User {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 微信用户ID
     */
    @TableField("wechat_open_id")
    private String wechatOpenId;
    
    /**
     * 微信UnionID
     */
    @TableField(exist = false)
    private String wechatUnionid;
    
    /**
     * 微信昵称
     */
    @TableField("wechat_nickname")
    private String wechatNickname;
    
    /**
     * 微信头像
     */
    @TableField("wechat_avatar")
    private String wechatAvatar;
    
    /**
     * 会员等级（0-普通，1-VIP等）
     */
    private Integer memberLevel;
    
    /**
     * 累计消费金额（单位：分）
     */
    private Long totalConsumption;
    
    /**
     * 可用积分
     */
    private Long availablePoints;
    
    /**
     * 账户状态（0-正常，1-禁用）
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 个性化推荐开关（0-关闭，1-开启）
     */
    private Integer personalizedRecommendation;
    
    /**
     * 位置授权开关（0-关闭，1-开启）
     */
    private Integer locationAuthorization;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
