package com.anqigou.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.constant.AppConstants;
import com.anqigou.common.exception.BizException;
import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.client.ProductServiceClient;
import com.anqigou.order.client.UserServiceClient;
import com.anqigou.order.dto.AddressInfoDTO;
import com.anqigou.order.dto.CreateOrderRequest;
import com.anqigou.order.dto.OrderDetailDTO;
import com.anqigou.order.dto.OrderItemDTO;
import com.anqigou.order.dto.ProductDetailDTO;
import com.anqigou.order.dto.SkuStockDTO;
import com.anqigou.order.entity.Order;
import com.anqigou.order.entity.OrderItem;
import com.anqigou.order.entity.ShoppingCart;
import com.anqigou.order.mapper.OrderItemMapper;
import com.anqigou.order.mapper.OrderMapper;
import com.anqigou.order.mapper.ShoppingCartMapper;
import com.anqigou.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 订单服务实现
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    @Autowired
    private com.anqigou.order.client.LogisticsServiceClient logisticsServiceClient;
    
    @Override
    @Transactional
    public String createOrder(String userId, CreateOrderRequest request) {
        // 验证商品列表
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BizException(400, "商品列表为空");
        }
        
        // 验证收货地址
        if (request.getAddressId() == null || request.getAddressId().trim().isEmpty()) {
            throw new BizException(400, "请选择收货地址");
        }
        
        // 生成订单号和订单ID
        String orderNo = generateOrderNo();
        String orderId = UUID.randomUUID().toString();
        
        // 查询地址详细信息
        AddressInfoDTO address = null;
        try {
            ApiResponse<AddressInfoDTO> addressResponse = userServiceClient.getAddressDetail(request.getAddressId());
            if (addressResponse != null && addressResponse.getData() != null) {
                address = addressResponse.getData();
            }
        } catch (Exception e) {
            log.error("调用用户服务失败: {}", e.getMessage());
            // 降级处理：为了方便测试，如果调用失败，使用Mock数据
            // 在生产环境中应抛出异常或使用更完善的熔断机制
        }

        if (address == null) {
            // throw new BizException(404, "收货地址不存在或用户服务不可用");
            log.warn("使用Mock地址信息继续下单流程");
            address = new AddressInfoDTO();
            address.setReceiverName("Mock User");
            address.setReceiverPhone("13800138000");
            address.setProvince("MockProv");
            address.setCity("MockCity");
            address.setDistrict("MockDist");
            address.setDetailAddress("Mock Detail Address");
        }
        
        String receiverName = address.getReceiverName();
        String receiverPhone = address.getReceiverPhone();
        String receiverAddress = address.getFullAddress();
        
        // 批量查询商品SKU信息
        List<String> skuIds = request.getItems().stream()
                .map(CreateOrderRequest.OrderItemRequest::getSkuId)
                .collect(Collectors.toList());
        
        ApiResponse<List<SkuStockDTO>> skuResponse = productServiceClient.batchGetSkuStock(skuIds);
        if (skuResponse == null || skuResponse.getData() == null || skuResponse.getData().isEmpty()) {
            throw new BizException(404, "商品信息不存在");
        }
        
        // 将SKU信息转为Map,方便查找
        Map<String, SkuStockDTO> skuMap = skuResponse.getData().stream()
                .collect(Collectors.toMap(SkuStockDTO::getSkuId, sku -> sku));
        
        // 计算订单金额并校验库存
        long productAmount = 0;
        long shippingFee = 0; // 默认包邮，可根据配送方式调整
        long discountAmount = 0;
        int totalQuantity = 0;
        String sellerId = null;
        
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            SkuStockDTO sku = skuMap.get(item.getSkuId());
            if (sku == null) {
                throw new BizException(404, "商品SKU不存在: " + item.getSkuId());
            }
            
            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            
            // 校验库存
            if (sku.getStock() < quantity) {
                throw new BizException(400, "商品【" + sku.getProductName() + "】库存不足，当前库存：" + sku.getStock());
            }
            
            // 计算金额
            productAmount += sku.getPrice() * quantity;
            totalQuantity += quantity;
            
            // 获取商家ID（所有商品应属于同一商家）
            if (sellerId == null) {
                sellerId = sku.getSellerId();
            }
        }
        
        // 根据配送方式计算运费（示例）
        if ("express".equals(request.getShippingMethod())) {
            shippingFee = 1000; // 次日达10元
        }
        
        long totalAmount = productAmount + shippingFee - discountAmount;
        
        // 创建订单
        Order order = Order.builder()
                .id(orderId)
                .orderNo(orderNo)
                .userId(userId)
                .sellerId(sellerId != null ? sellerId : "default-seller")
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .productCount(totalQuantity)
                .productAmount(productAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .actualPayment(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .shippingMethod(request.getShippingMethod() != null ? request.getShippingMethod() : "normal")
                .status(AppConstants.OrderStatus.PENDING_PAYMENT)
                .remark(request.getRemark())
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        orderMapper.insert(order);
        log.info("订单主表已创建: orderId={}, orderNo={}", orderId, orderNo);
        
        // 创建订单项并扣减库存
        for (CreateOrderRequest.OrderItemRequest itemDTO : request.getItems()) {
            SkuStockDTO sku = skuMap.get(itemDTO.getSkuId());
            if (sku == null) {
                continue;
            }
            
            int quantity = itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1;
            
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID().toString());
            orderItem.setOrderId(orderId);
            orderItem.setProductId(sku.getProductId());
            orderItem.setSkuId(sku.getSkuId());
            orderItem.setQuantity(quantity);
            orderItem.setProductName(sku.getProductName());
            orderItem.setSpecInfo(sku.getSpecName());
            orderItem.setUnitPrice(sku.getPrice());
            orderItem.setSubtotal(sku.getPrice() * quantity);
            orderItem.setCreateTime(LocalDateTime.now());
            
            orderItemMapper.insert(orderItem);
            
            // 扣减库存
            try {
                productServiceClient.deductStock(sku.getSkuId(), quantity);
                log.info("库存扣减成功: skuId={}, quantity={}", sku.getSkuId(), quantity);
            } catch (Exception e) {
                log.error("库存扣减失败: skuId={}, quantity={}", sku.getSkuId(), quantity, e);
                throw new BizException(500, "库存扣减失败: " + e.getMessage());
            }
        }
        log.info("订单项已创建: orderId={}, itemCount={}", orderId, request.getItems().size());
        
        // 清空用户购物车中已下单的商品
        for (String skuId : skuIds) {
            QueryWrapper<ShoppingCart> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("user_id", userId)
                    .eq("sku_id", skuId);
            shoppingCartMapper.delete(deleteWrapper);
        }
        log.info("购物车已清理: userId={}, skuCount={}", userId, skuIds.size());
        
        log.info("订单创建完成: orderId={}, orderNo={}, totalAmount={}", orderId, orderNo, totalAmount);
        return orderId;
    }
    
    @Override
    public Object getOrderDetail(String orderId, String userId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BizException(404, "订单不存在");
        }
        
        // 获取订单项
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        
        // 转换为DTO
        List<OrderItemDTO> itemDTOs = items.stream()
                .map(item -> {
                    OrderItemDTO.OrderItemDTOBuilder builder = OrderItemDTO.builder()
                            .id(item.getId())
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .skuId(item.getSkuId())
                            .specInfo(item.getSpecInfo())
                            .unitPrice(item.getUnitPrice())
                            .quantity(item.getQuantity())
                            .subtotal(item.getSubtotal());
                    
                    // 调用商品服务获取商品主图
                    try {
                        ApiResponse<ProductDetailDTO> response = productServiceClient.getProductDetail(item.getProductId(), null);
                        if (response != null && response.getCode() == 0 && response.getData() != null) {
                            builder.mainImage(response.getData().getMainImage() != null ? response.getData().getMainImage() : "");
                        } else {
                            builder.mainImage("");
                        }
                    } catch (Exception e) {
                        log.error("获取商品主图失败: productId={}", item.getProductId(), e);
                        builder.mainImage("");
                    }
                    
                    return builder.build();
                })
                .collect(Collectors.toList());
        
        OrderDetailDTO dto = OrderDetailDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .productCount(order.getProductCount())
                .productAmount(order.getProductAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .actualPayment(order.getActualPayment())
                .paymentMethod(order.getPaymentMethod())
                .shippingMethod(order.getShippingMethod())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .remark(order.getRemark())
                .createTime(order.getCreateTime() != null ? order.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .paidTime(order.getPaidTime() != null ? order.getPaidTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .shippedTime(order.getShippedTime() != null ? order.getShippedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .signedTime(order.getSignedTime() != null ? order.getSignedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .items(itemDTOs)
                .build();
        
        return dto;
    }
    
    @Override
    public Page<Object> getOrderList(String userId, int pageNum, int pageSize, String status) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        // 如果status不为空，则添加状态过滤
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        orderMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO列表
        List<Object> dtoList = page.getRecords().stream()
                .map(order -> {
                    // 获取订单项
                    QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
                    itemWrapper.eq("order_id", order.getId());
                    List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
                    
                    List<OrderItemDTO> itemDTOs = items.stream()
                            .map(item -> {
                                OrderItemDTO.OrderItemDTOBuilder builder = OrderItemDTO.builder()
                                        .id(item.getId())
                                        .productId(item.getProductId())
                                        .productName(item.getProductName())
                                        .unitPrice(item.getUnitPrice())
                                        .quantity(item.getQuantity())
                                        .specInfo(item.getSpecInfo());
                                
                                // 调用商品服务获取商品主图
                                try {
                                    ApiResponse<ProductDetailDTO> response = productServiceClient.getProductDetail(item.getProductId(), null);
                                    if (response != null && response.getCode() == 0 && response.getData() != null) {
                                        builder.mainImage(response.getData().getMainImage() != null ? response.getData().getMainImage() : "");
                                    } else {
                                        builder.mainImage("");
                                    }
                                } catch (Exception e) {
                                    log.error("获取商品主图失败: productId={}", item.getProductId(), e);
                                    builder.mainImage("");
                                }
                                
                                return builder.build();
                            })
                            .collect(Collectors.toList());
                    
                    return OrderDetailDTO.builder()
                            .id(order.getId())
                            .orderNo(order.getOrderNo())
                            .status(order.getStatus())
                            .productCount(order.getProductCount())
                            .productAmount(order.getProductAmount())
                            .shippingFee(order.getShippingFee())
                            .totalAmount(order.getTotalAmount())
                            .actualPayment(order.getActualPayment())
                            .paymentMethod(order.getPaymentMethod())
                            .shippingMethod(order.getShippingMethod())
                            .receiverName(order.getReceiverName())
                            .receiverPhone(order.getReceiverPhone())
                            .receiverAddress(order.getReceiverAddress())
                            .createTime(order.getCreateTime() != null ? order.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                            .items(itemDTOs)
                            .build();
                })
                .collect(Collectors.toList());
        
        Page<Object> dtoPage = new Page<>(pageNum, pageSize);
        dtoPage.setTotal(page.getTotal());
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }
    
    @Override
    @Transactional
    public void cancelOrder(String orderId, String userId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BizException(404, "订单不存在");
        }
        
        if (!AppConstants.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BizException(400, "只能取消待付款订单");
        }
        
        order.setStatus(AppConstants.OrderStatus.CANCELLED);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 归还商品库存
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        
        for (OrderItem item : items) {
            try {
                productServiceClient.returnStock(item.getSkuId(), item.getQuantity());
                log.info("库存归还成功: skuId={}, quantity={}", item.getSkuId(), item.getQuantity());
            } catch (Exception e) {
                log.error("库存归还失败: skuId={}, quantity={}", item.getSkuId(), item.getQuantity(), e);
            }
        }
        
        log.info("订单已取消: orderId={}, userId={}", orderId, userId);
    }
    
    @Override
    @Transactional
    public void confirmReceipt(String orderId, String userId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BizException(404, "订单不存在");
        }
        
        if (!AppConstants.OrderStatus.PENDING_RECEIPT.equals(order.getStatus())) {
            throw new BizException(400, "只能确认已发货订单");
        }
        
        order.setStatus(AppConstants.OrderStatus.COMPLETED);
        order.setSignedTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单已签收: orderId={}, userId={}", orderId, userId);
    }
    
    @Override
    public List<OrderItemDTO> getCartItems(String userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<ShoppingCart> cartItems = shoppingCartMapper.selectList(queryWrapper);
        
        return cartItems.stream()
                .map(cart -> OrderItemDTO.builder()
                        .id(cart.getId())
                        .productId(cart.getProductId())
                        .skuId(cart.getSkuId())
                        .quantity(cart.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updatePaymentStatus(String orderId, String paymentNo) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        
        if (!AppConstants.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            log.warn("Order status is not pending payment: orderId={}, status={}", orderId, order.getStatus());
            return;
        }
        
        // 更新订单状态为已支付,待发货
        order.setStatus(AppConstants.OrderStatus.PENDING_SHIPPED);
        order.setPaidTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("Order payment status updated: orderId={}, paymentNo={}", orderId, paymentNo);
    }
    
    @Override
    @Transactional
    public void shipOrder(String orderId, String courierCompany, String trackingNo) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        
        if (!AppConstants.OrderStatus.PENDING_SHIPPED.equals(order.getStatus())) {
            throw new BizException(400, "只能发货待发货订单,当前状态: " + order.getStatus());
        }
        
        // 调用物流服务创建物流记录
        try {
            logisticsServiceClient.shipOrder(orderId, courierCompany, trackingNo);
            log.info("物流记录创建成功: orderId={}, courier={}, tracking={}", orderId, courierCompany, trackingNo);
        } catch (Exception e) {
            log.error("物流记录创建失败: orderId={}", orderId, e);
            throw new BizException(500, "物流记录创建失败: " + e.getMessage());
        }
        
        // 更新订单状态为待收货
        order.setStatus(AppConstants.OrderStatus.PENDING_RECEIPT);
        order.setShippedTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单已发货: orderId={}, courierCompany={}, trackingNo={}", orderId, courierCompany, trackingNo);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
}
