package com.anqigou.user.service;

import java.util.List;

import com.anqigou.user.dto.AddressDTO;

/**
 * 地址业务服务接口
 */
public interface AddressService {
    
    /**
     * 获取用户的所有地址
     */
    List<AddressDTO> getAddressList(String userId);
    
    /**
     * 获取地址详情
     */
    AddressDTO getAddressDetail(String addressId, String userId);
    
    /**
     * 创建地址
     */
    AddressDTO createAddress(String userId, AddressDTO addressDTO);
    
    /**
     * 更新地址
     */
    AddressDTO updateAddress(String addressId, String userId, AddressDTO addressDTO);
    
    /**
     * 删除地址
     */
    void deleteAddress(String addressId, String userId);
    
    /**
     * 设置默认地址
     */
    void setDefaultAddress(String addressId, String userId);
}
