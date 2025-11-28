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
 * 售后日志表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("after_sale_log")
public class AfterSaleLog {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 售后申请ID
     */
    private String afterSaleId;
    
    /**
     * 操作人ID
     */
    private String operatorId;
    
    /**
     * 操作人类型（user-用户，seller-商家，admin-平台）
     */
    private String operatorType;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 操作内容
     */
    private String content;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}