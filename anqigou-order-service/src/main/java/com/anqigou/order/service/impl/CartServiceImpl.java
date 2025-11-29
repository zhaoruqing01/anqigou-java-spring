package com.anqigou.order.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.common.exception.BizException;
import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.client.ProductServiceClient;
import com.anqigou.order.dto.CartItemDTO;
import com.anqigou.order.dto.ProductDetailDTO;
import com.anqigou.order.dto.ProductSkuDTO;
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
    
    @Autowired
    private ProductServiceClient productServiceClient;

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
        
        // 将实体转换为 DTO 列表，并填充商品详细信息
        List<CartItemDTO> dtoList = cartItems.stream()
                .map(item -> {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setSkuId(item.getSkuId());
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    
                    // 调用product-service获取商品详细信息
                    try {
                        log.debug("Calling product service for productId: {}, userId: {}", item.getProductId(), userId);
                        ApiResponse<ProductDetailDTO> response = productServiceClient.getProductDetail(item.getProductId(), userId);
                        log.info("Product service response for productId {}: code={}, message={}, data=null?{}", 
                                item.getProductId(), 
                                response != null ? response.getCode() : "null", 
                                response != null ? response.getMessage() : "null",
                                response != null && response.getData() != null ? "false" : "true");
                        
                        if (response != null && response.getCode() == 200 && response.getData() != null) {
                            ProductDetailDTO productDetail = response.getData();
                            log.debug("Product details - name: {}, mainImage: {}, price: {}", 
                                    productDetail.getName(), productDetail.getMainImage(), productDetail.getPrice());
                                    
                            dto.setProductName(productDetail.getName() != null ? productDetail.getName() : "未知商品");
                            dto.setMainImage(productDetail.getMainImage() != null ? productDetail.getMainImage() : "");
                            dto.setPrice(productDetail.getPrice() != null ? productDetail.getPrice() : 0L);
                            
                            // 查找对应的SKU信息
                            if (productDetail.getSkus() != null) {
                                boolean foundSku = false;
                                for (ProductSkuDTO sku : productDetail.getSkus()) {
                                    if (sku.getId().equals(item.getSkuId())) {
                                        dto.setSpecInfo(sku.getSpecValue() != null ? sku.getSpecValue() : "");
                                        dto.setStock(sku.getStock() != null ? sku.getStock() : 0);
                                        log.debug("Found matching SKU - specInfo: {}, stock: {}", 
                                                sku.getSpecValue(), sku.getStock());
                                        foundSku = true;
                                        break;
                                    }
                                }
                                
                                // 如果没找到匹配的SKU，设置默认值
                                if (!foundSku) {
                                    dto.setSpecInfo("");
                                    dto.setStock(0);
                                    log.debug("No matching SKU found for skuId: {}", item.getSkuId());
                                }
                            } else {
                                dto.setSpecInfo("");
                                dto.setStock(0);
                                log.debug("No SKUs found in product detail");
                            }
                        } else {
                            log.warn("Failed to get product details for productId {}: response={}", item.getProductId(), response);
                            // 设置默认值
                            dto.setProductName("未知商品");
                            dto.setMainImage("");
                            dto.setPrice(0L);
                            dto.setSpecInfo("");
                            dto.setStock(0);
                        }
                    } catch (Exception e) {
                        log.error("获取商品详情失败: productId={}, skuId={}", item.getProductId(), item.getSkuId(), e);
                        // 即使获取商品详情失败，也要设置默认值以保证前端正常显示
                        dto.setProductName("未知商品");
                        dto.setMainImage("");
                        dto.setPrice(0L);
                        dto.setSpecInfo("");
                        dto.setStock(0);
                    }
                    
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