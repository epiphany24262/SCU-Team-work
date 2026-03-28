package com.example.demo.service;

import com.example.demo.dao.GPSRealTimeDAO;
import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.TrackPoint;
import com.example.demo.utils.BCXCookieManager;  // 导入Cookie管理器
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MapHistoryService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GPSRealTimeDAO gpsRealTimeDAO;
    @Autowired
    private BCXCookieManager bcxCookieManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BCX_BASE_URL = "http://www.bcxgps.com";

    /**
     * 获取设备列表
     */
    public List<DeviceInfo> getDeviceList() {
        try {
            return gpsRealTimeDAO.getDeviceList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 获取历史轨迹数据
     * @param sn 设备SN
     * @param date 日期，格式：yyyy-MM-dd
     */
    public List<TrackPoint> getHistoryTrack(String sn, String date) {
        List<TrackPoint> trackPoints = new ArrayList<>();

        try {
            // 1. 获取完整的cookie
            String cookie = bcxCookieManager.getFullCookie();

            // 2. 格式化日期
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date parsedDate = inputFormat.parse(date);
            String formattedDate = outputFormat.format(parsedDate);

            // 3. 构建URL
            String url = UriComponentsBuilder.fromHttpUrl(BCX_BASE_URL + "/page/his/loadHisData.action")
                    .queryParam("sn", sn)
                    .queryParam("timestart", formattedDate + " 00:00:00")
                    .queryParam("timeend", formattedDate + " 23:59:59")
                    .queryParam("pl", 100)
                    .queryParam("maptype", "google")
                    .build()
                    .toUriString();

            System.out.println("调用伴车星轨迹API: " + url);

            // 4. 简化请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);  // 使用Cookie管理器获取的cookie
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("X-Requested-With", "XMLHttpRequest");
            headers.set("Referer", "http://www.bcxgps.com/map/GMap.jsp");

            // 5. 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 6. 发送GET请求
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String response = responseEntity.getBody();
            System.out.println("API响应状态: " + responseEntity.getStatusCode());
            System.out.println("API响应长度: " + (response != null ? response.length() : 0));

            // 7. 解析JSON响应
            if (response != null && !response.trim().isEmpty()) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode listNode = jsonNode.get("list");

                if (listNode != null && listNode.isArray()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    for (JsonNode pointNode : listNode) {
                        try {
                            // 解析轨迹点
                            String lonStr = pointNode.get("lon").asText();
                            String latStr = pointNode.get("lat").asText();
                            String speedStr = pointNode.get("spe").asText();
                            String direction = pointNode.get("dir").asText();
                            String gpstimeStr = pointNode.get("gpstime").asText();

                            // 转换数据
                            Double lng = Double.parseDouble(lonStr);
                            Double lat = Double.parseDouble(latStr);
                            Integer speed = "".equals(speedStr) ? 0 : Integer.parseInt(speedStr);
                            Date time = dateFormat.parse(gpstimeStr);

                            // 创建轨迹点对象
                            TrackPoint trackPoint = new TrackPoint(lng, lat, speed, direction, time);
                            trackPoints.add(trackPoint);

                        } catch (Exception e) {
                            System.err.println("解析轨迹点失败: " + pointNode);
                        }
                    }

                    // 按时间排序
                    trackPoints.sort(Comparator.comparing(TrackPoint::getTime));

                    System.out.println("成功解析 " + trackPoints.size() + " 个轨迹点");
                } else {
                    System.out.println("API返回的list字段为空或不是数组");
                }
            } else {
                System.out.println("API返回空响应");
            }

        } catch (Exception e) {
            System.err.println("获取轨迹数据失败: " + e.getMessage());

            // 如果cookie可能过期，尝试刷新
            if (e.getMessage().contains("cookie") || e.getMessage().contains("会话")) {
                try {
                    bcxCookieManager.refreshFullCookie();
                    System.out.println("cookie已刷新，请重试");
                } catch (Exception ex) {
                    System.err.println("刷新cookie也失败: " + ex.getMessage());
                }
            }

            throw new RuntimeException("获取轨迹数据失败: " + e.getMessage());
        }

        return trackPoints;
    }
}