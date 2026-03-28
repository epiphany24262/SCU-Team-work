package com.example.demo.service;

import com.example.demo.dao.GPSRealTimeDAO;
import com.example.demo.entity.GPSRealTime;
import com.example.demo.utils.BCXCookieManager;  // 导入Cookie管理器
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BCXGPSService {

    @Autowired
    private GPSRealTimeDAO gpsRealTimeDAO;

    @Autowired
    private RestTemplate restTemplate;  // 需要这个

    @Autowired
    private BCXCookieManager bcxCookieManager;  // 注入Cookie管理器

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.US);

    /**
     * 获取GPS数据（同时采集和返回）
     */
    public Map<String, Object> collectAndSaveData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 2. 使用完全模拟浏览器的请求
            List<GPSRealTime> dataList = fetchDataFromBCX();

            // 保存到数据库
            if (!dataList.isEmpty()) {
                for (GPSRealTime data : dataList) {
                    gpsRealTimeDAO.updateOrInsert(data);
                    gpsRealTimeDAO.InsertGpsHistory(data);
                }
                System.out.println("成功保存 " + dataList.size() + " 条GPS数据");
            }

            // 返回前端
            Map<String, Object> formattedData = formatResponseData(dataList);
            response.put("success", true);
            response.put("data", formattedData);
            response.put("count", dataList.size());
            return response;

        } catch (Exception e) {
            System.err.println("数据采集失败: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * 从伴车星获取数据
     */
    public List<GPSRealTime> fetchDataFromBCX() throws Exception {
        try {
            // 1. 获取完整的cookie
            String Cookie = bcxCookieManager.getFullCookie();

            // 2. 生成时间参数（和浏览器完全一样）
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.US);
            String timeStr = sdf.format(new Date());
            String encodedTime = URLEncoder.encode(timeStr, "UTF-8");

            // 3. 构建URL（注意双&&）
            String url = "http://www.bcxgps.com/page/vip/showTerminalG.action?" +
                    "vip_id=0&currentPage=1&type=1&pageCount=20&search=&startType=0&line=on&&time=" +
                    encodedTime;

            System.out.println("请求URL: " + url);

            // 4. 构建请求头（完全按照cURL）
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", Cookie);  // 这个必须有
            headers.set("User-Agent", "Mozilla/5.0");  // 这个也要有
            headers.set("X-Requested-With", "XMLHttpRequest");  // AJAX请求

            // 5. POST请求，空body
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                throw new Exception("返回数据为空");
            }

            // 6. 打印响应长度
            System.out.println("响应长度: " + responseBody.length());

            return parseResponseData(responseBody);

        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
            throw new Exception("获取数据失败: " + e.getMessage());
        }
    }
    /**
     * 解析响应数据
     */
    private List<GPSRealTime> parseResponseData(String responseBody) throws Exception {
        List<GPSRealTime> dataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

            // 根据实际JSON结构解析 - key是"list"不是"data"
            if (responseMap.containsKey("list") && responseMap.get("list") instanceof List) {
                List<Map<String, Object>> dataItems = (List<Map<String, Object>>) responseMap.get("list");
                System.out.println("找到 " + dataItems.size() + " 条数据");

                for (Map<String, Object> item : dataItems) {
                    try {
                        GPSRealTime gpsData = parseDataToEntity(item);
                        dataList.add(gpsData);
                    } catch (Exception e) {
                        System.err.println("解析单条数据失败: " + e.getMessage());
                    }
                }
            } else {
                System.err.println("JSON格式不符，可用keys: " + responseMap.keySet());
            }
        } catch (Exception e) {
            System.err.println("JSON解析错误: " + e.getMessage());
            throw e;
        }

        return dataList;
    }

    /**
     * 解析HTML数据（备用方法）
     */
    private List<GPSRealTime> parseHTMLData(String html) {
        List<GPSRealTime> dataList = new ArrayList<>();
        System.out.println("收到HTML响应，长度: " + html.length());
        // 这里需要根据实际的HTML结构编写解析逻辑
        // 暂时返回空列表，需要根据实际情况实现
        return dataList;
    }

    /**
     * 将数据转换为实体
     */
    private GPSRealTime parseDataToEntity(Map<String, Object> data) {
        GPSRealTime entity = new GPSRealTime();

        try {
            // 根据实际JSON字段名设置
            entity.setSn(getLongValue(data.get("sn")));
            entity.setLon(getDoubleValue(data.get("lon")));
            entity.setLat(getDoubleValue(data.get("lat")));
            entity.setDir(getIntValue(data.get("dir")));
            entity.setSpe(getIntValue(data.get("spe"))); // 注意字段名是"spe"不是"speed"
            entity.setTname(getStringValue(data.get("tname")));
            entity.setChepai(getStringValue(data.get("chepai")));
            entity.setAddress(getStringValue(data.get("address")));

            // 设置时间字段
            String gpstimeStr = getStringValue(data.get("gpstime"));
            if (gpstimeStr != null && !gpstimeStr.isEmpty()) {
                try {
                    // 解析时间格式 "25/12/07 17:15:10"
                    SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    entity.setGpstime(sdf.parse(gpstimeStr));
                } catch (Exception e) {
                    entity.setGpstime(new Date());
                }
            } else {
                entity.setGpstime(new Date());
            }

            entity.setServerTime(new Date());

            // 其他字段 - 根据实际JSON字段名
            entity.setCarId(getIntValue(data.get("car_id")));
            entity.setStat(getIntValue(data.get("stat")));
            entity.setStart(getStringValue(data.get("start")));

            // 新增其他字段
            entity.setGsm(getIntValue(data.get("gsm")));
            entity.setGps(getIntValue(data.get("gps")));
            entity.setGpsend(getIntValue(data.get("gpsend")));
            entity.setCell(getStringValue(data.get("cell")));
            entity.setAlonlat(getStringValue(data.get("alonlat")));
            entity.setOlonlat(getStringValue(data.get("olonlat")));
            entity.setVol(getIntValue(data.get("vol")));
            entity.setOil(getIntValue(data.get("oil")));
            entity.setBat(getIntValue(data.get("bat")));
            entity.setMaxo(getIntValue(data.get("maxo")));
            entity.setRanges(getIntValue(data.get("ranges")));
            entity.setOldlon(getDoubleValue(data.get("oldlon")));
            entity.setOldlat(getDoubleValue(data.get("oldlat")));
            entity.setModel(getStringValue(data.get("model")));
            entity.setVersions(getStringValue(data.get("versions")));
            entity.setCarImg(getStringValue(data.get("carImg")));
            entity.setCtrl(getIntValue(data.get("ctrl")));
            entity.setPort(getIntValue(data.get("port")));
            entity.setSimstate(getIntValue(data.get("simstate")));
            entity.setVipId(getIntValue(data.get("vip_id")));
            entity.setTId(getIntValue(data.get("t_id")));
            entity.setComm(getStringValue(data.get("comm")));

            // 处理serverTime字段（可能是延迟时间）
            Integer serverTimeValue = getIntValue(data.get("serverTime"));
            if (serverTimeValue != null) {
                entity.setServerDelay(serverTimeValue);
            }

            // 处理startXF字段
            String startXF = getStringValue(data.get("startXF"));
            if (startXF != null && !startXF.isEmpty()) {
                entity.setStartXf(startXF);
            }

        } catch (Exception e) {
            System.err.println("解析数据到实体失败: " + e.getMessage());
        }

        return entity;
    }

    /**
     * 格式化响应数据给前端
     */
    public Map<String, Object> formatResponseData(List<GPSRealTime> dataList) {
        Map<String, Object> result = new HashMap<>();

        // 格式化车辆数据，匹配前端需要的格式
        List<Map<String, Object>> vehicles = new ArrayList<>();

        for (GPSRealTime data : dataList) {
            Map<String, Object> vehicle = new HashMap<>();
            vehicle.put("licensePlate", data.getChepai());
            vehicle.put("tname", data.getTname());
            vehicle.put("speed", data.getSpe());
            vehicle.put("lat", data.getLat());
            vehicle.put("lng", data.getLon());
            vehicle.put("address", data.getAddress());
            vehicle.put("dir", data.getDir());
            vehicle.put("gpsTime", data.getGpstime());
            vehicle.put("sn", data.getSn());
            vehicle.put("status", getStatusFromData(data));

            vehicles.add(vehicle);
        }

        result.put("vehicles", vehicles);
        result.put("total", vehicles.size());
        result.put("timestamp", new Date());

        return result;
    }

    /**
     * 根据数据判断状态
     */
    private String getStatusFromData(GPSRealTime data) {
        if (data.getStart() != null && data.getStart().contains("离线")) {
            return "offline";
        } else if (data.getSpe() != null && data.getSpe() > 0) {
            return "moving";
        } else {
            return "stopped";
        }
    }

    // 工具方法
    private Long getLongValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDoubleValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String getStringValue(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}