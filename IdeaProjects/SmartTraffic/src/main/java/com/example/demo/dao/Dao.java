package com.example.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class Dao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getHourlyTraffic(String date) {
        String sql = "SELECT HOUR(gps_time) as hour, COUNT(DISTINCT device_id) as count " +
                "FROM traffic_history WHERE DATE(gps_time) = ? GROUP BY HOUR(gps_time) ORDER BY hour";
        return jdbcTemplate.queryForList(sql, date);
    }

    public Integer getTotalVehicles(String date) {
        String sql = "SELECT COUNT(DISTINCT device_id) FROM traffic_history WHERE DATE(gps_time) = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, date);
    }

    public Map<String, Object> getOverviewStats(String date) {
        String sql = "SELECT COUNT(DISTINCT device_id) as total_vehicles, COUNT(*) as total_records, " +
                "COUNT(CASE WHEN alarm_type > 0 THEN 1 END) as alarm_count, AVG(speed) as avg_speed, " +
                "MAX(speed) as max_speed FROM traffic_history WHERE DATE(gps_time) = ?";
        return jdbcTemplate.queryForMap(sql, date);
    }
}