-- =============================================
-- 安琦购电商平台测试数据（完整版 - 丰富数据）
-- =============================================

-- 清空现有数据（可选）
-- TRUNCATE TABLE product_category;
-- TRUNCATE TABLE product;
-- TRUNCATE TABLE product_sku;
-- TRUNCATE TABLE seller;
-- TRUNCATE TABLE user;

-- =============================================
-- 1. 用户测试数据（10条）
-- =============================================
INSERT INTO `user` (`id`, `phone`, `nickname`, `avatar`, `password`, `wechat_open_id`, `wechat_nickname`, `wechat_avatar`, `member_level`, `total_consumption`, `available_points`, `status`, `last_login_time`, `last_login_ip`, `personalized_recommendation`, `location_authorization`, `create_time`, `update_time`, `deleted`)
VALUES
('user-001', '13800138001', '时尚达人小美', 'https://picsum.photos/100/100?random=1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_001', '小美', 'https://picsum.photos/100/100?random=1', 0, 158900, 1589, 0, NOW(), '192.168.1.100', 1, 1, NOW(), NOW(), 0),
('user-002', '13800138002', '数码极客老王', 'https://picsum.photos/100/100?random=2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_002', '老王', 'https://picsum.photos/100/100?random=2', 0, 289900, 2899, 0, NOW(), '192.168.1.101', 1, 1, NOW(), NOW(), 0),
('user-003', '13800138003', '美妆达人Lisa', 'https://picsum.photos/100/100?random=3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_003', 'Lisa', 'https://picsum.photos/100/100?random=3', 0, 456900, 4569, 0, NOW(), '192.168.1.102', 1, 1, NOW(), NOW(), 0),
('user-004', '13800138004', '居家生活小张', 'https://picsum.photos/100/100?random=4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_004', '小张', 'https://picsum.photos/100/100?random=4', 0, 189900, 1899, 0, NOW(), '192.168.1.103', 1, 1, NOW(), NOW(), 0),
('user-005', '13800138005', '健康饮食小李', 'https://picsum.photos/100/100?random=5', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_005', '小李', 'https://picsum.photos/100/100?random=5', 0, 234900, 2349, 0, NOW(), '192.168.1.104', 1, 1, NOW(), NOW(), 0),
('user-006', '13800138006', '运动健身小刚', 'https://picsum.photos/100/100?random=6', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_006', '小刚', 'https://picsum.photos/100/100?random=6', 0, 345900, 3459, 0, NOW(), '192.168.1.105', 1, 1, NOW(), NOW(), 0),
('user-007', '13800138007', '母婴专家小红', 'https://picsum.photos/100/100?random=7', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_007', '小红', 'https://picsum.photos/100/100?random=7', 0, 567900, 5679, 0, NOW(), '192.168.1.106', 1, 1, NOW(), NOW(), 0),
('user-008', '13800138008', '图书爱好者小文', 'https://picsum.photos/100/100?random=8', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_008', '小文', 'https://picsum.photos/100/100?random=8', 0, 123900, 1239, 0, NOW(), '192.168.1.107', 1, 1, NOW(), NOW(), 0),
('user-009', '13800138009', '宠物主人小宠', 'https://picsum.photos/100/100?random=9', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_009', '小宠', 'https://picsum.photos/100/100?random=9', 0, 89900, 899, 0, NOW(), '192.168.1.108', 1, 1, NOW(), NOW(), 0),
('user-010', '13800138010', '新用户小新', 'https://picsum.photos/100/100?random=10', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'wx_010', '小新', 'https://picsum.photos/100/100?random=10', 0, 0, 0, 0, NOW(), '192.168.1.109', 1, 1, NOW(), NOW(), 0);

-- =============================================
-- 2. 商家测试数据（5条）
-- =============================================
INSERT INTO `seller` (`id`, `user_id`, `shop_name`, `shop_logo`, `description`, `license_no`, `license_image`, `legal_person_name`, `legal_person_id`, `bank_account`, `bank_account_name`, `status`, `reject_reason`, `create_time`, `update_time`, `deleted`)
VALUES
('seller-001', 'user-001', '时尚服饰旗舰店', 'https://picsum.photos/200/200?random=11', '引领时尚潮流，品质服饰专家', '91110000123456789A', 'https://picsum.photos/400/300?random=111', '张三', '110101199001011234', '6222000012345678901', '张三', 1, NULL, NOW(), NOW(), 0),
('seller-002', 'user-002', '优品数码3C专营店', 'https://picsum.photos/200/200?random=12', '专注高品质数码产品，正品保障', '91110000234567890B', 'https://picsum.photos/400/300?random=112', '李四', '110101199102022345', '6222000023456789012', '李四', 1, NULL, NOW(), NOW(), 0),
('seller-003', 'user-003', '美妆护肤精选店', 'https://picsum.photos/200/200?random=13', '精选全球美妆护肤产品，呵护你的美', '91110000345678901C', 'https://picsum.photos/400/300?random=113', '王五', '110101199203033456', '6222000034567890123', '王五', 1, NULL, NOW(), NOW(), 0),
('seller-004', 'user-004', '品质家居生活馆', 'https://picsum.photos/200/200?random=14', '品质家居，温馨生活每一天', '91110000456789012D', 'https://picsum.photos/400/300?random=114', '赵六', '110101199304044567', '6222000045678901234', '赵六', 1, NULL, NOW(), NOW(), 0),
('seller-005', 'user-005', '健康美食天地', 'https://picsum.photos/200/200?random=15', '新鲜食材，健康美味，品质保证', '91110000567890123E', 'https://picsum.photos/400/300?random=115', '钱七', '110101199405055678', '6222000056789012345', '钱七', 1, NULL, NOW(), NOW(), 0);

-- =============================================
-- 3. 商品分类测试数据（三级分类结构）
-- =============================================

-- 一级分类（5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-1-001', NULL, '服装鞋包', 'https://picsum.photos/60/60?random=21', '时尚服装鞋包类目', 1, 1, 1, NOW(), NOW(), 0),
('cat-1-002', NULL, '美妆护肤', 'https://picsum.photos/60/60?random=22', '美妆护肤类目', 1, 2, 1, NOW(), NOW(), 0),
('cat-1-003', NULL, '家电数码', 'https://picsum.photos/60/60?random=23', '家电数码类目', 1, 3, 1, NOW(), NOW(), 0),
('cat-1-004', NULL, '食品生鲜', 'https://picsum.photos/60/60?random=24', '食品生鲜类目', 1, 4, 1, NOW(), NOW(), 0),
('cat-1-005', NULL, '家居生活', 'https://picsum.photos/60/60?random=25', '家居生活类目', 1, 5, 1, NOW(), NOW(), 0);

-- 二级分类（服装鞋包下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-2-001', 'cat-1-001', '女装', 'https://picsum.photos/48/48?random=31', '女装类目', 2, 1, 1, NOW(), NOW(), 0),
('cat-2-002', 'cat-1-001', '男装', 'https://picsum.photos/48/48?random=32', '男装类目', 2, 2, 1, NOW(), NOW(), 0),
('cat-2-003', 'cat-1-001', '童装', 'https://picsum.photos/48/48?random=33', '童装类目', 2, 3, 1, NOW(), NOW(), 0),
('cat-2-004', 'cat-1-001', '鞋靴', 'https://picsum.photos/48/48?random=34', '鞋靴类目', 2, 4, 1, NOW(), NOW(), 0),
('cat-2-005', 'cat-1-001', '箱包', 'https://picsum.photos/48/48?random=35', '箱包类目', 2, 5, 1, NOW(), NOW(), 0);

-- 二级分类（美妆护肤下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-2-006', 'cat-1-002', '面部护理', 'https://picsum.photos/48/48?random=36', '面部护理类目', 2, 1, 1, NOW(), NOW(), 0),
('cat-2-007', 'cat-1-002', '彩妆', 'https://picsum.photos/48/48?random=37', '彩妆类目', 2, 2, 1, NOW(), NOW(), 0),
('cat-2-008', 'cat-1-002', '身体护理', 'https://picsum.photos/48/48?random=38', '身体护理类目', 2, 3, 1, NOW(), NOW(), 0),
('cat-2-009', 'cat-1-002', '香水', 'https://picsum.photos/48/48?random=39', '香水类目', 2, 4, 1, NOW(), NOW(), 0),
('cat-2-010', 'cat-1-002', '美容工具', 'https://picsum.photos/48/48?random=40', '美容工具类目', 2, 5, 1, NOW(), NOW(), 0);

-- 二级分类（家电数码下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-2-011', 'cat-1-003', '手机通讯', 'https://picsum.photos/48/48?random=41', '手机通讯类目', 2, 1, 1, NOW(), NOW(), 0),
('cat-2-012', 'cat-1-003', '电脑办公', 'https://picsum.photos/48/48?random=42', '电脑办公类目', 2, 2, 1, NOW(), NOW(), 0),
('cat-2-013', 'cat-1-003', '家用电器', 'https://picsum.photos/48/48?random=43', '家用电器类目', 2, 3, 1, NOW(), NOW(), 0),
('cat-2-014', 'cat-1-003', '摄影摄像', 'https://picsum.photos/48/48?random=44', '摄影摄像类目', 2, 4, 1, NOW(), NOW(), 0),
('cat-2-015', 'cat-1-003', '智能设备', 'https://picsum.photos/48/48?random=45', '智能设备类目', 2, 5, 1, NOW(), NOW(), 0);

-- 二级分类（食品生鲜下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-2-016', 'cat-1-004', '新鲜水果', 'https://picsum.photos/48/48?random=46', '新鲜水果类目', 2, 1, 1, NOW(), NOW(), 0),
('cat-2-017', 'cat-1-004', '蔬菜', 'https://picsum.photos/48/48?random=47', '新鲜蔬菜类目', 2, 2, 1, NOW(), NOW(), 0),
('cat-2-018', 'cat-1-004', '肉禽蛋品', 'https://picsum.photos/48/48?random=48', '肉禽蛋品类目', 2, 3, 1, NOW(), NOW(), 0),
('cat-2-019', 'cat-1-004', '海鲜水产', 'https://picsum.photos/48/48?random=49', '海鲜水产类目', 2, 4, 1, NOW(), NOW(), 0),
('cat-2-020', 'cat-1-004', '零食饮料', 'https://picsum.photos/48/48?random=50', '零食饮料类目', 2, 5, 1, NOW(), NOW(), 0);

-- 二级分类（家居生活下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-2-021', 'cat-1-005', '床上用品', 'https://picsum.photos/48/48?random=51', '床上用品类目', 2, 1, 1, NOW(), NOW(), 0),
('cat-2-022', 'cat-1-005', '家居日用', 'https://picsum.photos/48/48?random=52', '家居日用类目', 2, 2, 1, NOW(), NOW(), 0),
('cat-2-023', 'cat-1-005', '厨具餐具', 'https://picsum.photos/48/48?random=53', '厨具餐具类目', 2, 3, 1, NOW(), NOW(), 0),
('cat-2-024', 'cat-1-005', '家具', 'https://picsum.photos/48/48?random=54', '家具类目', 2, 4, 1, NOW(), NOW(), 0),
('cat-2-025', 'cat-1-005', '家装建材', 'https://picsum.photos/48/48?random=55', '家装建材类目', 2, 5, 1, NOW(), NOW(), 0);

-- 三级分类（女装下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-3-001', 'cat-2-001', '上衣', NULL, '女装上衣', 3, 1, 1, NOW(), NOW(), 0),
('cat-3-002', 'cat-2-001', '裤子', NULL, '女装裤子', 3, 2, 1, NOW(), NOW(), 0),
('cat-3-003', 'cat-2-001', '裙子', NULL, '女装裙子', 3, 3, 1, NOW(), NOW(), 0),
('cat-3-004', 'cat-2-001', '外套', NULL, '女装外套', 3, 4, 1, NOW(), NOW(), 0),
('cat-3-005', 'cat-2-001', '套装', NULL, '女装套装', 3, 5, 1, NOW(), NOW(), 0);

-- 三级分类（男装下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-3-006', 'cat-2-002', 'T恤', NULL, '男装T恤', 3, 1, 1, NOW(), NOW(), 0),
('cat-3-007', 'cat-2-002', '衬衫', NULL, '男装衬衫', 3, 2, 1, NOW(), NOW(), 0),
('cat-3-008', 'cat-2-002', '裤子', NULL, '男装裤子', 3, 3, 1, NOW(), NOW(), 0),
('cat-3-009', 'cat-2-002', '外套', NULL, '男装外套', 3, 4, 1, NOW(), NOW(), 0),
('cat-3-010', 'cat-2-002', '西装', NULL, '男装西装', 3, 5, 1, NOW(), NOW(), 0);

-- 三级分类（面部护理下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-3-011', 'cat-2-006', '洁面', NULL, '洁面产品', 3, 1, 1, NOW(), NOW(), 0),
('cat-3-012', 'cat-2-006', '爽肤水', NULL, '爽肤水', 3, 2, 1, NOW(), NOW(), 0),
('cat-3-013', 'cat-2-006', '精华液', NULL, '精华液', 3, 3, 1, NOW(), NOW(), 0),
('cat-3-014', 'cat-2-006', '面霜', NULL, '面霜', 3, 4, 1, NOW(), NOW(), 0),
('cat-3-015', 'cat-2-006', '面膜', NULL, '面膜', 3, 5, 1, NOW(), NOW(), 0);

-- 三级分类（手机通讯下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-3-016', 'cat-2-011', '智能手机', NULL, '智能手机', 3, 1, 1, NOW(), NOW(), 0),
('cat-3-017', 'cat-2-011', '手机配件', NULL, '手机配件', 3, 2, 1, NOW(), NOW(), 0),
('cat-3-018', 'cat-2-011', '手机壳', NULL, '手机保护壳', 3, 3, 1, NOW(), NOW(), 0),
('cat-3-019', 'cat-2-011', '充电器', NULL, '手机充电器', 3, 4, 1, NOW(), NOW(), 0),
('cat-3-020', 'cat-2-011', '耳机', NULL, '手机耳机', 3, 5, 1, NOW(), NOW(), 0);

-- 三级分类（新鲜水果下5个）
INSERT INTO `product_category` (`id`, `parent_id`, `name`, `icon_url`, `description`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
('cat-3-021', 'cat-2-016', '苹果', NULL, '新鲜苹果', 3, 1, 1, NOW(), NOW(), 0),
('cat-3-022', 'cat-2-016', '橙子', NULL, '新鲜橙子', 3, 2, 1, NOW(), NOW(), 0),
('cat-3-023', 'cat-2-016', '香蕉', NULL, '新鲜香蕉', 3, 3, 1, NOW(), NOW(), 0),
('cat-3-024', 'cat-2-016', '进口水果', NULL, '进口水果', 3, 4, 1, NOW(), NOW(), 0),
('cat-3-025', 'cat-2-016', '时令水果', NULL, '时令水果', 3, 5, 1, NOW(), NOW(), 0);

-- =============================================
-- 4. 商品测试数据（30条，覆盖多个分类）
-- =============================================

-- 女装商品（6条）
INSERT INTO `product` (`id`, `seller_id`, `category_id`, `name`, `brand`, `description`, `price`, `original_price`, `stock`, `sold_count`, `rating`, `rating_count`, `main_image`, `images`, `video_url`, `status`, `shelf_time`, `create_time`, `update_time`, `deleted`)
VALUES
('prod-001', 'seller-001', 'cat-3-001', '纯棉白色T恤女短袖宽松百搭基础款', '优衣库', '<p>100%纯棉材质，柔软舒适。经典百搭款式，四季必备。宽松版型，适合各种身材。</p>', 5900, 9900, 300, 1250, 4.9, 892, 'https://picsum.photos/800/800?random=201', '["https://picsum.photos/800/800?random=201","https://picsum.photos/800/800?random=202"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-002', 'seller-001', 'cat-3-002', '高腰直筒牛仔裤女显瘦显高阔腿裤', 'Levi''s', '<p>经典牛仔面料，高腰设计显腿长。直筒版型，显瘦又时尚。百搭实用，四季皆宜。</p>', 18900, 35900, 85, 467, 4.7, 328, 'https://picsum.photos/800/800?random=203', '["https://picsum.photos/800/800?random=203","https://picsum.photos/800/800?random=204"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-003', 'seller-001', 'cat-3-003', '法式复古连衣裙女夏季气质显瘦收腰A字裙', '时尚丽人', '<p>法式复古风格，优雅气质。采用优质面料，舒适透气。收腰设计，完美展现身材曲线。</p>', 15900, 29900, 120, 238, 4.8, 156, 'https://picsum.photos/800/800?random=205', '["https://picsum.photos/800/800?random=205","https://picsum.photos/800/800?random=206"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-004', 'seller-001', 'cat-3-004', '羊毛呢大衣女中长款秋冬新款气质外套', 'MaxMara', '<p>精选优质羊毛面料，保暖又优雅。经典大衣设计，永不过时。精致剪裁，彰显品味。</p>', 58900, 89900, 45, 89, 4.9, 67, 'https://picsum.photos/800/800?random=207', '["https://picsum.photos/800/800?random=207","https://picsum.photos/800/800?random=208"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-005', 'seller-001', 'cat-3-005', '职业装套装女西装外套+裙子两件套', '雅莹', '<p>专业职场套装，展现干练气质。精致剪裁，完美贴合身形。优质面料，舒适不易皱。</p>', 39900, 69900, 68, 145, 4.8, 98, 'https://picsum.photos/800/800?random=209', '["https://picsum.photos/800/800?random=209","https://picsum.photos/800/800?random=210"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-006', 'seller-001', 'cat-3-003', '碎花雪纺连衣裙女中长款夏季仙女裙', '韩都衣舍', '<p>甜美碎花图案，仙气十足。雪纺面料，轻盈飘逸。中长款设计，遮肉显瘦。</p>', 12900, 24900, 95, 342, 4.7, 212, 'https://picsum.photos/800/800?random=211', '["https://picsum.photos/800/800?random=211","https://picsum.photos/800/800?random=212"]', NULL, 1, NOW(), NOW(), NOW(), 0);

-- 男装商品（5条）
INSERT INTO `product` (`id`, `seller_id`, `category_id`, `name`, `brand`, `description`, `price`, `original_price`, `stock`, `sold_count`, `rating`, `rating_count`, `main_image`, `images`, `video_url`, `status`, `shelf_time`, `create_time`, `update_time`, `deleted`)
VALUES
('prod-007', 'seller-001', 'cat-3-006', '纯棉短袖T恤男夏季圆领纯色打底衫', '海澜之家', '<p>100%纯棉，舒适透气。经典圆领设计，简约百搭。多色可选，基础款必备。</p>', 4900, 7900, 450, 2340, 4.8, 1567, 'https://picsum.photos/800/800?random=213', '["https://picsum.photos/800/800?random=213"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-008', 'seller-001', 'cat-3-007', '商务休闲长袖衬衫男修身韩版免烫', 'Jack&Jones', '<p>商务休闲两相宜，修身版型显身材。免烫面料，方便打理。精致细节，品质之选。</p>', 14900, 29900, 180, 678, 4.7, 445, 'https://picsum.photos/800/800?random=214', '["https://picsum.photos/800/800?random=214"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-009', 'seller-001', 'cat-3-008', '休闲牛仔裤男直筒宽松潮流百搭长裤', 'Lee', '<p>经典五袋款式，百搭实用。直筒版型，舒适自在。优质牛仔布料，耐穿耐洗。</p>', 16900, 29900, 220, 987, 4.8, 756, 'https://picsum.photos/800/800?random=215', '["https://picsum.photos/800/800?random=215"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-010', 'seller-001', 'cat-3-009', '冬季加厚羽绒服男短款青年保暖外套', 'The North Face', '<p>90%白鸭绒填充，保暖性强。防风防水面料，适应多种天气。短款设计，轻便不臃肿。</p>', 45900, 79900, 88, 234, 4.9, 189, 'https://picsum.photos/800/800?random=216', '["https://picsum.photos/800/800?random=216"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-011', 'seller-001', 'cat-3-010', '商务正装西装套装男修身职业装', '雅戈尔', '<p>高档羊毛面料，质感上乘。经典版型，专业商务。精湛工艺，细节考究。</p>', 89900, 149900, 45, 123, 4.9, 98, 'https://picsum.photos/800/800?random=217', '["https://picsum.photos/800/800?random=217"]', NULL, 1, NOW(), NOW(), NOW(), 0);

-- 美妆护肤商品（5条）
INSERT INTO `product` (`id`, `seller_id`, `category_id`, `name`, `brand`, `description`, `price`, `original_price`, `stock`, `sold_count`, `rating`, `rating_count`, `main_image`, `images`, `video_url`, `status`, `shelf_time`, `create_time`, `update_time`, `deleted`)
VALUES
('prod-012', 'seller-003', 'cat-3-011', '氨基酸洗面奶温和清洁保湿控油', 'Fancl', '<p>温和氨基酸配方，深层清洁不紧绷。控油保湿，平衡水油。适合所有肤质。</p>', 8900, 12900, 380, 3456, 4.8, 2345, 'https://picsum.photos/800/800?random=218', '["https://picsum.photos/800/800?random=218"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-013', 'seller-003', 'cat-3-012', '神仙水精华水保湿补水爽肤水', 'SK-II', '<p>经典神仙水，深层补水。改善肌肤质地，提亮肤色。长效保湿，肌肤水润透亮。</p>', 59900, 69900, 120, 2890, 4.9, 1987, 'https://picsum.photos/800/800?random=219', '["https://picsum.photos/800/800?random=219"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-014', 'seller-003', 'cat-3-013', '小棕瓶精华液修护抗老紧致', '雅诗兰黛', '<p>经典小棕瓶，修护肌肤。抗氧化配方，延缓衰老。质地清爽，易吸收。</p>', 78900, 89000, 150, 4560, 4.9, 3210, 'https://picsum.photos/800/800?random=220', '["https://picsum.photos/800/800?random=220"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-015', 'seller-003', 'cat-3-014', '保湿滋润面霜抗皱紧致提拉', 'La Mer', '<p>海蓝之谜经典面霜，深层滋养。珍贵成分，奢华体验。改善细纹，紧致提拉。</p>', 159900, 189900, 55, 678, 5.0, 456, 'https://picsum.photos/800/800?random=221', '["https://picsum.photos/800/800?random=221"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-016', 'seller-003', 'cat-3-015', '补水保湿面膜提亮肤色舒缓', '兰芝', '<p>夜间修护面膜，深层补水。提亮肤色，舒缓肌肤。睡眠面膜，免洗方便。</p>', 15900, 19900, 420, 5678, 4.8, 3890, 'https://picsum.photos/800/800?random=222', '["https://picsum.photos/800/800?random=222"]', NULL, 1, NOW(), NOW(), NOW(), 0);

-- 数码产品（7条）
INSERT INTO `product` (`id`, `seller_id`, `category_id`, `name`, `brand`, `description`, `price`, `original_price`, `stock`, `sold_count`, `rating`, `rating_count`, `main_image`, `images`, `video_url`, `status`, `shelf_time`, `create_time`, `update_time`, `deleted`)
VALUES
('prod-017', 'seller-002', 'cat-3-016', 'iPhone 15 Pro Max 256GB 原色钛金属', 'Apple', '<p>A17 Pro芯片，性能强劲。钛金属外壳，轻盈坚固。Pro级摄像系统，拍摄更专业。</p>', 999900, 999900, 50, 2890, 4.9, 1567, 'https://picsum.photos/800/800?random=223', '["https://picsum.photos/800/800?random=223"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-018', 'seller-002', 'cat-3-016', '华为Mate 60 Pro 12GB+512GB 雅川青', '华为', '<p>卫星通信技术，随时在线。第二代昆仑玻璃，坚固耐用。超光变XMAGE影像，记录精彩。</p>', 699900, 699900, 80, 1245, 4.8, 876, 'https://picsum.photos/800/800?random=224', '["https://picsum.photos/800/800?random=224"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-019', 'seller-002', 'cat-3-017', '无线蓝牙耳机降噪运动跑步入耳式', 'Sony', '<p>主动降噪技术，沉浸音质。IPX5防水，运动无忧。超长续航，30小时播放。</p>', 39900, 59900, 200, 3456, 4.7, 2134, 'https://picsum.photos/800/800?random=225', '["https://picsum.photos/800/800?random=225"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-020', 'seller-002', 'cat-3-018', 'iPhone手机壳透明硅胶防摔保护套', 'UAG', '<p>军工级防摔认证，全方位保护。透明设计，展现原机美感。精准开孔，使用便捷。</p>', 8900, 15900, 500, 5678, 4.6, 3892, 'https://picsum.photos/800/800?random=226', '["https://picsum.photos/800/800?random=226"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-021', 'seller-002', 'cat-3-019', '65W氮化镓快充充电器多口USB-C', 'Anker', '<p>65W大功率，快速充电。氮化镓技术，小巧便携。多口设计，同时充电多设备。</p>', 15900, 25900, 180, 2340, 4.8, 1567, 'https://picsum.photos/800/800?random=227', '["https://picsum.photos/800/800?random=227"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-022', 'seller-002', 'cat-3-020', 'AirPods Pro 2代主动降噪无线耳机', 'Apple', '<p>第二代主动降噪，沉浸式音质。自适应音频，智能体验。超长续航，无线充电。</p>', 189900, 189900, 120, 1890, 4.9, 1234, 'https://picsum.photos/800/800?random=228', '["https://picsum.photos/800/800?random=228"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-023', 'seller-002', 'cat-2-012', 'MacBook Pro 14英寸 M3芯片 16GB 512GB', 'Apple', '<p>M3芯片，性能飞跃。Liquid视网膜显示屏，色彩绚丽。超长续航，移动办公无忧。</p>', 1499900, 1499900, 30, 456, 5.0, 234, 'https://picsum.photos/800/800?random=229', '["https://picsum.photos/800/800?random=229"]', NULL, 1, NOW(), NOW(), NOW(), 0);

-- 食品生鲜商品（4条）
INSERT INTO `product` (`id`, `seller_id`, `category_id`, `name`, `brand`, `description`, `price`, `original_price`, `stock`, `sold_count`, `rating`, `rating_count`, `main_image`, `images`, `video_url`, `status`, `shelf_time`, `create_time`, `update_time`, `deleted`)
VALUES
('prod-024', 'seller-005', 'cat-3-021', '新鲜红富士苹果5斤装脆甜多汁', '烟台苹果', '<p>烟台红富士，果大形正。脆甜多汁，营养丰富。新鲜采摘，品质保证。</p>', 3900, 5900, 500, 4567, 4.8, 2890, 'https://picsum.photos/800/800?random=230', '["https://picsum.photos/800/800?random=230"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-025', 'seller-005', 'cat-3-022', '新鲜脐橙赣南橙子5斤装香甜', '赣南脐橙', '<p>赣南脐橙，果肉饱满。酸甜适中，维C丰富。新鲜直达，品质优选。</p>', 4900, 7900, 380, 3456, 4.7, 2134, 'https://picsum.photos/800/800?random=231', '["https://picsum.photos/800/800?random=231"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-026', 'seller-005', 'cat-3-023', '海南香蕉新鲜水果5斤装香甜软糯', '海南香蕉', '<p>海南香蕉，自然成熟。香甜软糯，营养丰富。新鲜直供，品质保证。</p>', 2900, 4900, 600, 6789, 4.6, 4123, 'https://picsum.photos/800/800?random=232', '["https://picsum.photos/800/800?random=232"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-027', 'seller-005', 'cat-3-024', '新西兰进口奇异果绿心猕猴桃礼盒2kg', '佳沛', '<p>新西兰进口，绿心猕猴桃。酸甜适中，维C之王。精美礼盒包装，送礼佳品。</p>', 15900, 19900, 100, 890, 4.8, 567, 'https://picsum.photos/800/800?random=233', '["https://picsum.photos/800/800?random=233"]', NULL, 1, NOW(), NOW(), NOW(), 0);

-- 家居生活商品（3条)
INSERT INTO `product` (`id`, `seller_id`, `category_id`, `name`, `brand`, `description`, `price`, `original_price`, `stock`, `sold_count`, `rating`, `rating_count`, `main_image`, `images`, `video_url`, `status`, `shelf_time`, `create_time`, `update_time`, `deleted`)
VALUES
('prod-028', 'seller-004', 'cat-2-021', '北欧风格纯棉四件套床上用品全棉', '水星家纺', '<p>100%纯棉面料，亲肤舒适。北欧简约设计，时尚百搭。精致做工，经久耐用。</p>', 29900, 59900, 280, 2340, 4.7, 1456, 'https://picsum.photos/800/800?random=234', '["https://picsum.photos/800/800?random=234"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-029', 'seller-004', 'cat-2-022', '智能扫地机器人全自动家用吸尘器', '石头科技', '<p>LDS激光导航，智能规划路径。2500Pa大吸力，深度清洁。APP远程控制，随时随地。</p>', 189900, 249900, 75, 678, 4.9, 456, 'https://picsum.photos/800/800?random=235', '["https://picsum.photos/800/800?random=235"]', NULL, 1, NOW(), NOW(), NOW(), 0),
('prod-030', 'seller-004', 'cat-2-023', '不锈钢锅具套装炒锅汤锅蒸锅', '苏泊尔', '<p>304不锈钢材质，健康安全。复合底设计，导热均匀。多件套装，满足多种烹饪需求。</p>', 39900, 69900, 150, 1234, 4.8, 890, 'https://picsum.photos/800/800?random=236', '["https://picsum.photos/800/800?random=236"]', NULL, 1, NOW(), NOW(), NOW(), 0);

-- =============================================
-- 5. 商品SKU测试数据（30条，每个商品1个SKU）
-- =============================================
INSERT INTO `product_sku` (`id`, `product_id`, `spec_name`, `spec_value_json`, `price`, `stock`, `barcode`, `create_time`, `update_time`, `deleted`)
VALUES
('sku-001', 'prod-001', '白色-M', '{"color":"白色","size":"M"}', 5900, 300, '6901234567001', NOW(), NOW(), 0),
('sku-002', 'prod-002', '蓝色-M', '{"color":"蓝色","size":"M"}', 18900, 85, '6901234567002', NOW(), NOW(), 0),
('sku-003', 'prod-003', '米白色-M', '{"color":"米白色","size":"M"}', 15900, 120, '6901234567003', NOW(), NOW(), 0),
('sku-004', 'prod-004', '黑色-M', '{"color":"黑色","size":"M"}', 58900, 45, '6901234567004', NOW(), NOW(), 0),
('sku-005', 'prod-005', '藏青色-M', '{"color":"藏青色","size":"M"}', 39900, 68, '6901234567005', NOW(), NOW(), 0),
('sku-006', 'prod-006', '碎花-M', '{"color":"碎花","size":"M"}', 12900, 95, '6901234567006', NOW(), NOW(), 0),
('sku-007', 'prod-007', '白色-L', '{"color":"白色","size":"L"}', 4900, 450, '6901234567007', NOW(), NOW(), 0),
('sku-008', 'prod-008', '蓝色-L', '{"color":"蓝色","size":"L"}', 14900, 180, '6901234567008', NOW(), NOW(), 0),
('sku-009', 'prod-009', '深蓝-32', '{"color":"深蓝","size":"32"}', 16900, 220, '6901234567009', NOW(), NOW(), 0),
('sku-010', 'prod-010', '黑色-L', '{"color":"黑色","size":"L"}', 45900, 88, '6901234567010', NOW(), NOW(), 0),
('sku-011', 'prod-011', '黑色-L', '{"color":"黑色","size":"L"}', 89900, 45, '6901234567011', NOW(), NOW(), 0),
('sku-012', 'prod-012', '120ml', '{"capacity":"120ml"}', 8900, 380, '6901234567012', NOW(), NOW(), 0),
('sku-013', 'prod-013', '230ml', '{"capacity":"230ml"}', 59900, 120, '6901234567013', NOW(), NOW(), 0),
('sku-014', 'prod-014', '50ml', '{"capacity":"50ml"}', 78900, 150, '6901234567014', NOW(), NOW(), 0),
('sku-015', 'prod-015', '60ml', '{"capacity":"60ml"}', 159900, 55, '6901234567015', NOW(), NOW(), 0),
('sku-016', 'prod-016', '70ml', '{"capacity":"70ml"}', 15900, 420, '6901234567016', NOW(), NOW(), 0),
('sku-017', 'prod-017', '原色钛金属-256GB', '{"color":"原色钛金属","storage":"256GB"}', 999900, 50, '6901234567017', NOW(), NOW(), 0),
('sku-018', 'prod-018', '雅川青-512GB', '{"color":"雅川青","storage":"512GB"}', 699900, 80, '6901234567018', NOW(), NOW(), 0),
('sku-019', 'prod-019', '黑色', '{"color":"黑色"}', 39900, 200, '6901234567019', NOW(), NOW(), 0),
('sku-020', 'prod-020', '透明', '{"color":"透明"}', 8900, 500, '6901234567020', NOW(), NOW(), 0),
('sku-021', 'prod-021', '白色', '{"color":"白色"}', 15900, 180, '6901234567021', NOW(), NOW(), 0),
('sku-022', 'prod-022', '白色', '{"color":"白色"}', 189900, 120, '6901234567022', NOW(), NOW(), 0),
('sku-023', 'prod-023', '深空灰-512GB', '{"color":"深空灰","storage":"512GB"}', 1499900, 30, '6901234567023', NOW(), NOW(), 0),
('sku-024', 'prod-024', '5斤装', '{"weight":"5斤"}', 3900, 500, '6901234567024', NOW(), NOW(), 0),
('sku-025', 'prod-025', '5斤装', '{"weight":"5斤"}', 4900, 380, '6901234567025', NOW(), NOW(), 0),
('sku-026', 'prod-026', '5斤装', '{"weight":"5斤"}', 2900, 600, '6901234567026', NOW(), NOW(), 0),
('sku-027', 'prod-027', '2kg装', '{"weight":"2kg"}', 15900, 100, '6901234567027', NOW(), NOW(), 0),
('sku-028', 'prod-028', '1.8m床-蓝色', '{"size":"1.8m床","color":"蓝色"}', 29900, 280, '6901234567028', NOW(), NOW(), 0),
('sku-029', 'prod-029', '白色', '{"color":"白色"}', 189900, 75, '6901234567029', NOW(), NOW(), 0),
('sku-030', 'prod-030', '六件套', '{"set":"六件套"}', 39900, 150, '6901234567030', NOW(), NOW(), 0);

-- =============================================
-- 测试数据导入完成
-- =============================================

-- 数据统计查询
SELECT '用户数量' as item, COUNT(*) as count FROM user WHERE deleted = 0
UNION ALL
SELECT '商家数量', COUNT(*) FROM seller WHERE deleted = 0
UNION ALL
SELECT '一级分类数量', COUNT(*) FROM product_category WHERE level = 1 AND deleted = 0
UNION ALL
SELECT '二级分类数量', COUNT(*) FROM product_category WHERE level = 2 AND deleted = 0
UNION ALL
SELECT '三级分类数量', COUNT(*) FROM product_category WHERE level = 3 AND deleted = 0
UNION ALL
SELECT '商品数量', COUNT(*) FROM product WHERE deleted = 0
UNION ALL
SELECT 'SKU数量', COUNT(*) FROM product_sku WHERE deleted = 0;
