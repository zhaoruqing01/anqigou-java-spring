package com.anqigou.order.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.anqigou.order.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 购物车 Mapper
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
