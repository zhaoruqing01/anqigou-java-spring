-- 动态物流数据生成系统 - 数据库更新SQL

-- 说明:
-- 本SQL用于更新物流相关数据库表,确保支持动态物流数据生成功能
-- 执行前请备份数据库!

-- 1. 检查logistics表是否存在必要字段
-- 如果不存在,则添加(实际上表结构已经包含了这些字段,这里仅作检查)

-- 检查sender_province字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics' 
  AND COLUMN_NAME = 'sender_province';

-- 检查sender_city字段  
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics' 
  AND COLUMN_NAME = 'sender_city';

-- 检查sender_address字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics' 
  AND COLUMN_NAME = 'sender_address';

-- 检查receiver_province字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics' 
  AND COLUMN_NAME = 'receiver_province';

-- 检查receiver_city字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics' 
  AND COLUMN_NAME = 'receiver_city';

-- 检查receiver_address字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics' 
  AND COLUMN_NAME = 'receiver_address';

-- 2. 检查logistics_track表是否存在必要字段

-- 检查courier_name字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics_track' 
  AND COLUMN_NAME = 'courier_name';

-- 检查courier_phone字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics_track' 
  AND COLUMN_NAME = 'courier_phone';

-- 检查sort_order字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'logistics_track' 
  AND COLUMN_NAME = 'sort_order';

-- 3. 清理测试数据(可选 - 仅开发环境使用)
-- 注意: 正式环境请勿执行此部分!

-- DELETE FROM logistics_track WHERE deleted = 0;
-- DELETE FROM logistics WHERE deleted = 0;

-- 4. 添加索引优化查询性能

-- 为logistics表的order_id添加索引(如果不存在)
CREATE INDEX IF NOT EXISTS idx_logistics_order_id ON logistics(order_id);

-- 为logistics_track表的logistics_id添加索引(如果不存在)
CREATE INDEX IF NOT EXISTS idx_logistics_track_logistics_id ON logistics_track(logistics_id);

-- 为logistics_track表的sort_order添加索引(如果不存在)
CREATE INDEX IF NOT EXISTS idx_logistics_track_sort_order ON logistics_track(logistics_id, sort_order DESC);

-- 5. 验证表结构

-- 查看logistics表结构
DESC logistics;

-- 查看logistics_track表结构
DESC logistics_track;

-- 6. 查询统计信息

-- 统计物流记录数
SELECT COUNT(*) AS logistics_count FROM logistics WHERE deleted = 0;

-- 统计物流轨迹记录数
SELECT COUNT(*) AS track_count FROM logistics_track WHERE deleted = 0;

-- 按快递公司统计
SELECT courier_company, COUNT(*) AS count 
FROM logistics 
WHERE deleted = 0 
GROUP BY courier_company;

-- 按状态统计
SELECT status, COUNT(*) AS count 
FROM logistics 
WHERE deleted = 0 
GROUP BY status;

-- 完成!
-- 数据库更新完成,系统已准备好支持动态物流数据生成功能。
