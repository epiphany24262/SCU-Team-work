package com.example.demo.controller;

import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.TrackPoint;
import com.example.demo.service.MapHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/map-history")
@CrossOrigin(origins = "*") // 允许跨域
public class MapHistoryController {

    @Autowired
    private MapHistoryService mapHistoryService;

    /**
     * 获取设备列表
     * GET /api/map-history/devices
     */
    @GetMapping("/devices")
    public ResponseEntity<Map<String, Object>> getDevices() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<DeviceInfo> devices = mapHistoryService.getDeviceList();

            response.put("success", true);
            response.put("data", devices);
            response.put("count", devices.size());
            response.put("message", "获取设备列表成功");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取设备列表失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取单日历史轨迹
     * GET /api/map-history/track/day
     * 参数: sn, date (yyyy-MM-dd)
     */
    @GetMapping("/track/day")
    public ResponseEntity<Map<String, Object>> getDayTrack(
            @RequestParam String sn,
            @RequestParam String date) {

        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("获取单日轨迹 - SN: " + sn + ", 日期: " + date);

            List<TrackPoint> trackPoints = mapHistoryService.getHistoryTrack(sn, date);

            response.put("success", true);
            response.put("data", trackPoints);
            response.put("count", trackPoints.size());
            response.put("message", "获取轨迹数据成功");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取轨迹数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

}