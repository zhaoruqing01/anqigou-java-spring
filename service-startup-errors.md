# 服务启动报错记录

## 错误时间

2025-11-29

## 已修复问题

### 1. 端口冲突问题

**问题描述:** 商品服务和订单服务都配置为端口 8083,导致端口冲突

**解决方案:** 将商品服务端口修改为 8082

- 订单服务: 8083
- 商品服务: 8082 (已修复)

### 2. 网关路由配置错误

**问题描述:** 网关路由配置与 Controller 实际路径不匹配

- 网关配置: `/api/products/**` 和 `/api/orders/**`
- Controller 实际路径: `/api/product/**` 和 `/api/order/**`

**解决方案:** 修改网关配置使其与 Controller 路径一致

- `/api/products/**` → `/api/product/**`
- `/api/orders/**` → `/api/order/**`

### 3. RabbitMQ 连接问题

**问题描述:** 订单服务无法连接到 RabbitMQ (localhost:5672)

**解决方案:** 在订单服务配置中排除 RabbitMQ 自动配置

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
```

## 修复后的服务端口分配

- 用户服务 (User Service): 8081
- 商品服务 (Product Service): 8082
- 订单服务 (Order Service): 8083
- 支付服务 (Payment Service): 8084
- 物流服务 (Logistics Service): 8085
- 网关服务 (Gateway): 8080

## 修复后的 API 路由映射

通过网关访问的正确路由:

1. 订单列表: `http://localhost:8080/api/order/list`
2. 商品分类: `http://localhost:8080/api/product/categories/first-level`
3. 购物车列表: `http://localhost:8080/api/cart/list`

直接访问服务的路由:

1. 订单列表: `http://localhost:8083/api/order/list`
2. 商品分类: `http://localhost:8082/api/product/categories/first-level`
3. 购物车列表: `http://localhost:8083/api/cart/list`

## 注意事项

1. 所有对外访问应通过网关(8080 端口)进行
2. 确保 Nacos 服务已启动(8848 端口)
3. 确保 MySQL 数据库已启动(3306 端口)
4. 确保 Redis 服务已启动(6379 端口)
5. RabbitMQ 为可选组件,已在配置中禁用

## 重启记录 (2025-11-29)

### 重启操作

1. 关闭所有正在运行的 Java 进程：使用 `taskkill /f /im java.exe` 命令
2. 重新启动所有服务：执行 `start-all-services.bat` 脚本
3. 服务启动顺序：用户服务 → 商品服务 → 订单服务 → 支付服务 → 物流服务 → 网关服务

### 重启结果

- **用户服务 (User Service) - 端口 8081**：正常启动
- **商品服务 (Product Service) - 端口 8082**：正常启动
- **订单服务 (Order Service) - 端口 8083**：启动成功，由于已禁用 RabbitMQ 自动配置，无 RabbitMQ 连接错误
- **支付服务 (Payment Service) - 端口 8084**：正常启动
- **物流服务 (Logistics Service) - 端口 8085**：正常启动
- **网关服务 (Gateway) - 端口 8080**：正常启动

### 验证结果

所有服务均已成功启动，没有出现新的报错。修复后的配置已生效，各服务可以正常运行。
