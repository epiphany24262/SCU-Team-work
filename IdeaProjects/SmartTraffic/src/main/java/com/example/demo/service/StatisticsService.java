package com.example.demo.service;

import com.example.demo.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private Dao statisticsDao;

    public Map<String, Object> getHourlyTrafficStats() {
        String date = "2023-11-06";
        List<Map<String, Object>> hourlyData = statisticsDao.getHourlyTraffic(date);

        // 数据处理逻辑
        List<String> hours = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Map<Integer, Integer> hourMap = new HashMap<>();

        for (int i = 0; i < 24; i++) hourMap.put(i, 0);
        for (Map<String, Object> data : hourlyData) {
            Integer hour = ((Number) data.get("hour")).intValue();
            Integer count = ((Number) data.get("count")).intValue();
            hourMap.put(hour, count);
        }

        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d时", i));
            counts.add(hourMap.get(i));
        }

        Integer total = statisticsDao.getTotalVehicles(date);

        Map<String, Object> result = new HashMap<>();
        result.put("hours", hours);
        result.put("counts", counts);
        result.put("total", total);
        result.put("date", date);
        return result;
    }

    public Map<String, Object> getOverviewStats() {
        String date = "2023-11-06";
        Map<String, Object> stats = statisticsDao.getOverviewStats(date);
        stats.put("date", date);
        return stats;
    }
}