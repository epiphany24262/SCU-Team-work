package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getReportData(String startTime,
                                             String endTime,
                                             String reportType,
                                             String dimension) {
        String table;
        String dateColumn;
        String valueExpr;
        String extraWhere = "";
        String desc;

        if ("carFlow".equalsIgnoreCase(reportType)) {
            table = "gps_history";
            dateColumn = "gpstime";
            valueExpr = "COUNT(*)";
            desc = "车辆流量统计";
        } else if ("violation".equalsIgnoreCase(reportType)) {
            table = "car_list";
            dateColumn = "created_at";
            valueExpr = "COUNT(*)";
            extraWhere = " AND illegal_type IS NOT NULL AND illegal_type <> ''";
            desc = "违章统计";
        } else if ("speed".equalsIgnoreCase(reportType)) {
            table = "gps_history";
            dateColumn = "gpstime";
            valueExpr = "ROUND(AVG(spe), 2)";
            desc = "平均车速";
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }

        String bucketExpr = buildBucketExpr(dateColumn, dimension);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(bucketExpr).append(" AS bucket, ")
                .append(valueExpr).append(" AS value ")
                .append("FROM ").append(table).append(" WHERE 1=1");

        List<Object> params = new ArrayList<>();
        if (startTime != null && !startTime.isEmpty()) {
            sql.append(" AND ").append(dateColumn).append(" >= ?");
            params.add(startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            sql.append(" AND ").append(dateColumn).append(" <= ?");
            params.add(endTime);
        }
        if (!extraWhere.isEmpty()) {
            sql.append(extraWhere);
        }
        sql.append(" GROUP BY bucket ORDER BY bucket");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row.get("bucket"));
            item.put("value", row.get("value"));
            item.put("desc", desc);
            list.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return result;
    }

    public List<Map<String, Object>> getReportList(String startTime,
                                                   String endTime,
                                                   String reportType,
                                                   String dimension) {
        Map<String, Object> data = getReportData(startTime, endTime, reportType, dimension);
        Object list = data.get("list");
        if (list instanceof List) {
            return (List<Map<String, Object>>) list;
        }
        return new ArrayList<>();
    }

    private String buildBucketExpr(String dateColumn, String dimension) {
        if ("week".equalsIgnoreCase(dimension)) {
            return "CONCAT(YEAR(" + dateColumn + "), '-W', LPAD(WEEK(" + dateColumn + ", 1), 2, '0'))";
        }
        if ("month".equalsIgnoreCase(dimension)) {
            return "DATE_FORMAT(" + dateColumn + ", '%Y-%m')";
        }
        if ("year".equalsIgnoreCase(dimension)) {
            return "DATE_FORMAT(" + dateColumn + ", '%Y')";
        }
        return "DATE_FORMAT(" + dateColumn + ", '%Y-%m-%d')";
    }
}
