package com.anqigou.order.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.common.exception.BizException;
import com.anqigou.order.dto.CartItemDTO;
import com.anqigou.order.entity.CartItem;
import com.anqigou.order.mapper.CartMapper;
import com.anqigou.order.service.CartService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public void addItemToCart(String userId, CartItemDTO cartItemDTO) {
        // 验证参数
        if (cartItemDTO.getSkuId() == null || cartItemDTO.getQuantity() == null || cartItemDTO.getQuantity() <= 0) {
            throw new BizException(400, "商品信息不完整或数量无效");
        }

        // 检查购物车中是否已存在该商品 (userId, productId, skuId)
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", cartItemDTO.getProductId())
                   .eq("sku_id", cartItemDTO.getSkuId());
        
        CartItem existingItem = cartMapper.selectOne(queryWrapper);
        
        if (existingItem != null) {
            // 如果存在，则更新数量
            existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
            existingItem.setUpdateTime(new Date());
            cartMapper.updateById(existingItem);
            log.info("Updated cart item quantity: userId={}, skuId={}, newQuantity={}", 
                     userId, cartItemDTO.getSkuId(), existingItem.getQuantity());
        } else {
            // 如果不存在，则插入新记录
            CartItem newItem = new CartItem();
            newItem.setId(UUID.randomUUID().toString());
            newItem.setUserId(userId);
            newItem.setProductId(cartItemDTO.getProductId());
            newItem.setSkuId(cartItemDTO.getSkuId());
            newItem.setQuantity(cartItemDTO.getQuantity());
            newItem.setCreateTime(new Date());
            newItem.setUpdateTime(new Date());
            cartMapper.insert(newItem);
            log.info("Added new item to cart: userId={}, skuId={}, quantity={}", 
                     userId, cartItemDTO.getSkuId(), cartItemDTO.getQuantity());
        }
    }

    @Override
    public List<CartItemDTO> getCartItems(String userId) {
        // 根据 userId 从数据库查询所有购物车项
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        
        List<CartItem> cartItems = cartMapper.selectList(queryWrapper);
        
        if (cartItems == null || cartItems.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 将实体转换为 DTO 列表
        List<CartItemDTO> dtoList = cartItems.stream()
                .map(item -> {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setSkuId(item.getSkuId());
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    
                    // TODO: 调用product-service获取商品详细信息
                    // 包括：productName, mainImage, specInfo, price, stock
                    // 暂时返回基础信息
                    
                    return dto;
                })
                .collect(Collectors.toList());
        
        log.info("Retrieved cart items: userId={}, itemCount={}", userId, dtoList.size());
        return dtoList;
    }

    @Override
    public void updateCartItemQuantity(String userId, String skuId, int quantity) {
        // 检查数量是否大于0
        if (quantity <= 0) {
            throw new BizException(400, "商品数量必须大于0");
        }
        
        // 查找购物车项
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("sku_id", skuId);
        
        CartItem cartItem = cartMapper.selectOne(queryWrapper);
        
        if (cartItem == null) {
            throw new BizException(404, "购物车中不存在该商品");
        }
        
        // 更新数量
        cartItem.setQuantity(quantity);
        cartItem.setUpdateTime(new Date());
        cartMapper.updateById(cartItem);
        
        log.info("Updated cart item quantity: userId={}, skuId={}, newQuantity={}", 
                 userId, skuId, quantity);
    }

    @Override
    public void removeItemFromCart(String userId, String skuId) {
        // 从数据库中删除指定的购物车项
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("sku_id", skuId);
        
        int deletedCount = cartMapper.delete(queryWrapper);
        
        if (deletedCount == 0) {
            throw new BizException(404, "购物车中不存在该商品");
        }
        
        log.info("Removed item from cart: userId={}, skuId={}", userId, skuId);
    }

    @Override
    public void clearCart(String userId) {
        // 从数据库中删除该用户的所有购物车项
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        int deletedCount = cartMapper.delete(queryWrapper);
        
        log.info("Cleared cart: userId={}, deletedCount={}", userId, deletedCount);
    }
}
