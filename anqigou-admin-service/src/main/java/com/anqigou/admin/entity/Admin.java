package com.anqigou.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("admin")
public class Admin {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 管理员用户名
     */
    private String username;
    
    /**
     * 管理员密码
     */
    private String password;
    
    /**
     * 管理员姓名
     */
    private String realName;
    
    /**
     * 角色（super-超级管理员、seller-商家审核员、product-商品审核员、data-数据分析员）
     */
    private String role;
    
    /**
     * 状态（active-活跃、inactive-停用）
     */
    private String status;
    
    /**
     * 联系邮箱
     */
    private String email;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
