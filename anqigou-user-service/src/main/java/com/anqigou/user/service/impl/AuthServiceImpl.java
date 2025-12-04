package com.anqigou.user.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.anqigou.common.constant.AppConstants;
import com.anqigou.common.exception.BizException;
import com.anqigou.common.util.JwtUtil;
import com.anqigou.common.util.StringUtil;
import com.anqigou.user.config.WechatConfig;
import com.anqigou.user.dto.LoginRequest;
import com.anqigou.user.dto.LoginResponse;
import com.anqigou.user.dto.RegisterRequest;
import com.anqigou.user.dto.UserInfoDTO;
import com.anqigou.user.dto.VerifyCodeLoginRequest;
import com.anqigou.user.entity.User;
import com.anqigou.user.mapper.UserMapper;
import com.anqigou.user.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户认证服务实现
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private WechatConfig wechatConfig;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void sendVerifyCode(String phone) {
        if (!StringUtil.isValidPhone(phone)) {
            throw new BizException(400, "手机号格式不正确");
        }
        
        // 生成验证码（6位数字）
        String verifyCode = String.format("%06d", (int)(Math.random() * 1000000));
        
        // 存入Redis，设置过期时间15分钟
        String key = AppConstants.RedisKeyPrefix.VERIFY_CODE + phone;
        redisTemplate.opsForValue().set(key, verifyCode, 
            AppConstants.Defaults.VERIFY_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        
        log.info("发送验证码到手机号: {}, 验证码: {}", phone, verifyCode);
        // TODO: 调用短信服务发送验证码
    }
    
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 验证手机号
        if (!StringUtil.isValidPhone(request.getPhone())) {
            throw new BizException(400, "手机号格式不正确");
        }
        
        // 验证密码
        if (StringUtil.isBlank(request.getPassword())) {
            throw new BizException(400, "密码不能为空");
        }
        if (request.getPassword().length() < 6) {
            throw new BizException(400, "密码长度至少6位");
        }
        
        // 检查手机号是否已注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            throw new BizException(400, "该手机号已被注册");
        }
        
        // 创建新用户
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname() != null ? request.getNickname() : "用户" + request.getPhone().substring(7))
                .avatar("")
                .memberLevel(0)
                .totalConsumption(0L)
                .availablePoints(0L)
                .status(0)
                .lastLoginTime(LocalDateTime.now())
                .lastLoginIp("127.0.0.1")
                .deleted(0)
                .build();
        
        userMapper.insert(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId());
        
        return LoginResponse.builder()
                .userId(user.getId())
                .token(token)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .expiresIn(AppConstants.Defaults.TOKEN_EXPIRE_TIME)
                .build();
    }
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 验证手机号
        if (!StringUtil.isValidPhone(request.getPhone())) {
            throw new BizException(400, "手机号格式不正确");
        }
        
        if (StringUtil.isBlank(request.getPassword())) {
            throw new BizException(400, "密码不能为空");
        }
        
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            throw new BizException(401, "用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(401, "密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp("127.0.0.1"); // TODO: 获取真实IP
        userMapper.updateById(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId());
        
        return LoginResponse.builder()
                .userId(user.getId())
                .token(token)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .expiresIn(AppConstants.Defaults.TOKEN_EXPIRE_TIME)
                .build();
    }
    
    @Override
    @Transactional
    public LoginResponse loginWithVerifyCode(VerifyCodeLoginRequest request) {
        // 验证手机号
        if (!StringUtil.isValidPhone(request.getPhone())) {
            throw new BizException(400, "手机号格式不正确");
        }
        
        // 验证码校验
        String verifyCodeKey = AppConstants.RedisKeyPrefix.VERIFY_CODE + request.getPhone();
        String savedCode = redisTemplate.opsForValue().get(verifyCodeKey);
        if (savedCode == null || !savedCode.equals(request.getVerifyCode())) {
            throw new BizException(400, "验证码错误或已过期");
        }
        
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            // 用户不存在则自动创建
            user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .phone(request.getPhone())
                    .nickname("用户" + request.getPhone().substring(7))
                    .avatar("")
                    .memberLevel(0)
                    .totalConsumption(0L)
                    .availablePoints(0L)
                    .status(0)
                    .lastLoginTime(LocalDateTime.now())
                    .lastLoginIp("127.0.0.1")
                    .deleted(0)
                    .build();
            userMapper.insert(user);
        } else {
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp("127.0.0.1");
            userMapper.updateById(user);
        }
        
        // 删除验证码
        redisTemplate.delete(verifyCodeKey);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId());
        
        return LoginResponse.builder()
                .userId(user.getId())
                .token(token)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .expiresIn(AppConstants.Defaults.TOKEN_EXPIRE_TIME)
                .build();
    }
    
    @Override
    @Transactional
    public LoginResponse wechatLogin(String code) {
        // 完整版微信登录实现
        try {
            // 调用微信官方API获取openid和session_key
            String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    wechatConfig.getAppId(), wechatConfig.getSecret(), code);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            
            // 发送GET请求
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            
            // 解析响应结果
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String openid = rootNode.get("openid").asText();
            String sessionKey = rootNode.get("session_key").asText();
            String unionid = rootNode.has("unionid") ? rootNode.get("unionid").asText() : null;
            
            if (rootNode.has("errcode")) {
                int errCode = rootNode.get("errcode").asInt();
                if (errCode != 0) {
                    String errMsg = rootNode.get("errmsg").asText();
                    log.error("微信登录API返回错误: errcode={}, errmsg={}", errCode, errMsg);
                    throw new BizException(500, "微信登录失败: " + errMsg);
                }
            }
            
            log.info("微信登录成功，openid: {}, unionid: {}", openid, unionid);
            
            // 根据openid查询用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("wechat_open_id", openid);
            User user = userMapper.selectOne(queryWrapper);
            
            if (user == null) {
                // 用户不存在，创建新用户
                user = User.builder()
                        .id(UUID.randomUUID().toString())
                        .phone("")
                        .wechatOpenId(openid)
                        .wechatUnionid(unionid)
                        .nickname("微信用户" + openid.substring(0, 8))
                        .avatar("")
                        .memberLevel(0)
                        .totalConsumption(0L)
                        .availablePoints(0L)
                        .status(0)
                        .lastLoginTime(LocalDateTime.now())
                        .lastLoginIp("127.0.0.1")
                        .deleted(0)
                        .build();
                userMapper.insert(user);
                log.info("创建新微信用户: {}", user.getId());
            } else {
                // 更新用户登录信息
                user.setLastLoginTime(LocalDateTime.now());
                user.setLastLoginIp("127.0.0.1");
                // 如果有unionid且未设置，更新unionid
                if (StringUtil.isNotBlank(unionid) && StringUtil.isBlank(user.getWechatUnionid())) {
                    user.setWechatUnionid(unionid);
                }
                userMapper.updateById(user);
                log.info("更新微信用户登录信息: {}", user.getId());
            }
            
            // 生成token
            String token = jwtUtil.generateToken(user.getId());
            
            return LoginResponse.builder()
                    .userId(user.getId())
                    .token(token)
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .expiresIn(AppConstants.Defaults.TOKEN_EXPIRE_TIME)
                    .build();
        } catch (Exception e) {
            log.error("微信登录处理失败: {}", e.getMessage(), e);
            throw new BizException(500, "微信登录处理失败: " + e.getMessage());
        }
    }
    
    @Override
    public UserInfoDTO getUserInfo(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        
        return UserInfoDTO.builder()
                .id(user.getId())
                .phone(StringUtil.maskPhoneNumber(user.getPhone()))
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .memberLevel(user.getMemberLevel())
                .totalConsumption(user.getTotalConsumption())
                .availablePoints(user.getAvailablePoints())
                .lastLoginTime(user.getLastLoginTime() != null ? user.getLastLoginTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .personalizedRecommendation(user.getPersonalizedRecommendation())
                .locationAuthorization(user.getLocationAuthorization())
                .build();
    }
    
    @Override
    public void updateUserInfo(String userId, UserInfoDTO userInfo) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        
        if (StringUtil.isNotBlank(userInfo.getNickname())) {
            if (!StringUtil.isValidNickname(userInfo.getNickname())) {
                throw new BizException(400, "昵称长度2-16字，仅支持中文、英文、数字");
            }
            user.setNickname(userInfo.getNickname());
        }
        
        if (StringUtil.isNotBlank(userInfo.getAvatar())) {
            user.setAvatar(userInfo.getAvatar());
        }
        
        if (userInfo.getPersonalizedRecommendation() != null) {
            user.setPersonalizedRecommendation(userInfo.getPersonalizedRecommendation());
        }
        
        if (userInfo.getLocationAuthorization() != null) {
            user.setLocationAuthorization(userInfo.getLocationAuthorization());
        }
        
        userMapper.updateById(user);
    }
    
    @Override
    public String validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
