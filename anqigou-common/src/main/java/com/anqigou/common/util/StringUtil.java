package com.anqigou.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 */
public class StringUtil extends StringUtils {
    
    /**
     * 隐藏手机号中间4位
     * 如：13800138000 -> 138****8000
     */
    public static String maskPhoneNumber(String phone) {
        if (isBlank(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 生成订单号
     * 格式：年月日+随机8位数字
     */
    public static String generateOrderNo() {
        long timestamp = System.currentTimeMillis();
        String dateStr = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date(timestamp));
        int random = (int) (Math.random() * 100000000);
        return dateStr + String.format("%08d", random);
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * 验证密码强度
     * 要求：6-20位，包含字母+数字，不支持纯数字/纯字母
     */
    public static boolean isValidPassword(String password) {
        if (isBlank(password) || password.length() < 6 || password.length() > 20) {
            return false;
        }
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }
    
    /**
     * 验证昵称格式
     * 长度2-16字，支持中文、英文、数字，不支持特殊符号
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && nickname.length() >= 2 && nickname.length() <= 16 
                && nickname.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
    }
}
