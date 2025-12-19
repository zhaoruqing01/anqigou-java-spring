package com.anqigou.product.document;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品Elasticsearch文档模型
 * 用于商品搜索、模糊匹配
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    /**
     * 商品ID
     */
    private String id;

    /**
     * 商品名称（支持分词和模糊搜索）
     */
    private String name;

    /**
     * 品牌（支持分词和模糊搜索）
     */
    private String brand;

    /**
     * 商品描述（支持分词和模糊搜索）
     */
    private String description;

    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 售价（单位：分）
     */
    private Long price;

    /**
     * 原价（单位：分）
     */
    private Long originalPrice;

    /**
     * 销售数量
     */
    private Integer soldCount;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 评价数量
     */
    private Integer ratingCount;

    /**
     * 主图URL
     */
    private String mainImage;

    /**
     * 商品状态（0-下架，1-上架）
     */
    private Integer status;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    private Integer deleted;

    /**
     * 商家ID
     */
    private String sellerId;

    /**
     * 创建时间（时间戳，用于排序）
     */
    private Long createTime;

    /**
     * 更新时间（时间戳）
     */
    private Long updateTime;

    /**
     * 获取索引名称
     */
    public static String getIndexName() {
        return "product_index";
    }

    /**
     * 获取索引映射JSON
     * 定义字段类型和分词器
     */
    public static String getIndexMapping() {
        return "{\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"name\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"standard\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"type\": \"keyword\",\n" +
                "          \"ignore_above\": 256\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"brand\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"standard\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"type\": \"keyword\",\n" +
                "          \"ignore_above\": 256\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"description\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"standard\"\n" +
                "    },\n" +
                "    \"categoryId\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"categoryName\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"price\": {\n" +
                "      \"type\": \"long\"\n" +
                "    },\n" +
                "    \"originalPrice\": {\n" +
                "      \"type\": \"long\"\n" +
                "    },\n" +
                "    \"soldCount\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"rating\": {\n" +
                "      \"type\": \"double\"\n" +
                "    },\n" +
                "    \"ratingCount\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"mainImage\": {\n" +
                "      \"type\": \"keyword\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"status\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"deleted\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"sellerId\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"createTime\": {\n" +
                "      \"type\": \"long\"\n" +
                "    },\n" +
                "    \"updateTime\": {\n" +
                "      \"type\": \"long\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}
