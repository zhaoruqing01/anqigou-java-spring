package com.anqigou.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch 工具类
 * 用于商品搜索、模糊匹配等功能
 */
@Component
@Slf4j
public class ElasticsearchUtil {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 创建索引
     */
    public boolean createIndex(String indexName, String mappingJson) {
        try {
            if (indexExists(indexName)) {
                log.warn("索引已存在: {}", indexName);
                return true;
            }

            CreateIndexRequest request = new CreateIndexRequest(indexName);
            if (mappingJson != null && !mappingJson.isEmpty()) {
                request.mapping(mappingJson, XContentType.JSON);
            }

            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            log.info("创建索引成功: {}, acknowledged={}", indexName, response.isAcknowledged());
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("创建索引失败: {}", indexName, e);
            return false;
        }
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) {
        try {
            if (!indexExists(indexName)) {
                log.warn("索引不存在: {}", indexName);
                return true;
            }

            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            log.info("删除索引成功: {}, acknowledged={}", indexName, response.isAcknowledged());
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("删除索引失败: {}", indexName, e);
            return false;
        }
    }

    /**
     * 检查索引是否存在
     */
    public boolean indexExists(String indexName) {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("检查索引存在性失败: {}", indexName, e);
            return false;
        }
    }

    /**
     * 索引单个文档
     */
    public boolean indexDocument(String index, String id, String jsonString) {
        try {
            IndexRequest request = new IndexRequest(index)
                    .id(id)
                    .source(jsonString, XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            log.info("索引文档成功: index={}, id={}, result={}", index, id, response.getResult());
            return true;
        } catch (IOException e) {
            log.error("索引文档失败: index={}, id={}", index, id, e);
            return false;
        }
    }

    /**
     * 批量索引文档
     */
    public boolean bulkIndex(String index, Map<String, String> documents) {
        try {
            BulkRequest bulkRequest = new BulkRequest();

            for (Map.Entry<String, String> entry : documents.entrySet()) {
                IndexRequest request = new IndexRequest(index)
                        .id(entry.getKey())
                        .source(entry.getValue(), XContentType.JSON);
                bulkRequest.add(request);
            }

            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                log.error("批量索引部分失败: {}", response.buildFailureMessage());
                return false;
            }

            log.info("批量索引成功: index={}, count={}", index, documents.size());
            return true;
        } catch (IOException e) {
            log.error("批量索引失败: index={}", index, e);
            return false;
        }
    }

    /**
     * 删除文档
     */
    public boolean deleteDocument(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            log.info("删除文档成功: index={}, id={}, result={}", index, id, response.getResult());
            return true;
        } catch (IOException e) {
            log.error("删除文档失败: index={}, id={}", index, id, e);
            return false;
        }
    }

    /**
     * 搜索文档 - 支持模糊匹配
     *
     * @param index 索引名称
     * @param keyword 搜索关键词
     * @param fields 搜索字段
     * @param from 起始位置
     * @param size 返回数量
     * @return 搜索结果的JSON字符串列表
     */
    public List<Map<String, Object>> search(String index, String keyword, String[] fields, int from, int size) {
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            if (keyword != null && !keyword.isEmpty() && fields != null && fields.length > 0) {
                // 使用multi_match查询实现多字段模糊匹配
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, fields)
                        .fuzziness(Fuzziness.AUTO)  // 自动模糊匹配
                        .prefixLength(0)             // 前缀长度
                        .maxExpansions(50));         // 最大扩展数

                sourceBuilder.query(boolQuery);
            }

            sourceBuilder.from(from);
            sourceBuilder.size(size);
            searchRequest.source(sourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            List<Map<String, Object>> results = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                results.add(hit.getSourceAsMap());
            }

            log.info("搜索成功: index={}, keyword={}, hits={}", index, keyword, results.size());
            return results;
        } catch (IOException e) {
            log.error("搜索失败: index={}, keyword={}", index, keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 高级搜索 - 支持分类过滤、排序
     *
     * @param index 索引名称
     * @param keyword 搜索关键词
     * @param searchFields 搜索字段
     * @param categoryId 分类ID（可选）
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     * @param from 起始位置
     * @param size 返回数量
     * @return 搜索结果列表
     */
    public List<Map<String, Object>> advancedSearch(String index, String keyword, String[] searchFields,
                                                     String categoryId, String sortField, SortOrder sortOrder,
                                                     int from, int size) {
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 关键词搜索
            if (keyword != null && !keyword.isEmpty() && searchFields != null && searchFields.length > 0) {
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, searchFields)
                        .fuzziness(Fuzziness.AUTO)
                        .prefixLength(0)
                        .maxExpansions(50));
            }

            // 分类过滤
            if (categoryId != null && !categoryId.isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("categoryId", categoryId));
            }

            // 只查询已上架商品
            boolQuery.filter(QueryBuilders.termQuery("status", 1));
            boolQuery.filter(QueryBuilders.termQuery("deleted", 0));

            sourceBuilder.query(boolQuery);

            // 排序
            if (sortField != null && !sortField.isEmpty()) {
                sourceBuilder.sort(sortField, sortOrder != null ? sortOrder : SortOrder.DESC);
            }

            sourceBuilder.from(from);
            sourceBuilder.size(size);
            searchRequest.source(sourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            List<Map<String, Object>> results = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSourceAsMap();
                source.put("_id", hit.getId());
                source.put("_score", hit.getScore());
                results.add(source);
            }

            log.info("高级搜索成功: index={}, keyword={}, categoryId={}, hits={}",
                    index, keyword, categoryId, results.size());
            return results;
        } catch (IOException e) {
            log.error("高级搜索失败: index={}, keyword={}, categoryId={}", index, keyword, categoryId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计文档数量
     */
    public long count(String index, String keyword, String[] fields) {
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            if (keyword != null && !keyword.isEmpty() && fields != null && fields.length > 0) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, fields)
                        .fuzziness(Fuzziness.AUTO));
                sourceBuilder.query(boolQuery);
            }

            sourceBuilder.size(0);
            searchRequest.source(sourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return response.getHits().getTotalHits().value;
        } catch (IOException e) {
            log.error("统计失败: index={}, keyword={}", index, keyword, e);
            return 0;
        }
    }
}