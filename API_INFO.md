# 安琦购电商平台 - API 信息文档

## 1. 用户认证模块 API

### 1.1 发送验证码

- **API 路径**: `/api/auth/send-code`
- **请求方法**: POST
- **功能描述**: 向指定手机号发送验证码
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | phone | String | 是 | 用户手机号 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "验证码已发送",
    "data": null
  }
  ```

### 1.2 用户注册

- **API 路径**: `/api/auth/register`
- **请求方法**: POST
- **功能描述**: 用户注册
- **请求体**:
  ```json
  {
    "phone": "13800138000",
    "password": "123456",
    "verifyCode": "123456",
    "nickname": "测试用户"
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userId": "user123",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg"
    }
  }
  ```

### 1.3 密码登录

- **API 路径**: `/api/auth/login`
- **请求方法**: POST
- **功能描述**: 用户通过手机号和密码登录
- **请求体**:
  ```json
  {
    "phone": "13800138000",
    "password": "123456"
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userId": "user123",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg"
    }
  }
  ```

### 1.4 验证码登录

- **API 路径**: `/api/auth/login-with-code`
- **请求方法**: POST
- **功能描述**: 用户通过手机号和验证码登录
- **请求体**:
  ```json
  {
    "phone": "13800138000",
    "verifyCode": "123456"
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userId": "user123",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg"
    }
  }
  ```

### 1.5 微信登录

- **API 路径**: `/api/auth/wechat-login`
- **请求方法**: POST
- **功能描述**: 用户通过微信登录
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | code | String | 是 | 微信登录临时凭证 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userId": "user123",
      "nickname": "微信用户",
      "avatar": "https://example.com/wechat-avatar.jpg"
    }
  }
  ```

### 1.6 获取用户信息

- **API 路径**: `/api/auth/user-info`
- **请求方法**: GET
- **功能描述**: 获取当前登录用户信息
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "userId": "user123",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg",
      "phone": "13800138000"
    }
  }
  ```

### 1.7 更新用户信息

- **API 路径**: `/api/auth/user-info`
- **请求方法**: PUT
- **功能描述**: 更新当前登录用户信息
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
- **请求体**:
  ```json
  {
    "nickname": "新昵称",
    "avatar": "https://example.com/new-avatar.jpg"
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": null
  }
  ```

## 2. 地址管理模块 API

### 2.1 获取地址列表

- **API 路径**: `/api/user/address/list`
- **请求方法**: GET
- **功能描述**: 获取当前用户的地址列表
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 否 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "addressId": "addr123",
        "consignee": "张三",
        "phone": "13800138000",
        "province": "北京市",
        "city": "北京市",
        "district": "朝阳区",
        "detailAddress": "测试街道123号",
        "isDefault": true,
        "zipCode": "100000"
      }
    ]
  }
  ```

### 2.2 获取地址详情

- **API 路径**: `/api/user/address/{addressId}`
- **请求方法**: GET
- **功能描述**: 获取地址详情
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | addressId | String | 是 | 地址 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "addressId": "addr123",
      "consignee": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detailAddress": "测试街道123号",
      "isDefault": true,
      "zipCode": "100000"
    }
  }
  ```

### 2.3 创建地址

- **API 路径**: `/api/user/address`
- **请求方法**: POST
- **功能描述**: 创建新地址
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **请求体**:
  ```json
  {
    "consignee": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detailAddress": "测试街道123号",
    "isDefault": true,
    "zipCode": "100000"
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "地址创建成功",
    "data": {
      "addressId": "addr123",
      "consignee": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detailAddress": "测试街道123号",
      "isDefault": true,
      "zipCode": "100000"
    }
  }
  ```

### 2.4 更新地址

- **API 路径**: `/api/user/address/{addressId}`
- **请求方法**: PUT
- **功能描述**: 更新地址信息
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | addressId | String | 是 | 地址 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **请求体**:
  ```json
  {
    "consignee": "李四",
    "phone": "13900139000",
    "province": "上海市",
    "city": "上海市",
    "district": "浦东新区",
    "detailAddress": "测试街道456号",
    "isDefault": false,
    "zipCode": "200000"
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "地址更新成功",
    "data": {
      "addressId": "addr123",
      "consignee": "李四",
      "phone": "13900139000",
      "province": "上海市",
      "city": "上海市",
      "district": "浦东新区",
      "detailAddress": "测试街道456号",
      "isDefault": false,
      "zipCode": "200000"
    }
  }
  ```

### 2.5 删除地址

- **API 路径**: `/api/user/address/{addressId}`
- **请求方法**: DELETE
- **功能描述**: 删除地址
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | addressId | String | 是 | 地址 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "地址删除成功",
    "data": null
  }
  ```

### 2.6 设置默认地址

- **API 路径**: `/api/user/address/{addressId}/set-default`
- **请求方法**: POST
- **功能描述**: 设置默认地址
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | addressId | String | 是 | 地址 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "默认地址设置成功",
    "data": null
  }
  ```

## 3. 意见反馈模块 API

### 3.1 提交反馈

- **API 路径**: `/api/feedback`
- **请求方法**: POST
- **功能描述**: 提交意见反馈
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求体**:
  ```json
  {
    "type": "功能异常",
    "content": "测试反馈内容",
    "images": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ]
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "反馈提交成功",
    "data": null
  }
  ```

### 3.2 获取反馈列表

- **API 路径**: `/api/feedback/list`
- **请求方法**: GET
- **功能描述**: 获取当前用户的反馈列表
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，默认 1 |
  | size | Integer | 否 | 每页条数，默认 10 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "feedbackId": "fb123",
          "type": "功能异常",
          "content": "测试反馈内容",
          "images": ["https://example.com/image1.jpg"],
          "status": "待处理",
          "createTime": "2025-11-29T00:00:00Z"
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1
    }
  }
  ```

### 3.3 获取反馈详情

- **API 路径**: `/api/feedback/detail`
- **请求方法**: GET
- **功能描述**: 获取反馈详情
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | feedbackId | String | 是 | 反馈 ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "feedbackId": "fb123",
      "type": "功能异常",
      "content": "测试反馈内容",
      "images": ["https://example.com/image1.jpg"],
      "status": "待处理",
      "createTime": "2025-11-29T00:00:00Z",
      "replyContent": null,
      "replyTime": null
    }
  }
  ```

## 4. 收藏管理模块 API

### 4.1 添加收藏

- **API 路径**: `/api/user/favorite/add/{productId}`
- **请求方法**: POST
- **功能描述**: 添加商品到收藏夹
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | productId | String | 是 | 商品 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

### 4.2 取消收藏

- **API 路径**: `/api/user/favorite/cancel/{productId}`
- **请求方法**: DELETE
- **功能描述**: 取消收藏商品
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | productId | String | 是 | 商品 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

### 4.3 批量取消收藏

- **API 路径**: `/api/user/favorite/batch-cancel`
- **请求方法**: DELETE
- **功能描述**: 批量取消收藏商品
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **请求体**:
  ```json
  ["product1", "product2", "product3"]
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": 3
  }
  ```

### 4.4 获取收藏列表

- **API 路径**: `/api/user/favorite/list`
- **请求方法**: GET
- **功能描述**: 获取收藏列表
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | pageNum | Integer | 否 | 页码，默认 1 |
  | pageSize | Integer | 否 | 每页条数，默认 10 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "productId": "product1",
        "productName": "测试商品1",
        "price": 99.9,
        "originalPrice": 129.9,
        "mainImage": "https://example.com/product1.jpg",
        "createTime": "2025-11-29T00:00:00Z"
      }
    ]
  }
  ```

### 4.5 检查商品是否已收藏

- **API 路径**: `/api/user/favorite/check/{productId}`
- **请求方法**: GET
- **功能描述**: 检查商品是否已收藏
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | productId | String | 是 | 商品 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 否 | Bearer token |
  | X-User-Id | String | 否 | 用户 ID（优先级低于 Authorization） |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

## 5. 购物车模块 API

### 5.1 添加商品到购物车

- **API 路径**: `/api/cart/add`
- **请求方法**: POST
- **功能描述**: 添加商品到购物车
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求体**:
  ```json
  {
    "productId": "product1",
    "skuId": "sku1",
    "quantity": 1
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "添加成功",
    "data": null
  }
  ```

### 5.2 获取购物车列表

- **API 路径**: `/api/cart/list`
- **请求方法**: GET
- **功能描述**: 获取购物车列表
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "skuId": "sku1",
        "productId": "product1",
        "productName": "测试商品1",
        "skuName": "红色+XL",
        "price": 99.9,
        "quantity": 1,
        "mainImage": "https://example.com/product1.jpg",
        "stock": 100
      }
    ]
  }
  ```

### 5.3 更新购物车商品数量

- **API 路径**: `/api/cart/update`
- **请求方法**: PUT
- **功能描述**: 更新购物车商品数量
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求体**:
  ```json
  {
    "skuId": "sku1",
    "quantity": 2
  }
  ```
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": null
  }
  ```

### 5.4 移除购物车商品

- **API 路径**: `/api/cart/remove`
- **请求方法**: DELETE
- **功能描述**: 移除购物车商品
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | skuId | String | 是 | 商品 SKU ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "移除成功",
    "data": null
  }
  ```

### 5.5 清空购物车

- **API 路径**: `/api/cart/clear`
- **请求方法**: DELETE
- **功能描述**: 清空购物车
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "购物车已清空",
    "data": null
  }
  ```

## 6. 订单管理模块 API

### 6.1 创建订单

- **API 路径**: `/api/order/create`
- **请求方法**: POST
- **功能描述**: 创建订单
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求体**:
  ```json
  {
    "addressId": "addr123",
    "items": [
      {
        "skuId": "sku1",
        "quantity": 1
      }
    ],
    "paymentMethod": 1,
    "shippingMethod": "normal",
    "remark": "测试订单备注"
  }
  ```
  **参数说明**:
  - `addressId`: 收货地址 ID（必填）
  - `items`: 订单商品列表（必填）
    - `skuId`: 商品 SKU ID（必填）
    - `quantity`: 购买数量（必填）
  - `paymentMethod`: 支付方式（必填，1-微信支付，2-支付宝）
  - `shippingMethod`: 配送方式（选填，normal-标准配送，express-次日达，pickup-到店自提，默认 normal）
  - `remark`: 订单备注（选填）
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "订单创建成功",
    "data": "order123"
  }
  ```

### 6.2 获取订单详情

- **API 路径**: `/api/order/{orderId}`
- **请求方法**: GET
- **功能描述**: 获取订单详情
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | orderId | String | 是 | 订单 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "orderId": "order123",
      "orderNo": "2025112912345678",
      "status": "待付款",
      "totalAmount": 99.9,
      "actualAmount": 99.9,
      "paymentMethod": "微信支付",
      "createTime": "2025-11-29T00:00:00Z",
      "address": {
        "consignee": "张三",
        "phone": "13800138000",
        "fullAddress": "北京市朝阳区测试街道123号"
      },
      "items": [
        {
          "skuId": "sku1",
          "productId": "product1",
          "productName": "测试商品1",
          "skuName": "红色+XL",
          "price": 99.9,
          "quantity": 1,
          "mainImage": "https://example.com/product1.jpg"
        }
      ]
    }
  }
  ```

### 6.3 获取订单列表

- **API 路径**: `/api/order/list`
- **请求方法**: GET
- **功能描述**: 获取订单列表
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | pageNum | Integer | 否 | 页码，默认 1 |
  | pageSize | Integer | 否 | 每页条数，默认 10 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "orderId": "order123",
          "orderNo": "2025112912345678",
          "status": "待付款",
          "totalAmount": 99.9,
          "createTime": "2025-11-29T00:00:00Z",
          "items": [
            {
              "productName": "测试商品1",
              "skuName": "红色+XL",
              "price": 99.9,
              "quantity": 1,
              "mainImage": "https://example.com/product1.jpg"
            }
          ]
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1
    }
  }
  ```

### 6.4 取消订单

- **API 路径**: `/api/order/{orderId}/cancel`
- **请求方法**: POST
- **功能描述**: 取消订单
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | orderId | String | 是 | 订单 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "订单取消成功",
    "data": null
  }
  ```

### 6.5 确认收货

- **API 路径**: `/api/order/{orderId}/confirm-receipt`
- **请求方法**: POST
- **功能描述**: 确认收货
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | orderId | String | 是 | 订单 ID |
- **请求头**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | Authorization | String | 是 | Bearer token |
  | X-User-Id | String | 是 | 用户 ID |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "订单已签收",
    "data": null
  }
  ```

### 6.6 更新订单支付状态

- **API 路径**: `/api/order/{orderId}/pay/{paymentNo}`
- **请求方法**: GET
- **功能描述**: 更新订单支付状态（供支付服务调用）
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | orderId | String | 是 | 订单 ID |
  | paymentNo | String | 是 | 支付单号 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

### 6.7 订单发货

- **API 路径**: `/api/order/{orderId}/ship`
- **请求方法**: POST
- **功能描述**: 订单发货（供商家使用）
- **路径参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | orderId | String | 是 | 订单 ID |
- **请求参数**:
  | 参数名 | 类型 | 必填 | 描述 |
  |--------|------|------|------|
  | courierCompany | String | 是 | 快递公司 |
  | trackingNo | String | 是 | 快递单号 |
- **响应结果**:
  ```json
  {
    "code": 200,
    "message": "订单已发货",
    "data": null
  }
  ```

## 7. 通用响应格式

所有 API 响应均采用统一格式：

```json
{
  "code": 200, // 响应码，200表示成功，其他表示失败
  "message": "success", // 响应消息
  "data": null // 响应数据，根据具体API返回不同类型
}
```

### 响应码说明

| 响应码 | 描述               |
| ------ | ------------------ |
| 200    | 请求成功           |
| 400    | 请求参数错误       |
| 401    | 未授权，需要登录   |
| 403    | 禁止访问，权限不足 |
| 404    | 请求资源不存在     |
| 500    | 服务器内部错误     |
| 501    | 未实现的功能       |
| 502    | 网关错误           |
| 503    | 服务不可用         |
| 504    | 网关超时           |

## 8. API 调用注意事项

1. **认证方式**：所有需要登录的 API 均需要在请求头中携带`Authorization`（Bearer token）或`X-User-Id`
2. **请求格式**：所有 POST、PUT 请求的请求体均为 JSON 格式，Content-Type 为 application/json
3. **响应处理**：客户端需要根据响应码判断请求是否成功，并处理相应的业务逻辑
4. **错误处理**：当请求失败时，客户端需要展示响应中的 message 字段，告知用户具体错误信息
5. **接口版本**：API 版本通过 URL 路径区分，如`/api/v1/xxx`
6. **请求频率**：为了保护系统安全，API 有请求频率限制，超过限制会返回 429 错误
7. **数据格式**：所有日期时间格式均采用 ISO 8601 标准，如`2025-11-29T00:00:00Z`
8. **分页参数**：所有分页查询 API 均支持 pageNum 和 pageSize 参数，默认值分别为 1 和 10

## 9. 开发环境 API 地址

| 服务名称 | 服务地址                  | 说明                     |
| -------- | ------------------------- | ------------------------ |
| API 网关 | http://localhost:8081     | 所有 API 请求的入口      |
| 用户服务 | http://localhost:8081/api | 处理用户相关请求         |
| 商品服务 | http://localhost:8082/api | 处理商品相关请求         |
| 订单服务 | http://localhost:8083/api | 处理订单、购物车相关请求 |
| 支付服务 | http://localhost:8084/api | 处理支付相关请求         |
| 物流服务 | http://localhost:8085/api | 处理物流相关请求         |

## 10. 测试账号

| 账号类型   | 手机号      | 密码   | 说明                 |
| ---------- | ----------- | ------ | -------------------- |
| 普通用户   | 13800138000 | 123456 | 用于测试用户端功能   |
| 商家账号   | 13900139000 | 123456 | 用于测试商家端功能   |
| 管理员账号 | 13700137000 | 123456 | 用于测试管理员端功能 |
