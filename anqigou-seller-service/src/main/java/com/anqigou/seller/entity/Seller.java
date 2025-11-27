package com.anqigou.seller.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商家实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("seller")
public class Seller {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 店铺名称
     */
    private String shopName;
    
    /**
     * 店铺LOGO
     */
    private String shopLogo;
    
    /**
     * 店铺描述
     */
    private String shopDescription;
    
    /**
     * 营业执照号
     */
    private String licenseNo;
    
    /**
     * 营业执照图片
     */
    private String licenseImage;
    
    /**
     * 审核状态（pending-待审核、approved-已审核、rejected-已拒绝）
     */
    private String status;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 店铺等级（1-5级）
     */
    private Integer level;
    
    /**
     * 信用分（0-100）
     */
    private Double creditScore;
    
    /**
     * 总销售额
     */
    private Long totalSales;
    
    /**
     * 总订单数
     */
    private Integer totalOrders;
    
    /**
     * 好评率（%）
     */
    private Double goodCommentRate;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 联系邮箱
     */
    private String email;
    
    /**
     * 经营地址
     */
    private String businessAddress;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
