package com.anqigou.user.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.dto.AddressDTO;
import com.anqigou.user.service.AddressService;

/**
 * 地址管理控制器
 */
@RestController
@RequestMapping("/user/address")
@Validated
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 获取用户的地址列表
     */
    @GetMapping("/list")
    public ApiResponse<List<AddressDTO>> getAddressList(@RequestAttribute(name = "userId", required = false) String userId) {
        // 如果userId为null，返回空列表
        if (userId == null) {
            return ApiResponse.success(Collections.emptyList());
        }
        List<AddressDTO> addresses = addressService.getAddressList(userId);
        return ApiResponse.success(addresses);
    }
    
    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    public ApiResponse<AddressDTO> getAddressDetail(@PathVariable String addressId,
                                                    @RequestAttribute(name = "userId", required = false) String userId) {
        // 如果userId为null，返回错误
        if (userId == null) {
            return ApiResponse.failure(401, "用户未登录");
        }
        AddressDTO address = addressService.getAddressDetail(addressId, userId);
        return ApiResponse.success(address);
    }
    
    /**
     * 创建地址
     */
    @PostMapping
    public ApiResponse<AddressDTO> createAddress(@RequestAttribute(name = "userId", required = false) String userId,
                                                 @RequestBody AddressDTO addressDTO) {
        // 如果userId为null，返回错误
        if (userId == null) {
            return ApiResponse.failure(401, "用户未登录");
        }
        AddressDTO address = addressService.createAddress(userId, addressDTO);
        return ApiResponse.success("地址创建成功", address);
    }
    
    /**
     * 创建地址（兼容旧路径）
     */
    @PostMapping("/create")
    public ApiResponse<AddressDTO> createAddressOld(@RequestAttribute(name = "userId", required = false) String userId,
                                                 @RequestBody AddressDTO addressDTO) {
        return createAddress(userId, addressDTO);
    }
    
    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    public ApiResponse<AddressDTO> updateAddress(@PathVariable String addressId,
                                                 @RequestAttribute(name = "userId", required = false) String userId,
                                                 @RequestBody AddressDTO addressDTO) {
        // 如果userId为null，返回错误
        if (userId == null) {
            return ApiResponse.failure(401, "用户未登录");
        }
        AddressDTO address = addressService.updateAddress(addressId, userId, addressDTO);
        return ApiResponse.success("地址更新成功", address);
    }
    
    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    public ApiResponse<String> deleteAddress(@PathVariable String addressId,
                                          @RequestAttribute(name = "userId", required = false) String userId) {
        // 如果userId为null，返回错误
        if (userId == null) {
            return ApiResponse.failure(401, "用户未登录");
        }
        addressService.deleteAddress(addressId, userId);
        return ApiResponse.success("地址删除成功");
    }
    
    /**
     * 设置默认地址
     */
    @PostMapping("/{addressId}/set-default")
    public ApiResponse<String> setDefaultAddress(@PathVariable String addressId,
                                              @RequestAttribute(name = "userId", required = false) String userId) {
        // 如果userId为null，返回错误
        if (userId == null) {
            return ApiResponse.failure(401, "用户未登录");
        }
        addressService.setDefaultAddress(addressId, userId);
        return ApiResponse.success("默认地址设置成功");
    }
}
