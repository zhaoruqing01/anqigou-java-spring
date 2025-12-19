package com.anqigou.product.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.anqigou.common.exception.BizException;
import com.anqigou.common.util.ElasticsearchUtil;
import com.anqigou.product.document.ProductDocument;
import com.anqigou.product.dto.ProductDetailDTO;
import com.anqigou.product.dto.ProductListItemDTO;
import com.anqigou.product.dto.ProductSkuDTO;
import com.anqigou.product.dto.SkuStockDTO;
import com.anqigou.product.entity.Product;
import com.anqigou.product.entity.ProductCategory;
import com.anqigou.product.entity.ProductReview;
import com.anqigou.product.entity.ProductSku;
import com.anqigou.product.mapper.ProductCategoryMapper;
import com.anqigou.product.mapper.ProductMapper;
import com.anqigou.product.mapper.ProductReviewMapper;
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

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    @Value("${elasticsearch.enabled:false}")
    private boolean elasticsearchEnabled;
    
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
        // 如果启用了Elasticsearch且有搜索关键词，优先使用ES搜索
        if (elasticsearchEnabled && keyword != null && !keyword.isEmpty()) {
            try {
                return searchProductsByElasticsearch(pageNum, pageSize, categoryId, keyword, sortBy);
            } catch (Exception e) {
                log.error("Elasticsearch搜索失败，降级使用数据库查询: {}", e.getMessage());
                // 降级到数据库查询
            }
        }

        // 使用数据库查询（默认方式或降级方式）
        return searchProductsByDatabase(pageNum, pageSize, categoryId, keyword, sortBy);
    }

    /**
     * 使用Elasticsearch搜索商品
     */
    private Page<ProductListItemDTO> searchProductsByElasticsearch(int pageNum, int pageSize,
                                                                    String categoryId, String keyword, String sortBy) {
        // 确定排序字段和方向
        String sortField = "createTime";
        SortOrder sortOrder = SortOrder.DESC;

        if ("price_asc".equals(sortBy)) {
            sortField = "price";
            sortOrder = SortOrder.ASC;
        } else if ("price_desc".equals(sortBy)) {
            sortField = "price";
            sortOrder = SortOrder.DESC;
        } else if ("hot".equals(sortBy)) {
            sortField = "soldCount";
            sortOrder = SortOrder.DESC;
        }

        // 定义搜索字段（商品名称、品牌、描述）
        String[] searchFields = {"name", "brand", "description"};

        // 计算分页参数
        int from = (pageNum - 1) * pageSize;

        // 执行ES高级搜索
        List<Map<String, Object>> results = elasticsearchUtil.advancedSearch(
                ProductDocument.getIndexName(),
                keyword,
                searchFields,
                categoryId,
                sortField,
                sortOrder,
                from,
                pageSize
        );

        // 转换ES结果为DTO
        List<ProductListItemDTO> dtoList = results.stream()
                .map(this::convertMapToProductListItemDTO)
                .collect(Collectors.toList());

        // 统计总数（这里简化处理，实际应该单独查询总数）
        long total = elasticsearchUtil.count(ProductDocument.getIndexName(), keyword, searchFields);

        Page<ProductListItemDTO> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.setRecords(dtoList);

        log.info("Elasticsearch搜索成功: keyword={}, hits={}, total={}", keyword, dtoList.size(), total);
        return page;
    }

    /**
     * 使用数据库搜索商品（原有逻辑，支持增强的模糊匹配）
     */
    private Page<ProductListItemDTO> searchProductsByDatabase(int pageNum, int pageSize,
                                                               String categoryId, String keyword, String sortBy) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1);

        if (categoryId != null && !categoryId.isEmpty()) {
            queryWrapper.eq("category_id", categoryId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            // 增强的模糊搜索：支持每个字符的模糊匹配
            // 将搜索词拆分成单个字符，构建更灵活的搜索条件
            String fuzzyKeyword = keyword.trim();
            
            // 策略1: 完整词匹配（优先级最高）
            queryWrapper.and(wrapper -> {
                wrapper.like("name", fuzzyKeyword)
                       .or().like("brand", fuzzyKeyword)
                       .or().like("description", fuzzyKeyword);
                
                // 策略2: 拆分字符模糊匹配（支持 "水国" 匹配 "水果"）
                // 为每个字符构建like条件
                if (fuzzyKeyword.length() >= 2) {
                    for (int i = 0; i < fuzzyKeyword.length(); i++) {
                        String singleChar = String.valueOf(fuzzyKeyword.charAt(i));
                        wrapper.or().like("name", singleChar);
                    }
                }
            });
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

    /**
     * 将ES返回的Map转换为ProductListItemDTO
     */
    private ProductListItemDTO convertMapToProductListItemDTO(Map<String, Object> source) {
        return ProductListItemDTO.builder()
                .id((String) source.get("id"))
                .name((String) source.get("name"))
                .price(source.get("price") != null ? ((Number) source.get("price")).longValue() : 0L)
                .originalPrice(source.get("originalPrice") != null ? ((Number) source.get("originalPrice")).longValue() : 0L)
                .mainImage((String) source.get("mainImage"))
                .soldCount(source.get("soldCount") != null ? ((Number) source.get("soldCount")).intValue() : 0)
                .rating(source.get("rating") != null ? ((Number) source.get("rating")).doubleValue() : 0.0)
                .build();
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
    
    @Override
    public List<ProductCategory> listCategories() {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1)
                .orderByAsc("sort_order")
                .orderByAsc("level");
        
        return productCategoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<ProductCategory> listFirstLevelCategories() {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1)
                .eq("level", 1)
                .orderByAsc("sort_order");
        
        return productCategoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<ProductCategory> listSubCategories(String parentId) {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1)
                .eq("parent_id", parentId)
                .orderByAsc("sort_order");
        
        return productCategoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public Page<ProductReview> listProductReviews(int pageNum, int pageSize, String productId, Integer rating) {
        QueryWrapper<ProductReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("product_id", productId)
                .orderByDesc("create_time");
        
        if (rating != null) {
            queryWrapper.eq("rating", rating);
        }
        
        Page<ProductReview> page = new Page<>(pageNum, pageSize);
        productReviewMapper.selectPage(page, queryWrapper);
        
        return page;
    }
    
    @Override
    public Map<String, Object> getProductReviewStats(String productId) {
        QueryWrapper<ProductReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("product_id", productId);
        
        // 获取总评价数
        Long totalCount = productReviewMapper.selectCount(queryWrapper);
        
        // 获取各评分数量
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("rating1Count", 0);
        stats.put("rating2Count", 0);
        stats.put("rating3Count", 0);
        stats.put("rating4Count", 0);
        stats.put("rating5Count", 0);
        
        if (totalCount > 0) {
            // 统计各评分数量
            for (int i = 1; i <= 5; i++) {
                queryWrapper.clear();
                queryWrapper.eq("deleted", 0)
                        .eq("product_id", productId)
                        .eq("rating", i);
                Long count = productReviewMapper.selectCount(queryWrapper);
                stats.put("rating" + i + "Count", count);
            }
        } else {
            stats.put("avgRating", 0.0);
        }
        
        return stats;
    }
    
    @Override
    public List<SkuStockDTO> batchGetSkuStock(List<String> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        QueryWrapper<ProductSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", skuIds)
                .eq("deleted", 0);
        
        List<ProductSku> skus = productSkuMapper.selectList(queryWrapper);
        
        return skus.stream()
                .map(sku -> {
                    // 查询商品信息
                    Product product = productMapper.selectById(sku.getProductId());
                    
                    return SkuStockDTO.builder()
                            .skuId(sku.getId())
                            .productId(sku.getProductId())
                            .productName(product != null ? product.getName() : "未知商品")
                            .specName(sku.getSpecName())
                            .specValueJson(sku.getSpecValueJson())
                            .price(sku.getPrice())
                            .stock(sku.getStock())
                            .sellerId(product != null ? product.getSellerId() : "default-seller")
                            .mainImage(product != null ? product.getMainImage() : "")
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public void deductStock(String skuId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BizException(400, "扣减数量必须大于0");
        }
        
        ProductSku sku = productSkuMapper.selectById(skuId);
        
        if (sku == null || sku.getDeleted() == 1) {
            throw new BizException(404, "SKU不存在");
        }
        
        if (sku.getStock() < quantity) {
            throw new BizException(400, "库存不足，当前库存：" + sku.getStock());
        }
        
        // 扣减库存
        sku.setStock(sku.getStock() - quantity);
        productSkuMapper.updateById(sku);
        
        log.info("扣减库存成功: skuId={}, quantity={}, remainStock={}", skuId, quantity, sku.getStock());
    }
    
    @Override
    public void returnStock(String skuId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BizException(400, "归还数量必须大于0");
        }
        
        ProductSku sku = productSkuMapper.selectById(skuId);
        
        if (sku == null || sku.getDeleted() == 1) {
            throw new BizException(404, "SKU不存在");
        }
        
        // 归还库存
        sku.setStock(sku.getStock() + quantity);
        productSkuMapper.updateById(sku);
        
        log.info("归还库存成功: skuId={}, quantity={}, currentStock={}", skuId, quantity, sku.getStock());
    }
}
