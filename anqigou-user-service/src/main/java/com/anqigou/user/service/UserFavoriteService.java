package com.anqigou.user.service;

import java.util.List;

import com.anqigou.user.dto.FavoriteItemDTO;
import com.anqigou.user.entity.UserFavorite;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户收藏服务
 */
public interface UserFavoriteService extends IService<UserFavorite> {

    /**
     * 添加收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否添加成功
     */
    boolean addFavorite(String userId, String productId);

    /**
     * 取消收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否取消成功
     */
    boolean cancelFavorite(String userId, String productId);

    /**
     * 批量取消收藏
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 取消成功数量
     */
    int batchCancelFavorite(String userId, List<String> productIds);

    /**
     * 查询用户收藏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 收藏列表
     */
    List<FavoriteItemDTO> listFavorites(String userId, int pageNum, int pageSize);

    /**
     * 检查商品是否已收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean isFavorite(String userId, String productId);
}
