package com.anqigou.logistics.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anqigou.logistics.config.Kuaidi100Config;

/**
 * 快递100 API客户端工具类
 */
@Component
public class Kuaidi100Client {
    
    @Autowired
    private Kuaidi100Config kuaidi100Config;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 实时查询物流轨迹
     * @param com 快递公司编码
     * @param num 快递单号
     * @return 物流轨迹信息
     */
    public JSONObject queryLogisticsTrack(String com, String num) {
        // 构建请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("com", com);
        paramMap.put("num", num);
        paramMap.put("from", "");
        paramMap.put("to", "");
        paramMap.put("resultv2", "1");
        
        String param = JSON.toJSONString(paramMap);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        
        // 生成签名
        String sign = generateSign(param, t);
        
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        // 构建请求体
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("param", param);
        requestBody.add("sign", sign);
        requestBody.add("t", t);
        requestBody.add("customer", kuaidi100Config.getCustomer());
        
        // 发送请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                kuaidi100Config.getQueryUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        
        // 解析响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return JSON.parseObject(response.getBody());
        } else {
            throw new RuntimeException("快递100 API请求失败：" + response.getStatusCode());
        }
    }
    
    /**
     * 获取地图轨迹数据
     * @param com 快递公司编码
     * @param num 快递单号
     * @return 地图轨迹信息
     */
    public JSONObject getMapTrack(String com, String num) {
        // 快递100地图轨迹接口地址
        String mapTrackUrl = "https://poll.kuaidi100.com/poll/maptrack.do";
        
        // 构建请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("com", com);
        paramMap.put("num", num);
        paramMap.put("show", "1"); // 显示地图
        
        String param = JSON.toJSONString(paramMap);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        
        // 生成签名
        String sign = generateSign(param, t);
        
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        // 构建请求体
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("param", param);
        requestBody.add("sign", sign);
        requestBody.add("t", t);
        requestBody.add("customer", kuaidi100Config.getCustomer());
        
        // 发送请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                mapTrackUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        
        // 解析响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return JSON.parseObject(response.getBody());
        } else {
            throw new RuntimeException("快递100地图轨迹API请求失败：" + response.getStatusCode());
        }
    }
    
    /**
     * 生成签名
     * 签名算法：MD5(param + t + customer + key)
     */
    private String generateSign(String param, String t) {
        String signStr = param + t + kuaidi100Config.getCustomer() + kuaidi100Config.getSecret();
        return DigestUtils.md5Hex(signStr).toUpperCase();
    }
    
    /**
     * 获取快递公司编码映射
     */
    public Map<String, String> getCompanyCodeMap() {
        if (kuaidi100Config.getCompanyCodeMap() == null) {
            return new HashMap<>();
        }
        return JSON.parseObject(kuaidi100Config.getCompanyCodeMap(), Map.class);
    }
    
    /**
     * 根据快递公司名称获取编码
     */
    public String getCompanyCode(String companyName) {
        Map<String, String> codeMap = getCompanyCodeMap();
        return codeMap.getOrDefault(companyName, companyName);
    }
}