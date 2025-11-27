package com.anqigou.order.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.anqigou.order.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 订单项 Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
