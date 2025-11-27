package com.anqigou.user.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 用户设备实体类
 */
@Data
@TableName("user_device")
public class UserDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型（如：android、ios、web）
     */
    private String deviceType;

    /**
     * 设备令牌
     */
    private String deviceToken;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date loginTime;

    /**
     * 最后活跃时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastActiveTime;

    /**
     * 状态（0-已下线，1-在线）
     */
    private Integer status;
}
