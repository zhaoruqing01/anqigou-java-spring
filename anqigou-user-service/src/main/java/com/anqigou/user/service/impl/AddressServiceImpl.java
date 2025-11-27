package com.anqigou.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.user.dto.AddressDTO;
import com.anqigou.user.entity.Address;
import com.anqigou.user.mapper.AddressMapper;
import com.anqigou.user.service.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 地址业务服务实现
 */
@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    public List<AddressDTO> getAddressList(String userId) {
        List<Address> addresses = addressMapper.selectByUserId(userId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AddressDTO getAddressDetail(String addressId, String userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id", addressId).eq("user_id", userId);
        Address address = addressMapper.selectOne(wrapper);
        return address != null ? convertToDTO(address) : null;
    }
    
    @Override
    public AddressDTO createAddress(String userId, AddressDTO addressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        
        // 生成完整地址
        generateFullAddress(address);
        
        // 如果是默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            QueryWrapper<Address> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId).eq("is_default", true);
            List<Address> defaults = addressMapper.selectList(wrapper);
            for (Address defaultAddr : defaults) {
                defaultAddr.setIsDefault(false);
                addressMapper.updateById(defaultAddr);
            }
        }
        
        addressMapper.insert(address);
        return convertToDTO(address);
    }
    
    @Override
    public AddressDTO updateAddress(String addressId, String userId, AddressDTO addressDTO) {
        Address address = getAddressEntity(addressId, userId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        BeanUtils.copyProperties(addressDTO, address, "id", "userId", "createTime");
        address.setUpdateTime(LocalDateTime.now());
        
        // 生成完整地址
        generateFullAddress(address);
        
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(addressDTO.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            QueryWrapper<Address> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId).eq("is_default", true);
            List<Address> defaults = addressMapper.selectList(wrapper);
            for (Address defaultAddr : defaults) {
                defaultAddr.setIsDefault(false);
                addressMapper.updateById(defaultAddr);
            }
        }
        
        addressMapper.updateById(address);
        return convertToDTO(address);
    }
    
    @Override
    public void deleteAddress(String addressId, String userId) {
        Address address = getAddressEntity(addressId, userId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        addressMapper.deleteById(addressId);
    }
    
    @Override
    public void setDefaultAddress(String addressId, String userId) {
        Address address = getAddressEntity(addressId, userId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 先取消其他默认地址
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_default", true);
        List<Address> defaults = addressMapper.selectList(wrapper);
        for (Address defaultAddr : defaults) {
            defaultAddr.setIsDefault(false);
            addressMapper.updateById(defaultAddr);
        }
        
        // 设置当前地址为默认
        address.setIsDefault(true);
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.updateById(address);
    }
    
    private Address getAddressEntity(String addressId, String userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id", addressId).eq("user_id", userId);
        return addressMapper.selectOne(wrapper);
    }
    
    /**
     * 生成完整地址
     */
    private void generateFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            sb.append(address.getCity());
        }
        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            sb.append(address.getDistrict());
        }
        if (address.getDetailAddress() != null && !address.getDetailAddress().isEmpty()) {
            sb.append(address.getDetailAddress());
        }
        address.setFullAddress(sb.toString());
    }
    
    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto);
        
        // 如果fullAddress为空，动态生成完整地址
        if (dto.getFullAddress() == null || dto.getFullAddress().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (dto.getProvince() != null && !dto.getProvince().isEmpty()) {
                sb.append(dto.getProvince());
            }
            if (dto.getCity() != null && !dto.getCity().isEmpty()) {
                sb.append(dto.getCity());
            }
            if (dto.getDistrict() != null && !dto.getDistrict().isEmpty()) {
                sb.append(dto.getDistrict());
            }
            if (dto.getDetailAddress() != null && !dto.getDetailAddress().isEmpty()) {
                sb.append(dto.getDetailAddress());
            }
            dto.setFullAddress(sb.toString());
        }
        
        return dto;
    }
}
