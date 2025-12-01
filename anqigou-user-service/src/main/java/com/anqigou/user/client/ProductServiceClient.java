package com.anqigou.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.dto.ProductDetailDTO;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "anqigou-product-service", path = "/api/product", url = "http://localhost:8082")
public interface ProductServiceClient {
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    ApiResponse<ProductDetailDTO> getProductDetail(@PathVariable("productId") String productId, 
                                                  @RequestHeader(value = "X-User-Id", required = false) String userId);
}
