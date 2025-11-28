package com.anqigou.order.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.anqigou.order.entity.CartItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface CartMapper extends BaseMapper<CartItem> {
}
