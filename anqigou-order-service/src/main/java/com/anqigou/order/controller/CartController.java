package com.anqigou.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.dto.CartItemDTO;
import com.anqigou.order.service.CartService;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public ApiResponse<String> addItemToCart(@RequestAttribute String userId, @RequestBody CartItemDTO cartItemDTO) {
        cartService.addItemToCart(userId, cartItemDTO);
        return ApiResponse.success("添加成功");
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CartItemDTO>> getCartItems(@RequestAttribute String userId) {
        List<CartItemDTO> cartItems = cartService.getCartItems(userId);
        return ApiResponse.success(cartItems);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public ApiResponse<String> updateCartItemQuantity(@RequestAttribute String userId, @RequestBody CartItemDTO cartItemDTO) {
        cartService.updateCartItemQuantity(userId, cartItemDTO.getSkuId(), cartItemDTO.getQuantity());
        return ApiResponse.success("更新成功");
    }
    
    /**
     * 移除购物车商品
     */
    @DeleteMapping("/remove")
    public ApiResponse<String> removeItemFromCart(@RequestAttribute String userId, @RequestParam String skuId) {
        cartService.removeItemFromCart(userId, skuId);
        return ApiResponse.success("移除成功");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(@RequestAttribute String userId) {
        cartService.clearCart(userId);
        return ApiResponse.success("购物车已清空");
    }
}
