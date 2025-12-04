package com.anqigou.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.order.dto.AddressInfoDTO;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "anqigou-user-service", path = "/api/user")
public interface UserServiceClient {
    
    /**
     * 获取地址详情
     */
    @GetMapping("/address/{addressId}")
    ApiResponse<AddressInfoDTO> getAddressDetail(@PathVariable("addressId") String addressId);
}
