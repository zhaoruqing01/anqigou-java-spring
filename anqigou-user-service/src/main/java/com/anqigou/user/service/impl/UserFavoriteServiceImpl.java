package com.anqigou.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.user.entity.UserFavorite;
import com.anqigou.user.mapper.UserFavoriteMapper;
import com.anqigou.user.service.UserFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 用户收藏服务实现类
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    public boolean addFavorite(String userId, String productId) {
        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            return true;
        }
        // 创建收藏记录
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        return save(favorite);
    }

    @Override
    public boolean cancelFavorite(String userId, String productId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId);
        return remove(wrapper);
    }

    @Override
    public int batchCancelFavorite(String userId, List<String> productIds) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .in(UserFavorite::getProductId, productIds);
        return remove(wrapper) ? productIds.size() : 0;
    }

    @Override
    public List<UserFavorite> listFavorites(String userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreateTime);
        // 分页查询
        int offset = (pageNum - 1) * pageSize;
        return userFavoriteMapper.selectList(wrapper.last("LIMIT " + offset + "," + pageSize));
    }

    @Override
    public boolean isFavorite(String userId, String productId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId);
        return count(wrapper) > 0;
    }
}
