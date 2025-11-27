package com.anqigou.product.service;

import com.anqigou.product.dto.ProductDetailDTO;
import com.anqigou.product.dto.ProductListItemDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 获取商品详情
     */
    ProductDetailDTO getProductDetail(String productId, String userId);
    
    /**
     * 分页查询商品列表
     */
    Page<ProductListItemDTO> listProducts(int pageNum, int pageSize, String categoryId, 
                                          String keyword, String sortBy);
    
    /**
     * 搜索商品
     */
    Page<ProductListItemDTO> searchProducts(int pageNum, int pageSize, String keyword);
    
    /**
     * 获取热门商品
     */
    List<ProductListItemDTO> getHotProducts(int limit);
    
    /**
     * 获取推荐商品
     */
    List<ProductListItemDTO> getRecommendedProducts(String userId, int limit);
}
