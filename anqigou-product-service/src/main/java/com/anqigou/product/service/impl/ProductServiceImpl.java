package com.anqigou.product.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.common.exception.BizException;
import com.anqigou.product.dto.ProductDetailDTO;
import com.anqigou.product.dto.ProductListItemDTO;
import com.anqigou.product.dto.ProductSkuDTO;
import com.anqigou.product.entity.Product;
import com.anqigou.product.entity.ProductSku;
import com.anqigou.product.mapper.ProductMapper;
import com.anqigou.product.mapper.ProductSkuMapper;
import com.anqigou.product.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品服务实现
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductSkuMapper productSkuMapper;
    
    @Override
    public ProductDetailDTO getProductDetail(String productId, String userId) {
        Product product = productMapper.selectById(productId);
        
        if (product == null || product.getDeleted() == 1) {
            throw new BizException(404, "商品不存在");
        }
        
        // 获取SKU列表
        QueryWrapper<ProductSku> skuWrapper = new QueryWrapper<>();
        skuWrapper.eq("product_id", productId)
                .eq("deleted", 0);
        List<ProductSku> skus = productSkuMapper.selectList(skuWrapper);
        
        // 转换SKU为DTO
        List<ProductSkuDTO> skuDTOs = skus.stream()
                .map(sku -> ProductSkuDTO.builder()
                        .id(sku.getId())
                        .specValue(sku.getSpecName())
                        .price(sku.getPrice())
                        .stock(sku.getStock())
                        .build())
                .collect(Collectors.toList());
        
        // 解析商品图片JSON
        List<String> images = new ArrayList<>();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            try {
                // 简单的JSON数组解析
                String imageStr = product.getImages();
                if (imageStr.startsWith("[") && imageStr.endsWith("]")) {
                    String[] parts = imageStr.substring(1, imageStr.length()-1).split(",");
                    for (String part : parts) {
                        String url = part.trim().replace("\"", "");
                        if (!url.isEmpty()) {
                            images.add(url);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to parse product images: {}", productId);
                images = new ArrayList<>();
            }
        }
        
        ProductDetailDTO dto = ProductDetailDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .images(images)
                .detailHtml("") // TODO: 从数据库获取详情HTML
                .skus(skuDTOs)
                .soldCount(product.getSoldCount())
                .rating(product.getRating() != null ? product.getRating().doubleValue() : 0.0)
                .ratingCount(product.getRatingCount())
                .mainImage(product.getMainImage())
                .build();
        
        return dto;
    }
    
    @Override
    public Page<ProductListItemDTO> listProducts(int pageNum, int pageSize, String categoryId, 
                                                 String keyword, String sortBy) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1);
        
        if (categoryId != null && !categoryId.isEmpty()) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("name", keyword);
        }
        
        // 排序处理
        if ("price_asc".equals(sortBy)) {
            queryWrapper.orderByAsc("price");
        } else if ("price_desc".equals(sortBy)) {
            queryWrapper.orderByDesc("price");
        } else if ("hot".equals(sortBy)) {
            queryWrapper.orderByDesc("sold_count");
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        
        Page<Product> page = new Page<>(pageNum, pageSize);
        productMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<ProductListItemDTO> dtoList = page.getRecords().stream()
                .map(product -> ProductListItemDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .originalPrice(product.getOriginalPrice())
                        .mainImage(product.getMainImage())
                        .soldCount(product.getSoldCount())
                        .rating(product.getRating() != null ? product.getRating().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
        
        Page<ProductListItemDTO> dtoPage = new Page<>(pageNum, pageSize);
        dtoPage.setTotal(page.getTotal());
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }
    
    @Override
    public Page<ProductListItemDTO> searchProducts(int pageNum, int pageSize, String keyword) {
        return listProducts(pageNum, pageSize, null, keyword, null);
    }
    
    @Override
    public List<ProductListItemDTO> getHotProducts(int limit) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1)
                .orderByDesc("sold_count")
                .last("LIMIT " + limit);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        
        return products.stream()
                .map(product -> ProductListItemDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .originalPrice(product.getOriginalPrice())
                        .mainImage(product.getMainImage())
                        .soldCount(product.getSoldCount())
                        .rating(product.getRating() != null ? product.getRating().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductListItemDTO> getRecommendedProducts(String userId, int limit) {
        // 推荐逻辑：暂时返回随机商品
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1)
                .orderByDesc("rating")
                .last("LIMIT " + limit);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        
        return products.stream()
                .map(product -> ProductListItemDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .originalPrice(product.getOriginalPrice())
                        .mainImage(product.getMainImage())
                        .soldCount(product.getSoldCount())
                        .rating(product.getRating() != null ? product.getRating().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
}
