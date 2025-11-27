package com.anqigou.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.anqigou.user.entity.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 地址数据访问层
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    
    /**
     * 查询用户的所有地址
     */
    List<Address> selectByUserId(@Param("userId") String userId);
    
    /**
     * 查询用户的默认地址
     */
    Address selectDefaultByUserId(@Param("userId") String userId);
}
