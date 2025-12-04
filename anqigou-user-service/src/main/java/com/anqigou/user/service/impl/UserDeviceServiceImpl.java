package com.anqigou.user.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anqigou.user.entity.UserDevice;
import com.anqigou.user.mapper.UserDeviceMapper;
import com.anqigou.user.service.UserDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 用户设备服务实现类
 */
@Service
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceMapper, UserDevice> implements UserDeviceService {

    @Autowired
    private UserDeviceMapper userDeviceMapper;

    @Override
    public UserDevice recordLoginDevice(String userId, String deviceName, String deviceType, String deviceToken, String loginIp) {
        // 检查设备是否已存在
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
                .eq(UserDevice::getDeviceToken, deviceToken);
        UserDevice existingDevice = getOne(wrapper);

        if (existingDevice != null) {
            // 更新现有设备信息
            existingDevice.setDeviceName(deviceName);
            existingDevice.setDeviceType(deviceType);
            existingDevice.setLoginIp(loginIp);
            existingDevice.setLoginTime(new Date());
            existingDevice.setLastActiveTime(new Date());
            existingDevice.setStatus(1); // 在线状态
            updateById(existingDevice);
            return existingDevice;
        } else {
            // 创建新设备记录
            UserDevice newDevice = new UserDevice();
            newDevice.setUserId(userId);
            newDevice.setDeviceName(deviceName);
            newDevice.setDeviceType(deviceType);
            newDevice.setDeviceToken(deviceToken);
            newDevice.setLoginIp(loginIp);
            newDevice.setLoginTime(new Date());
            newDevice.setLastActiveTime(new Date());
            newDevice.setStatus(1); // 在线状态
            save(newDevice);
            return newDevice;
        }
    }

    @Override
    public List<UserDevice> listUserDevices(String userId) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
                .orderByDesc(UserDevice::getLastActiveTime);
        return list(wrapper);
    }

    @Override
    public boolean offlineDevice(String userId, String deviceId) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
                .eq(UserDevice::getId, deviceId);
        UserDevice device = getOne(wrapper);
        if (device != null) {
            device.setStatus(0); // 下线状态
            return updateById(device);
        }
        return false;
    }

    @Override
    public int offlineOtherDevices(String userId, String currentDeviceToken) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
                .ne(UserDevice::getDeviceToken, currentDeviceToken);
        List<UserDevice> devices = list(wrapper);
        if (devices.isEmpty()) {
            return 0;
        }
        // 批量更新状态为下线
        for (UserDevice device : devices) {
            device.setStatus(0);
        }
        return updateBatchById(devices) ? devices.size() : 0;
    }

    @Override
    public boolean updateDeviceActiveTime(String deviceToken) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getDeviceToken, deviceToken);
        UserDevice device = getOne(wrapper);
        if (device != null) {
            device.setLastActiveTime(new Date());
            return updateById(device);
        }
        return false;
    }
}
