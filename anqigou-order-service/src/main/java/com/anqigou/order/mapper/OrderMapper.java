package com.anqigou.order.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.anqigou.order.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
