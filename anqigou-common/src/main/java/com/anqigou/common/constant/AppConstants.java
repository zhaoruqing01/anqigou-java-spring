package com.anqigou.common.constant;

/**
 * 应用常量
 */
public class AppConstants {
    
    /**
     * 响应码常量
     */
    public static class ResponseCode {
        public static final int SUCCESS = 0;
        public static final int INVALID_PARAM = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int SERVER_ERROR = 500;
    }
    
    /**
     * Redis Key 前缀
     */
    public static class RedisKeyPrefix {
        /**
         * 登录令牌前缀
         */
        public static final String LOGIN_TOKEN = "login:token:";
        
        /**
         * 验证码前缀
         */
        public static final String VERIFY_CODE = "verify:code:";
        
        /**
         * 热门商品前缀
         */
        public static final String HOT_PRODUCTS = "hot:products:";
        
        /**
         * 购物车前缀
         */
        public static final String SHOPPING_CART = "cart:";
        
        /**
         * 库存前缀
         */
        public static final String INVENTORY = "inventory:";
        
        /**
         * 用户收藏前缀
         */
        public static final String USER_FAVORITE = "favorite:user:";
    }
    
    /**
     * 默认值
     */
    public static class Defaults {
        /**
         * Token 过期时间（7天）
         */
        public static final Long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
        
        /**
         * 验证码过期时间（15分钟）
         */
        public static final Long VERIFY_CODE_EXPIRE_TIME = 15 * 60 * 1000L;
        
        /**
         * 订单待支付过期时间（15分钟）
         */
        public static final Long ORDER_PAYMENT_EXPIRE_TIME = 15 * 60 * 1000L;
        
        /**
         * 订单库存冻结时间（15分钟）
         */
        public static final Long ORDER_INVENTORY_LOCK_TIME = 15 * 60 * 1000L;
        
        /**
         * 默认每页记录数
         */
        public static final Integer DEFAULT_PAGE_SIZE = 20;
        
        /**
         * 最大每页记录数
         */
        public static final Integer MAX_PAGE_SIZE = 100;
    }
    
    /**
     * 订单状态
     */
    public static class OrderStatus {
        /**
         * 待付款
         */
        public static final String PENDING_PAYMENT = "pending_payment";
        
        /**
         * 待发货
         */
        public static final String PENDING_SHIPPED = "pending_shipped";
        
        /**
         * 待收货
         */
        public static final String PENDING_RECEIPT = "pending_receipt";
        
        /**
         * 已完成
         */
        public static final String COMPLETED = "completed";
        
        /**
         * 已取消
         */
        public static final String CANCELLED = "cancelled";
    }
    
    /**
     * 支付状态
     */
    public static class PaymentStatus {
        /**
         * 待支付
         */
        public static final String PENDING = "pending";
        
        /**
         * 已支付
         */
        public static final String PAID = "paid";
        
        /**
         * 支付失败
         */
        public static final String FAILED = "failed";
        
        /**
         * 已退款
         */
        public static final String REFUNDED = "refunded";
    }
    
    /**
     * 物流状态
     */
    public static class LogisticsStatus {
        /**
         * 待发货
         */
        public static final String PENDING = "pending";
        
        /**
         * 已发货
         */
        public static final String SHIPPED = "shipped";
        
        /**
         * 运输中
         */
        public static final String TRANSIT = "transit";
        
        /**
         * 派送中
         */
        public static final String DELIVERING = "delivering";
        
        /**
         * 已签收
         */
        public static final String SIGNED = "signed";
        
        /**
         * 异常
         */
        public static final String EXCEPTION = "exception";
    }
}
