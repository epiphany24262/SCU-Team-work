package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.StatisticsService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取2023-11-06的24小时车流量统计
     */
    @PostMapping("/hourly-traffic")
    public Map<String, Object> getHourlyTraffic() {
        return statisticsService.getHourlyTrafficStats();
    }

    /**
     * 获取2023-11-06的统计概览
     */
    @PostMapping("/overview")
    public Map<String, Object> getOverview() {
        return statisticsService.getOverviewStats();
    }
}