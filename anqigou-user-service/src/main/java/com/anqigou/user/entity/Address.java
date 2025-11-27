package com.anqigou.user.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收货地址实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_address")
public class Address {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    private String userId;
    
    private String receiverName;
    
    private String receiverPhone;
    
    @TableField("province_name")
    private String province;
    
    @TableField("city_name")
    private String city;
    
    @TableField("district_name")
    private String district;
    
    private String detailAddress;
    
    @TableField(exist = false)
    private String fullAddress;
    
    private Boolean isDefault;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
