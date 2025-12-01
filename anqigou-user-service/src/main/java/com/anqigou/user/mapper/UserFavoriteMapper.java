package com.anqigou.user.mapper;

import java.util.List;

import com.anqigou.user.dto.FavoriteItemDTO;
import com.anqigou.user.entity.UserFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户收藏Mapper
 */
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    
    /**
     * 查询用户收藏列表，关联商品信息
     *
     * @param userId   用户ID
     * @param offset   偏移量
     * @param pageSize 每页数量
     * @return 收藏列表，包含商品信息
     */
    List<FavoriteItemDTO> selectFavoriteListWithProductInfo(String userId, int offset, int pageSize);
}
