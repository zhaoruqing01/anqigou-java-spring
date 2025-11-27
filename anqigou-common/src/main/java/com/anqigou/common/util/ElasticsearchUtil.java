package com.anqigou.common.util;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// import java.io.IOException;

/**
 * Elasticsearch 工具类
 * Elasticsearch 墟稀可放此配置，是可选的搞索桇框
 */
@Component
@Slf4j
public class ElasticsearchUtil {
    
    // Elasticsearch 配置已禁用
    
    public void indexDocument(String index, String id, String jsonString) {
        log.info("Elasticsearch is disabled");
    }
    
    public void search(String index, String keyword, int from, int size) {
        log.info("Elasticsearch is disabled");
    }
    
    public void createIndex(String indexName) {
        log.info("Elasticsearch is disabled");
    }
    
    public void deleteIndex(String indexName) {
        log.info("Elasticsearch is disabled");
    }
}