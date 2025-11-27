-- =============================================
-- 安琦购电商平台数据库初始化脚本
-- =============================================

-- 用户表
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL COMMENT '用户ID',
  `phone` varchar(11) UNIQUE NOT NULL COMMENT '手机号',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `avatar` varchar(500) COMMENT '头像URL',
  `password` varchar(255) COMMENT '密码（加密存储）',
  `wechat_open_id` varchar(100) UNIQUE COMMENT '微信OpenID',
  `wechat_nickname` varchar(50) COMMENT '微信昵称',
  `wechat_avatar` varchar(500) COMMENT '微信头像',
  `member_level` tinyint DEFAULT 0 COMMENT '会员等级（0-普通会员）',
  `total_consumption` bigint DEFAULT 0 COMMENT '累计消费金额（单位：分）',
  `available_points` bigint DEFAULT 0 COMMENT '可用积分',
  `status` tinyint DEFAULT 0 COMMENT '账户状态（0-正常，1-禁用）',
  `last_login_time` datetime COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COMMENT '最后登录IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_wechat_open_id` (`wechat_open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户地址表
CREATE TABLE `user_address` (
  `id` varchar(36) NOT NULL COMMENT '地址ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(11) NOT NULL COMMENT '收货人手机号',
  `province_code` varchar(10) COMMENT '省份编码',
  `province_name` varchar(50) NOT NULL COMMENT '省份',
  `city_code` varchar(10) COMMENT '城市编码',
  `city_name` varchar(50) NOT NULL COMMENT '城市',
  `district_code` varchar(10) COMMENT '区县编码',
  `district_name` varchar(50) NOT NULL COMMENT '区县',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `postal_code` varchar(6) COMMENT '邮政编码',
  `address_tag` varchar(20) COMMENT '地址标签（家、公司等）',
  `is_default` tinyint DEFAULT 0 COMMENT '是否为默认地址（0-否，1-是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 商品分类表
CREATE TABLE `product_category` (
  `id` varchar(36) NOT NULL COMMENT '分类ID',
  `parent_id` varchar(36) COMMENT '父分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `icon_url` varchar(500) COMMENT '分类图标URL',
  `description` varchar(500) COMMENT '分类描述',
  `level` tinyint NOT NULL COMMENT '分类级别（1-一级，2-二级，3-三级）',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE `product` (
  `id` varchar(36) NOT NULL COMMENT '商品ID',
  `seller_id` varchar(36) NOT NULL COMMENT '商家ID',
  `category_id` varchar(36) NOT NULL COMMENT '分类ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `brand` varchar(100) COMMENT '品牌',
  `description` longtext COMMENT '商品描述',
  `price` bigint NOT NULL COMMENT '售价（单位：分）',
  `original_price` bigint NOT NULL COMMENT '原价（单位：分）',
  `stock` int DEFAULT 0 COMMENT '库存',
  `sold_count` int DEFAULT 0 COMMENT '销售数量',
  `rating` decimal(3,2) DEFAULT 0 COMMENT '评分（0-5分）',
  `rating_count` int DEFAULT 0 COMMENT '评价数',
  `main_image` varchar(500) NOT NULL COMMENT '主图URL',
  `images` longtext COMMENT '商品图片（JSON数组）',
  `video_url` varchar(500) COMMENT '商品视频URL',
  `status` tinyint DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
  `shelf_time` datetime COMMENT '上架时间',
  `shelf_off_time` datetime COMMENT '下架时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  FULLTEXT KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 商品规格表
CREATE TABLE `product_sku` (
  `id` varchar(36) NOT NULL COMMENT 'SKU ID',
  `product_id` varchar(36) NOT NULL COMMENT '商品ID',
  `spec_name` varchar(100) NOT NULL COMMENT '规格名称（如颜色+尺寸）',
  `spec_value_json` longtext NOT NULL COMMENT '规格值JSON（如{color: 红色, size: L}）',
  `price` bigint NOT NULL COMMENT '该规格价格（单位：分）',
  `stock` int DEFAULT 0 COMMENT '该规格库存',
  `barcode` varchar(100) COMMENT '条码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格表';

-- 购物车表
CREATE TABLE `shopping_cart` (
  `id` varchar(36) NOT NULL COMMENT '购物车ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `product_id` varchar(36) NOT NULL COMMENT '商品ID',
  `sku_id` varchar(36) NOT NULL COMMENT 'SKU ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '购买数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product_sku` (`user_id`, `product_id`, `sku_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表
CREATE TABLE `orders` (
  `id` varchar(36) NOT NULL COMMENT '订单ID',
  `order_no` varchar(20) UNIQUE NOT NULL COMMENT '订单号',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `seller_id` varchar(36) NOT NULL COMMENT '商家ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(11) NOT NULL COMMENT '收货人手机号',
  `receiver_address` varchar(500) NOT NULL COMMENT '收货地址',
  `product_count` int NOT NULL COMMENT '商品总数',
  `product_amount` bigint NOT NULL COMMENT '商品金额（单位：分）',
  `shipping_fee` bigint DEFAULT 0 COMMENT '配送费（单位：分）',
  `discount_amount` bigint DEFAULT 0 COMMENT '优惠金额（单位：分）',
  `total_amount` bigint NOT NULL COMMENT '订单总金额（单位：分）',
  `actual_payment` bigint NOT NULL COMMENT '实际支付金额（单位：分）',
  `payment_method` varchar(20) COMMENT '支付方式（weixin、alipay）',
  `shipping_method` varchar(20) DEFAULT 'normal' COMMENT '配送方式（normal-标准、express-加急、pickup-自提）',
  `status` varchar(20) DEFAULT 'pending_payment' COMMENT '订单状态',
  `remark` varchar(500) COMMENT '订单备注',
  `paid_time` datetime COMMENT '支付时间',
  `shipped_time` datetime COMMENT '发货时间',
  `signed_time` datetime COMMENT '签收时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单项表
CREATE TABLE `order_item` (
  `id` varchar(36) NOT NULL COMMENT '订单项ID',
  `order_id` varchar(36) NOT NULL COMMENT '订单ID',
  `product_id` varchar(36) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `sku_id` varchar(36) NOT NULL COMMENT 'SKU ID',
  `spec_info` varchar(500) COMMENT '规格信息',
  `unit_price` bigint NOT NULL COMMENT '单价（单位：分）',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `subtotal` bigint NOT NULL COMMENT '小计（单位：分）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- 支付记录表
CREATE TABLE `payment` (
  `id` varchar(36) NOT NULL COMMENT '支付ID',
  `order_id` varchar(36) NOT NULL COMMENT '订单ID',
  `order_no` varchar(20) NOT NULL COMMENT '订单号',
  `payment_no` varchar(100) UNIQUE NOT NULL COMMENT '支付单号',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `amount` bigint NOT NULL COMMENT '支付金额（单位：分）',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式（weixin、alipay）',
  `status` varchar(20) DEFAULT 'pending' COMMENT '支付状态（pending-待支付，paid-已支付，failed-失败，refunded-已退款）',
  `transaction_id` varchar(100) COMMENT '第三方交易ID',
  `paid_time` datetime COMMENT '支付完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 用户收藏表
CREATE TABLE `user_favorite` (
  `id` varchar(36) NOT NULL COMMENT '收藏ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `product_id` varchar(36) NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 商品评价表
CREATE TABLE `product_review` (
  `id` varchar(36) NOT NULL COMMENT '评价ID',
  `product_id` varchar(36) NOT NULL COMMENT '商品ID',
  `order_id` varchar(36) NOT NULL COMMENT '订单ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `rating` tinyint NOT NULL COMMENT '评分（1-5星）',
  `content` longtext COMMENT '评价内容',
  `images` longtext COMMENT '评价图片（JSON数组）',
  `is_anonymous` tinyint DEFAULT 0 COMMENT '是否匿名（0-否，1-是）',
  `helpful_count` int DEFAULT 0 COMMENT '有用数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- 搜索历史表
CREATE TABLE `search_history` (
  `id` varchar(36) NOT NULL COMMENT '历史ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';

-- 商家表
CREATE TABLE `seller` (
  `id` varchar(36) NOT NULL COMMENT '商家ID',
  `user_id` varchar(36) UNIQUE NOT NULL COMMENT '关联用户ID',
  `shop_name` varchar(100) NOT NULL COMMENT '店铺名称',
  `shop_logo` varchar(500) COMMENT '店铺logo',
  `description` longtext COMMENT '店铺描述',
  `license_no` varchar(50) UNIQUE NOT NULL COMMENT '营业执照号',
  `license_image` varchar(500) COMMENT '营业执照图片',
  `legal_person_name` varchar(50) NOT NULL COMMENT '法人姓名',
  `legal_person_id` varchar(50) UNIQUE NOT NULL COMMENT '法人身份证号',
  `bank_account` varchar(50) UNIQUE NOT NULL COMMENT '银行账户',
  `bank_account_name` varchar(50) NOT NULL COMMENT '银行账户名',
  `status` tinyint DEFAULT 0 COMMENT '状态（0-待审核，1-已认证，2-审核驳回，3-禁用）',
  `reject_reason` varchar(500) COMMENT '驳回原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家表';

-- 创建索引以提高查询性能
CREATE INDEX idx_product_seller ON product(seller_id);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_payment_order ON payment(order_id);
