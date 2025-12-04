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

import lombok.extern.slf4j.Slf4j;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@Validated
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public ApiResponse<String> addItemToCart(@RequestAttribute(required = false) String userId, @RequestBody CartItemDTO cartItemDTO) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            cartService.addItemToCart(userId, cartItemDTO);
            return ApiResponse.success("添加成功");
        } catch (Exception e) {
            log.error("Add item to cart failed", e);
            return ApiResponse.failure(500, "Add item to cart failed: " + e.getMessage());
        }
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CartItemDTO>> getCartItems(@RequestAttribute(required = false) String userId) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            List<CartItemDTO> cartItems = cartService.getCartItems(userId);
            return ApiResponse.success(cartItems);
        } catch (Exception e) {
            log.error("Get cart items failed", e);
            return ApiResponse.failure(500, "Get cart items failed: " + e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public ApiResponse<String> updateCartItemQuantity(@RequestAttribute(required = false) String userId, @RequestBody CartItemDTO cartItemDTO) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            cartService.updateCartItemQuantity(userId, cartItemDTO.getSkuId(), cartItemDTO.getQuantity());
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            log.error("Update cart item quantity failed", e);
            return ApiResponse.failure(500, "Update cart item quantity failed: " + e.getMessage());
        }
    }
    
    /**
     * 移除购物车商品
     */
    @DeleteMapping("/remove")
    public ApiResponse<String> removeItemFromCart(@RequestAttribute(required = false) String userId, @RequestParam String skuId) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            cartService.removeItemFromCart(userId, skuId);
            return ApiResponse.success("移除成功");
        } catch (Exception e) {
            log.error("Remove item from cart failed", e);
            return ApiResponse.failure(500, "Remove item from cart failed: " + e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(@RequestAttribute(required = false) String userId) {
        try {
            if (userId == null) {
                return ApiResponse.failure(401, "User ID missing");
            }
            cartService.clearCart(userId);
            return ApiResponse.success("购物车已清空");
        } catch (Exception e) {
            log.error("Clear cart failed", e);
            return ApiResponse.failure(500, "Clear cart failed: " + e.getMessage());
        }
    }
}
