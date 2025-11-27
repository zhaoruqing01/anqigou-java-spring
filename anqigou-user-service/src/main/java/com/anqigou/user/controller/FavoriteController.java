package com.anqigou.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.service.UserFavoriteService;

/**
 * 用户收藏控制器
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加收藏
     *
     * @param productId 商品ID
     * @return 操作结果
     */
    @PostMapping("/add/{productId}")
    public ApiResponse<Boolean> addFavorite(@PathVariable String productId, @RequestHeader("user-id") String userId) {
        boolean result = userFavoriteService.addFavorite(userId, productId);
        return ApiResponse.success(result);
    }

    /**
     * 取消收藏
     *
     * @param productId 商品ID
     * @return 操作结果
     */
    @DeleteMapping("/cancel/{productId}")
    public ApiResponse<Boolean> cancelFavorite(@PathVariable String productId, @RequestHeader("user-id") String userId) {
        boolean result = userFavoriteService.cancelFavorite(userId, productId);
        return ApiResponse.success(result);
    }

    /**
     * 批量取消收藏
     *
     * @param productIds 商品ID列表
     * @return 取消成功数量
     */
    @DeleteMapping("/batch-cancel")
    public ApiResponse<Integer> batchCancelFavorite(@RequestBody List<String> productIds, @RequestHeader("user-id") String userId) {
        int count = userFavoriteService.batchCancelFavorite(userId, productIds);
        return ApiResponse.success(count);
    }

    /**
     * 查询收藏列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 收藏列表
     */
    @GetMapping("/list")
    public ApiResponse<List> listFavorites(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestHeader("user-id") String userId) {
        List favorites = userFavoriteService.listFavorites(userId, pageNum, pageSize);
        return ApiResponse.success(favorites);
    }

    /**
     * 检查商品是否已收藏
     *
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{productId}")
    public ApiResponse<Boolean> checkFavorite(@PathVariable String productId, @RequestHeader("user-id") String userId) {
        boolean isFavorite = userFavoriteService.isFavorite(userId, productId);
        return ApiResponse.success(isFavorite);
    }
}
