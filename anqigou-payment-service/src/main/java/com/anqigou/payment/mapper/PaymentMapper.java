package com.anqigou.payment.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.anqigou.payment.entity.Payment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 支付 Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
