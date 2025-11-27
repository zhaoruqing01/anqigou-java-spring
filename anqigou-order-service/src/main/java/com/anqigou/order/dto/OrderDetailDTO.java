package com.anqigou.order.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDTO {
    
    /**
     * 订单ID
     */
    private String id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 商品总数
     */
    private Integer productCount;
    
    /**
     * 商品金额（单位：分）
     */
    private Long productAmount;
    
    /**
     * 配送费（单位：分）
     */
    private Long shippingFee;
    
    /**
     * 优惠金额（单位：分）
     */
    private Long discountAmount;
    
    /**
     * 订单总金额（单位：分）
     */
    private Long totalAmount;
    
    /**
     * 实际支付金额（单位：分）
     */
    private Long actualPayment;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 配送方式
     */
    private String shippingMethod;
    
    /**
     * 收货人
     */
    private String receiverName;
    
    /**
     * 收货人手机号
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间（时间戳）
     */
    private Long createTime;
    
    /**
     * 支付时间（时间戳）
     */
    private Long paidTime;
    
    /**
     * 发货时间（时间戳）
     */
    private Long shippedTime;
    
    /**
     * 签收时间（时间戳）
     */
    private Long signedTime;
    
    /**
     * 订单项列表
     */
    private List<OrderItemDTO> items;
}
