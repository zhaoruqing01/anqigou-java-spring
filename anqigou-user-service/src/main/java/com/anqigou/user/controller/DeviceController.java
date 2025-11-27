package com.anqigou.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anqigou.common.response.ApiResponse;
import com.anqigou.user.service.UserDeviceService;

/**
 * 用户设备控制器
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private UserDeviceService userDeviceService;

    /**
     * 获取用户设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    @GetMapping("/list")
    public ApiResponse listDevices(@RequestHeader("user-id") String userId) {
        return ApiResponse.success(userDeviceService.listUserDevices(userId));
    }

    /**
     * 下线指定设备
     *
     * @param deviceId 设备ID
     * @param userId   用户ID
     * @return 操作结果
     */
    @PostMapping("/offline/{deviceId}")
    public ApiResponse<Boolean> offlineDevice(@PathVariable String deviceId, @RequestHeader("user-id") String userId) {
        boolean result = userDeviceService.offlineDevice(userId, deviceId);
        return ApiResponse.success(result);
    }

    /**
     * 下线除当前设备外的所有设备
     *
     * @param currentDeviceToken 当前设备令牌
     * @param userId             用户ID
     * @return 下线成功数量
     */
    @PostMapping("/offline-others")
    public ApiResponse<Integer> offlineOtherDevices(@RequestParam String currentDeviceToken, @RequestHeader("user-id") String userId) {
        int count = userDeviceService.offlineOtherDevices(userId, currentDeviceToken);
        return ApiResponse.success(count);
    }
}
