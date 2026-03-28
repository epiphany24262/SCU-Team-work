package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class BCXCookieManager {

    @Value("${bcx.cookie-url}")
    private String cookieUrl;

    @Value("${bcx.cookie-randTID}")
    private String cookieRandTID;

    private final RestTemplate restTemplate;
    private String JSESSIONID = "";  // 存储JSESSIONID
    private String Cookie = "";  // 存储完整的cookie

    public BCXCookieManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            refreshFullCookie();  // 启动时获取完整cookie
        } catch (Exception e) {
            System.err.println("Cookie管理器初始化失败: " + e.getMessage());
        }
    }

    /**
     * 获取完整的Cookie字符串
     */
    public synchronized String getFullCookie() throws Exception {
        if (Cookie.isEmpty()) {
            refreshFullCookie();
        }
        return Cookie;
    }

    /**
     * 刷新完整的Cookie
     */
    public synchronized void refreshFullCookie() throws Exception {
        try {
            // 1. 获取JSESSIONID
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            headers.set("Accept-Language", "zh-CN,zh;q=0.9");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    cookieUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<String> cookieHeaders = response.getHeaders().get("Set-Cookie");
            if (cookieHeaders != null && !cookieHeaders.isEmpty()) {
                for (String cookie : cookieHeaders) {
                    if (cookie.contains("JSESSIONID")) {
                        String[] parts = cookie.split(";")[0].split("=");
                        if (parts.length >= 2) {
                            JSESSIONID = parts[1];
                            break;
                        }
                    }
                }
            }

            if (JSESSIONID.isEmpty()) {
                throw new Exception("未能从响应中获取JSESSIONID");
            }else{
                System.out.println("获取伴车星游客Cookie: " + JSESSIONID);
            }

            // 2. 组成完整的cookie
            Cookie = "JSESSIONID=" + JSESSIONID + "; " + cookieRandTID;

        } catch (Exception e) {
            System.err.println("获取Cookie失败: " + e.getMessage());
            throw new Exception("获取Cookie失败: " + e.getMessage());
        }
    }
}