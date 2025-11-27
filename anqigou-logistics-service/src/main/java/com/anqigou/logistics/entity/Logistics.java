package com.anqigou.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流信息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("logistics")
public class Logistics {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 快递公司（sf-顺丰、zto-中通、yt-圆通等）
     */
    private String courierCompany;
    
    /**
     * 快递单号
     */
    private String trackingNo;
    
    /**
     * 发货人省份
     */
    private String senderProvince;
    
    /**
     * 发货人城市
     */
    private String senderCity;
    
    /**
     * 发货人地址
     */
    private String senderAddress;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货人省份
     */
    private String receiverProvince;
    
    /**
     * 收货人城市
     */
    private String receiverCity;
    
    /**
     * 收货人地址
     */
    private String receiverAddress;
    
    /**
     * 物流状态（pending-待发货、shipped-已发货、transit-运输中、delivering-派送中、signed-已签收、exception-异常）
     */
    private String status;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippedTime;
    
    /**
     * 签收时间
     */
    private LocalDateTime signedTime;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
