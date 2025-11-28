package com.anqigou.order.service;

import java.util.List;

import com.anqigou.order.dto.CartItemDTO;

public interface CartService {

    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param cartItemDTO 购物车项
     */
    void addItemToCart(String userId, CartItemDTO cartItemDTO);

    /**
     * 获取用户的购物车列表
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItemDTO> getCartItems(String userId);

    /**
     * 更新购物车中商品的数量
     * @param userId 用户ID
     * @param skuId 商品SKU ID
     * @param quantity 数量
     */
    void updateCartItemQuantity(String userId, String skuId, int quantity);

    /**
     * 从购物车中移除商品
     * @param userId 用户ID
     * @param skuId 商品SKU ID
     */
    void removeItemFromCart(String userId, String skuId);

    /**
     * 清空用户的购物车
     * @param userId 用户ID
     */
    void clearCart(String userId);
}
