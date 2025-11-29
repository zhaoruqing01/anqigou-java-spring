# API 接口 404/403 错误修复总结

## 修复日期

2025-11-29

## 问题列表及修复方案

### 1. http://localhost:8083/api/order/list?pageNum=1&pageSize=10 报 404 ❌

**原因:**

- 网关路由配置为 `/api/orders/**`,但 OrderController 实际路径为 `/api/order/**`
- 商品服务和订单服务端口冲突,都使用 8083

**修复:**

- ✅ 修改网关路由: `/api/orders/**` → `/api/order/**`
- ✅ 修改商品服务端口: 8083 → 8082
- ✅ 重新编译网关服务和商品服务

**修复后正确访问方式:**

- 通过网关: `http://localhost:8080/api/order/list?pageNum=1&pageSize=10`
- 直接访问: `http://localhost:8083/api/order/list?pageNum=1&pageSize=10`

---

### 2. http://localhost:8082/api/product/categories/first-level 报 404 ❌

**原因:**

- 网关路由配置为 `/api/products/**`,但 ProductController 实际路径为 `/api/product/**`
- 商品服务原端口为 8083,与订单服务冲突

**修复:**

- ✅ 修改网关路由: `/api/products/**` → `/api/product/**`
- ✅ 修改商品服务端口: 8083 → 8082
- ✅ 重新编译商品服务

**修复后正确访问方式:**

- 通过网关: `http://localhost:8080/api/product/categories/first-level`
- 直接访问: `http://localhost:8082/api/product/categories/first-level`

---

### 3. http://localhost:8081/api/api/cart/list 报 403 ❌

**原因:**

- URL 路径错误,多了一个 `/api`
- CartController 路径为 `/api/cart/**`,在订单服务(8083)中

**修复:**

- 无需代码修复,只需更正访问 URL

**正确访问方式:**

- 通过网关: `http://localhost:8080/api/cart/list`
- 直接访问: `http://localhost:8083/api/cart/list`

---

### 4. http://localhost:8083/api/order/list?pageNum=1&pageSize=10&status= 报 404 ❌

**原因:**

- 同问题 1,网关路由配置错误

**修复:**

- ✅ 修改网关路由: `/api/orders/**` → `/api/order/**`

**修复后正确访问方式:**

- 通过网关: `http://localhost:8080/api/order/list?pageNum=1&pageSize=10&status=`
- 直接访问: `http://localhost:8083/api/order/list?pageNum=1&pageSize=10&status=`

---

### 5. RabbitMQ 连接错误 ⚠️

**原因:**

- 订单服务尝试连接 RabbitMQ (localhost:5672),但 RabbitMQ 未启动

**修复:**

- ✅ 在订单服务配置中排除 RabbitMQ 自动配置

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
```

---

## 修复的文件清单

1. `anqigou-java-spring/anqigou-product-service/src/main/resources/application.yml`

   - 修改服务端口: 8083 → 8082

2. `anqigou-java-spring/anqigou-gateway/src/main/resources/application.yml`

   - 修改商品服务路由: `/api/products/**` → `/api/product/**`
   - 修改订单服务路由: `/api/orders/**` → `/api/order/**`

3. `anqigou-java-spring/anqigou-order-service/src/main/resources/application.yml`
   - 已包含 RabbitMQ 自动配置排除(无需修改)

---

## 服务端口分配(修复后)

| 服务名称                     | 端口 | 状态      |
| ---------------------------- | ---- | --------- |
| 网关服务 (Gateway)           | 8080 | ✅        |
| 用户服务 (User Service)      | 8081 | ✅        |
| 商品服务 (Product Service)   | 8082 | ✅ 已修复 |
| 订单服务 (Order Service)     | 8083 | ✅        |
| 支付服务 (Payment Service)   | 8084 | ✅        |
| 物流服务 (Logistics Service) | 8085 | ✅        |

---

## API 路由映射(修复后)

### 订单相关

- 订单列表: `GET /api/order/list`
- 订单详情: `GET /api/order/{orderId}`
- 创建订单: `POST /api/order/create`
- 取消订单: `POST /api/order/{orderId}/cancel`

### 商品相关

- 商品详情: `GET /api/product/{productId}`
- 商品列表: `GET /api/product/list`
- 商品搜索: `GET /api/product/search`
- 一级分类: `GET /api/product/categories/first-level`
- 子分类: `GET /api/product/categories/sub`

### 购物车相关

- 购物车列表: `GET /api/cart/list`
- 添加商品: `POST /api/cart/add`
- 更新数量: `PUT /api/cart/update`
- 移除商品: `DELETE /api/cart/remove`

---

## 后续步骤

1. ✅ 重新编译商品服务: `mvn clean compile -pl anqigou-product-service -am`
2. ✅ 重新编译网关服务: `mvn clean compile -pl anqigou-gateway -am`
3. ⏳ 重启相关服务以应用更改:
   - 停止当前运行的商品服务(如果有)
   - 停止当前运行的网关服务(如果有)
   - 启动商品服务(端口 8082)
   - 启动网关服务(端口 8080)
   - 启动订单服务(端口 8083,如果未运行)
4. ⏳ 验证 API 接口是否正常工作

---

## 验证命令

修复完成后,可以使用以下命令验证:

```bash
# 测试订单列表
curl http://localhost:8080/api/order/list?pageNum=1&pageSize=10

# 测试商品分类
curl http://localhost:8080/api/product/categories/first-level

# 测试购物车列表(需要认证)
curl http://localhost:8080/api/cart/list

# 带状态参数的订单列表
curl http://localhost:8080/api/order/list?pageNum=1&pageSize=10&status=
```

---

## 注意事项

1. 所有外部访问应通过网关(8080 端口)进行
2. 确保 Nacos 注册中心已启动(8848 端口)
3. 确保 MySQL 数据库已启动(3306 端口)
4. 确保 Redis 服务已启动(6379 端口)
5. RabbitMQ 为可选组件,已在配置中禁用,不影响核心功能
6. 服务重启后需要等待向 Nacos 注册完成(通常需要几秒钟)
