package com.example.demo.controller;

import com.example.demo.service.BCXGPSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/collection")
@CrossOrigin(origins = "*") // 允许跨域
public class MapController {

    @Autowired
    private BCXGPSService bcxgpsService;

    /**
     * 获取GPS数据（同时采集和返回）
     */
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getGPSData() {
        System.out.println("收到数据获取请求");
        Map<String, Object> response = bcxgpsService.collectAndSaveData();
        return ResponseEntity.ok(response);
    }
}