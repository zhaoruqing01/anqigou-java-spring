package com.anqigou.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.product.dto.ProductDetailDTO;
import com.anqigou.product.dto.ProductListItemDTO;
import com.anqigou.product.service.ProductService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
@Validated
@Slf4j
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailDTO> getProductDetail(@PathVariable String productId,
                                                         @RequestAttribute(required = false) String userId) {
        ProductDetailDTO product = productService.getProductDetail(productId, userId);
        return ApiResponse.success(product);
    }
    
    /**
     * 商品列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<ProductListItemDTO>> listProducts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy) {
        Page<ProductListItemDTO> products = productService.listProducts(pageNum, pageSize, categoryId, keyword, sortBy);
        return ApiResponse.success(products);
    }
    
    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ApiResponse<Page<ProductListItemDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ProductListItemDTO> products = productService.searchProducts(pageNum, pageSize, keyword);
        return ApiResponse.success(products);
    }
    
    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    public ApiResponse<List<ProductListItemDTO>> getHotProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductListItemDTO> hotProducts = productService.getHotProducts(limit);
        return ApiResponse.success(hotProducts);
    }
    
    /**
     * 获取推荐商品
     */
    @GetMapping("/recommended")
    public ApiResponse<List<ProductListItemDTO>> getRecommendedProducts(
            @RequestAttribute(required = false) String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductListItemDTO> recommendedProducts = productService.getRecommendedProducts(userId, limit);
        return ApiResponse.success(recommendedProducts);
    }
}
