package com.anqigou.user.service;

import java.util.List;

import com.anqigou.user.entity.UserDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户设备服务
 */
public interface UserDeviceService extends IService<UserDevice> {

    /**
     * 记录用户登录设备
     *
     * @param userId      用户ID
     * @param deviceName  设备名称
     * @param deviceType  设备类型
     * @param deviceToken 设备令牌
     * @param loginIp     登录IP
     * @return 设备记录
     */
    UserDevice recordLoginDevice(String userId, String deviceName, String deviceType, String deviceToken, String loginIp);

    /**
     * 获取用户的所有设备
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    List<UserDevice> listUserDevices(String userId);

    /**
     * 下线指定设备
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 是否下线成功
     */
    boolean offlineDevice(String userId, String deviceId);

    /**
     * 下线除当前设备外的所有设备
     *
     * @param userId       用户ID
     * @param currentDeviceToken 当前设备令牌
     * @return 下线成功数量
     */
    int offlineOtherDevices(String userId, String currentDeviceToken);

    /**
     * 更新设备活跃时间
     *
     * @param deviceToken 设备令牌
     * @return 是否更新成功
     */
    boolean updateDeviceActiveTime(String deviceToken);
}
