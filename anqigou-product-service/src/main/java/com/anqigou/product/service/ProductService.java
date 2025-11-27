package com.anqigou.product.service;

import com.anqigou.product.dto.ProductDetailDTO;
import com.anqigou.product.dto.ProductListItemDTO;
import com.anqigou.product.entity.ProductCategory;
import com.anqigou.product.entity.ProductReview;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 获取商品分类列表
     */
    List<ProductCategory> listCategories();
    
    /**
     * 获取一级分类列表
     */
    List<ProductCategory> listFirstLevelCategories();
    
    /**
     * 根据父分类ID获取子分类列表
     */
    List<ProductCategory> listSubCategories(String parentId);
    
    /**
     * 获取商品评价列表
     */
    Page<ProductReview> listProductReviews(int pageNum, int pageSize, String productId, Integer rating);
    
    /**
     * 获取商品评价统计
     */
    Map<String, Object> getProductReviewStats(String productId);
}
