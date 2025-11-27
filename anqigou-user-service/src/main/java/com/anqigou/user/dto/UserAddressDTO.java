package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户地址DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDTO {
    
    /**
     * 地址ID
     */
    private String id;
    
    /**
     * 收货人
     */
    private String receiverName;
    
    /**
     * 手机号（中间4位打码）
     */
    private String receiverPhone;
    
    /**
     * 省份
     */
    private String provinceName;
    
    /**
     * 城市
     */
    private String cityName;
    
    /**
     * 区县
     */
    private String districtName;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 地址标签
     */
    private String addressTag;
    
    /**
     * 是否为默认地址
     */
    private Boolean isDefault;
    
    /**
     * 完整地址
     */
    private String fullAddress;
}
