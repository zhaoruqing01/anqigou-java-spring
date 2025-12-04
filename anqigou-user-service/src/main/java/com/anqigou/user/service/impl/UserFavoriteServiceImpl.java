package com.anqigou.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.client.ProductServiceClient;
import com.anqigou.user.dto.FavoriteItemDTO;
import com.anqigou.user.dto.ProductDetailDTO;
import com.anqigou.user.dto.ProductSkuDTO;
import com.anqigou.user.entity.UserFavorite;
import com.anqigou.user.mapper.UserFavoriteMapper;
import com.anqigou.user.service.UserFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户收藏服务实现类
 */
@Service
@Slf4j
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    
    @Autowired
    private ProductServiceClient productServiceClient;

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
    public List<FavoriteItemDTO> listFavorites(String userId, int pageNum, int pageSize) {
        // 先获取收藏记录
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreateTime);
        // 分页查询
        int offset = (pageNum - 1) * pageSize;
        List<UserFavorite> favorites = userFavoriteMapper.selectList(wrapper.last("LIMIT " + offset + "," + pageSize));
        
        // 转换为FavoriteItemDTO并填充商品信息
        List<FavoriteItemDTO> result = new ArrayList<>();
        for (UserFavorite favorite : favorites) {
            FavoriteItemDTO item = new FavoriteItemDTO();
            item.setId(favorite.getId());
            item.setUserId(favorite.getUserId());
            item.setProductId(favorite.getProductId());
            item.setCreateTime(favorite.getCreateTime());
            
            try {
                // 通过Feign客户端获取商品详情，添加异常处理
                ApiResponse<ProductDetailDTO> productResponse = productServiceClient.getProductDetail(favorite.getProductId(), userId);
                if (productResponse.getCode() == 0 && productResponse.getData() != null) {
                    ProductDetailDTO product = productResponse.getData();
                    item.setProductName(product.getName());
                    item.setMainImage(product.getMainImage());
                    item.setPrice(product.getPrice());
                    item.setOriginalPrice(product.getOriginalPrice());
                    
                    // 计算总库存（所有SKU库存之和）
                    int totalStock = 0;
                    if (product.getSkus() != null && !product.getSkus().isEmpty()) {
                        for (ProductSkuDTO sku : product.getSkus()) {
                            if (sku.getStock() != null) {
                                totalStock += sku.getStock();
                            }
                        }
                    }
                    item.setStock(totalStock);
                    
                    item.setSoldCount(product.getSoldCount());
                    item.setRating(product.getRating());
                    item.setRatingCount(product.getRatingCount());
                }
            } catch (Exception e) {
                // 捕获Feign调用异常，防止整个请求失败
                log.error("Failed to get product detail for favorite item: {}", favorite.getProductId(), e);
                // 继续处理其他收藏项
            }
            
            result.add(item);
        }
        
        return result;
    }

    @Override
    public boolean isFavorite(String userId, String productId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId);
        return count(wrapper) > 0;
    }
}
