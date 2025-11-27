package com.anqigou.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    
    private String id;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String detailAddress;
    
    private String fullAddress;
    
    private Boolean isDefault;
}
