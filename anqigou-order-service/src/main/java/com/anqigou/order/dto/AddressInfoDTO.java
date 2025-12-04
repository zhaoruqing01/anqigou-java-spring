package com.anqigou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址信息DTO（用于订单服务）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfoDTO {
    
    /**
     * 地址ID
     */
    private String id;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人手机号
     */
    private String receiverPhone;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区/县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 是否默认地址
     */
    private Boolean isDefault;
    
    /**
     * 地址标签
     */
    private String tag;
    
    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
}
