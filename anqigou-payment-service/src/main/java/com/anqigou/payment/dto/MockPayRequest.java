package com.anqigou.payment.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模拟支付请求 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MockPayRequest {
    
    /**
     * 订单ID - 必填
     */
    @NotBlank(message = "订单ID不能为空")
    private String orderId;
    
    /**
     * 支付金额（单位：分）- 必填
     */
    @NotNull(message = "支付金额不能为空")
    @Min(value = 1, message = "支付金额必须大于0")
    private Long amount;
    
    /**
     * 支付方式（wechat、alipay、mock）- 可选，默认为mock
     */
    private String paymentMethod;
    
    /**
     * 用户ID - 可选，如果不传则从订单服务获取
     */
    private String userId;
    
    /**
     * 商品描述 - 可选
     */
    private String description;
    
    /**
     * 是否自动发货 - 可选，默认true，30秒后自动发货
     */
    private Boolean autoShip;
}
