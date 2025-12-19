package com.anqigou.product.service;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.anqigou.common.util.ElasticsearchUtil;
import com.anqigou.product.document.ProductDocument;
import com.anqigou.product.entity.Product;
import com.anqigou.product.entity.ProductCategory;
import com.anqigou.product.mapper.ProductCategoryMapper;
import com.anqigou.product.mapper.ProductMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品Elasticsearch同步服务
 * 负责商品数据与Elasticsearch的同步
 */
@Service
@Slf4j
public class ProductSyncService {

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Value("${elasticsearch.enabled:false}")
    private boolean elasticsearchEnabled;

    @Value("${elasticsearch.auto-init:false}")
    private boolean autoInit;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 应用启动时初始化Elasticsearch索引
     */
    @PostConstruct
    public void init() {
        if (!elasticsearchEnabled) {
            log.info("Elasticsearch未启用，跳过索引初始化");
            return;
        }

        try {
            // 创建商品索引
            String indexName = ProductDocument.getIndexName();
            if (!elasticsearchUtil.indexExists(indexName)) {
                boolean created = elasticsearchUtil.createIndex(indexName, ProductDocument.getIndexMapping());
                if (created) {
                    log.info("成功创建Elasticsearch索引: {}", indexName);

                    // 如果配置了自动初始化，则同步所有商品
                    if (autoInit) {
                        log.info("开始全量同步商品数据到Elasticsearch...");
                        syncAllProducts();
                    }
                } else {
                    log.error("创建Elasticsearch索引失败: {}", indexName);
                }
            } else {
                log.info("Elasticsearch索引已存在: {}", indexName);
            }
        } catch (Exception e) {
            log.error("Elasticsearch索引初始化失败", e);
        }
    }

    /**
     * 同步单个商品到Elasticsearch
     */
    public boolean syncProduct(String productId) {
        if (!elasticsearchEnabled) {
            return false;
        }

        try {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                log.warn("商品不存在，无法同步到ES: {}", productId);
                return false;
            }

            ProductDocument document = convertToDocument(product);
            String json = objectMapper.writeValueAsString(document);

            boolean success = elasticsearchUtil.indexDocument(
                    ProductDocument.getIndexName(),
                    productId,
                    json
            );

            if (success) {
                log.info("商品同步到ES成功: {}", productId);
            }

            return success;
        } catch (Exception e) {
            log.error("商品同步到ES失败: {}", productId, e);
            return false;
        }
    }

    /**
     * 批量同步商品到Elasticsearch
     */
    public boolean syncProducts(List<String> productIds) {
        if (!elasticsearchEnabled || productIds == null || productIds.isEmpty()) {
            return false;
        }

        try {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", productIds);
            List<Product> products = productMapper.selectList(queryWrapper);

            Map<String, String> documents = new HashMap<>();
            for (Product product : products) {
                ProductDocument document = convertToDocument(product);
                String json = objectMapper.writeValueAsString(document);
                documents.put(product.getId(), json);
            }

            boolean success = elasticsearchUtil.bulkIndex(ProductDocument.getIndexName(), documents);

            if (success) {
                log.info("批量同步商品到ES成功: count={}", documents.size());
            }

            return success;
        } catch (Exception e) {
            log.error("批量同步商品到ES失败", e);
            return false;
        }
    }

    /**
     * 从Elasticsearch删除商品
     */
    public boolean deleteProduct(String productId) {
        if (!elasticsearchEnabled) {
            return false;
        }

        try {
            boolean success = elasticsearchUtil.deleteDocument(ProductDocument.getIndexName(), productId);

            if (success) {
                log.info("从ES删除商品成功: {}", productId);
            }

            return success;
        } catch (Exception e) {
            log.error("从ES删除商品失败: {}", productId, e);
            return false;
        }
    }

    /**
     * 全量同步所有商品到Elasticsearch
     * 仅同步已上架且未删除的商品
     */
    public int syncAllProducts() {
        if (!elasticsearchEnabled) {
            log.warn("Elasticsearch未启用，无法执行全量同步");
            return 0;
        }

        try {
            // 查询所有已上架且未删除的商品
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1)
                    .eq("deleted", 0);

            List<Product> products = productMapper.selectList(queryWrapper);

            if (products.isEmpty()) {
                log.info("没有需要同步的商品");
                return 0;
            }

            Map<String, String> documents = new HashMap<>();
            for (Product product : products) {
                try {
                    ProductDocument document = convertToDocument(product);
                    String json = objectMapper.writeValueAsString(document);
                    documents.put(product.getId(), json);
                } catch (Exception e) {
                    log.error("转换商品文档失败: {}", product.getId(), e);
                }
            }

            boolean success = elasticsearchUtil.bulkIndex(ProductDocument.getIndexName(), documents);

            if (success) {
                log.info("全量同步商品到ES成功: total={}", documents.size());
                return documents.size();
            } else {
                log.error("全量同步商品到ES失败");
                return 0;
            }
        } catch (Exception e) {
            log.error("全量同步商品到ES异常", e);
            return 0;
        }
    }

    /**
     * 重建索引（删除旧索引，创建新索引，全量同步）
     */
    public boolean rebuildIndex() {
        if (!elasticsearchEnabled) {
            log.warn("Elasticsearch未启用，无法重建索引");
            return false;
        }

        try {
            String indexName = ProductDocument.getIndexName();

            // 删除旧索引
            log.info("开始删除旧索引: {}", indexName);
            elasticsearchUtil.deleteIndex(indexName);

            // 创建新索引
            log.info("开始创建新索引: {}", indexName);
            boolean created = elasticsearchUtil.createIndex(indexName, ProductDocument.getIndexMapping());

            if (!created) {
                log.error("创建索引失败: {}", indexName);
                return false;
            }

            // 全量同步
            log.info("开始全量同步商品数据...");
            int count = syncAllProducts();

            log.info("索引重建完成，同步商品数: {}", count);
            return count > 0;
        } catch (Exception e) {
            log.error("重建索引失败", e);
            return false;
        }
    }

    /**
     * 将Product实体转换为ProductDocument
     */
    private ProductDocument convertToDocument(Product product) {
        // 查询分类名称
        String categoryName = "";
        if (product.getCategoryId() != null && !product.getCategoryId().isEmpty()) {
            ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }

        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .soldCount(product.getSoldCount())
                .rating(product.getRating())
                .ratingCount(product.getRatingCount())
                .mainImage(product.getMainImage())
                .status(product.getStatus())
                .deleted(product.getDeleted())
                .sellerId(product.getSellerId())
                .createTime(product.getCreateTime() != null ?
                        product.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .updateTime(product.getUpdateTime() != null ?
                        product.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .build();
    }
}
