package com.anqigou.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anqigou.common.constant.AppConstants;
import com.anqigou.common.exception.BizException;
import com.anqigou.order.dto.OrderDetailDTO;
import com.anqigou.order.dto.OrderItemDTO;
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
    
    @Override
    @Transactional
    public String createOrder(String userId, String addressId, int paymentMethod, String remark) {
        // 获取购物车中已选中的商品
        QueryWrapper<ShoppingCart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", userId);
        List<ShoppingCart> cartItems = shoppingCartMapper.selectList(cartWrapper);
        
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BizException(400, "购物车为空");
        }
        
        // 生成订单号
        String orderNo = generateOrderNo();
        String orderId = UUID.randomUUID().toString();
        
        // 计算订单金额
        long productAmount = 0;
        long shippingFee = 1000; // 默认配送费10元
        long discountAmount = 0;
        
        // 创建订单项
        for (ShoppingCart cart : cartItems) {
            productAmount += cart.getQuantity() * 0; // TODO: 获取商品实际价格
        }
        
        long totalAmount = productAmount + shippingFee - discountAmount;
        
        // 创建订单
        Order order = Order.builder()
                .id(orderId)
                .orderNo(orderNo)
                .userId(userId)
                .sellerId("") // TODO: 从商品信息获取商家ID
                .productCount(cartItems.size())
                .productAmount(productAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .actualPayment(totalAmount)
                .paymentMethod(paymentMethod == 1 ? "weixin" : "alipay")
                .shippingMethod("normal")
                .status(AppConstants.OrderStatus.PENDING_PAYMENT)
                .remark(remark)
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();
        
        orderMapper.insert(order);
        
        // 清空购物车
        shoppingCartMapper.delete(cartWrapper);
        
        log.info("订单已创建: orderId={}, orderNo={}, totalAmount={}", orderId, orderNo, totalAmount);
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
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .skuId(item.getSkuId())
                        .specInfo(item.getSpecInfo())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .mainImage("") // TODO: 从商品表获取主图
                        .build())
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
    public Page<Object> getOrderList(String userId, int pageNum, int pageSize) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        
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
                            .map(item -> OrderItemDTO.builder()
                                    .id(item.getId())
                                    .productId(item.getProductId())
                                    .productName(item.getProductName())
                                    .unitPrice(item.getUnitPrice())
                                    .quantity(item.getQuantity())
                                    .mainImage("") // TODO: 从商品表获取
                                    .specInfo(item.getSpecInfo())
                                    .build())
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
        
        // TODO: 归还商品库存
        
        log.info("订单已取消: orderId={}, userId={}", orderId, userId);
    }
    
    @Override
    @Transactional
    public void confirmReceipt(String orderId, String userId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BizException(404, "订单不存在");
        }
        
        if (!"SHIPPED".equals(order.getStatus())) {
            throw new BizException(400, "只能确认已发货订单");
        }
        
        order.setStatus("COMPLETED");
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
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
}
