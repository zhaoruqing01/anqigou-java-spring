package com.anqigou.order.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 售后申请表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("after_sale_apply")
public class AfterSaleApply {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单项ID
     */
    private String orderItemId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 商家ID
     */
    private String sellerId;
    
    /**
     * 售后类型（refund-仅退款，return_refund-退货退款，exchange-换货）
     */
    private String type;
    
    /**
     * 售后原因
     */
    private String reason;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 申请金额（单位：分）
     */
    private Long amount;
    
    /**
     * 凭证图片（JSON数组）
     */
    private String images;
    
    /**
     * 售后状态（pending-待审核，approved-已同意，rejected-已拒绝，processing-处理中，completed-已完成，cancelled-已取消）
     */
    private String status;
    
    /**
     * 实际退款金额（单位：分）
     */
    private Long refundAmount;
    
    /**
     * 快递公司
     */
    private String expressCompany;
    
    /**
     * 快递单号
     */
    private String expressNo;
    
    /**
     * 商家备注
     */
    private String sellerRemark;
    
    /**
     * 平台备注
     */
    private String adminRemark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private Integer deleted;
}