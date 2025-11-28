package com.anqigou.order.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.dto.SkuStockDTO;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "anqigou-product-service", path = "/api/product")
public interface ProductServiceClient {
    
    /**
     * 批量获取SKU库存信息
     */
    @GetMapping("/sku/batch-stock")
    ApiResponse<List<SkuStockDTO>> batchGetSkuStock(@RequestParam("skuIds") List<String> skuIds);
    
    /**
     * 扣减库存
     */
    @GetMapping("/sku/{skuId}/deduct-stock")
    ApiResponse<String> deductStock(@PathVariable("skuId") String skuId, 
                                   @RequestParam("quantity") Integer quantity);
    
    /**
     * 归还库存
     */
    @GetMapping("/sku/{skuId}/return-stock")
    ApiResponse<String> returnStock(@PathVariable("skuId") String skuId, 
                                   @RequestParam("quantity") Integer quantity);
}
