package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

// 直接加@Service，让Spring扫描，无需单独Impl类
@Service
public class LogService {
    // 注入Spring内置的JdbcTemplate
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 分页查询日志（核心方法，无XML，纯Java写SQL）
     */
    public Map<String, Object> getLogsByPage(
            String startTime,
            String endTime,
            String operator,
            String operation,
            String status,
            Integer page,
            Integer pageSize) {
        // 1. 拼接查询条件（动态SQL，参数为null则不筛选）
        StringBuilder whereSql = new StringBuilder("WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (startTime != null && !startTime.isEmpty()) {
            whereSql.append(" AND operate_time >= ?");
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            whereSql.append(" AND operate_time <= ?");
            params.put("endTime", endTime);
        }
        if (operator != null && !operator.isEmpty()) {
            whereSql.append(" AND operator = ?");
            params.put("operator", operator);
        }
        if (operation != null && !operation.isEmpty()) {
            whereSql.append(" AND operation = ?");
            params.put("operation", operation);
        }
        if (status != null && !status.isEmpty()) {
            whereSql.append(" AND status = ?");
            params.put("status", status);
        }

        // 2. 查询总条数（计算分页用）
        String countSql = "SELECT COUNT(*) FROM system_logs " + whereSql;
        int total = jdbcTemplate.queryForObject(countSql, Integer.class, getParamsArray(params));

        // 3. 计算分页偏移量（page从1开始，数据库从0开始）
        int offset = (page - 1) * pageSize;

        // 4. 查询分页数据
        String dataSql = "SELECT id, operator, operation, content, operate_time as operateTime, " +
                "ip_address as ipAddress, status FROM system_logs " +
                whereSql + " ORDER BY operate_time DESC LIMIT ?, ?";
        // 补充分页参数
        List<Object> dataParams = getParamsList(params);
        dataParams.add(offset);
        dataParams.add(pageSize);

        // 执行查询，返回Map列表（字段名→值）
        List<Map<String, Object>> logList = jdbcTemplate.queryForList(dataSql, dataParams.toArray());

        // 5. 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", logList);
        result.put("total", total);
        return result;
    }

    /**
     * 添加日志
     */
    public void addLog(String operator, String operation, String content, String ip, String status) {
        String sql = "INSERT INTO system_logs (operator, operation, content, ip_address, status, operate_time) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql, operator, operation, content, ip, status);
    }

    /**
     * 修改日志
     */
    public boolean updateLog(Integer id, String content, String status) {
        // 先检查ID是否存在
        String checkSql = "SELECT COUNT(*) FROM system_logs WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (count == 0) {
            return false;
        }
        // 修改日志
        String updateSql = "UPDATE system_logs SET content = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, content, status, id);
        return true;
    }

    /**
     * 删除日志
     */
    public boolean deleteLog(Integer id) {
        String checkSql = "SELECT COUNT(*) FROM system_logs WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (count == 0) {
            return false;
        }
        String deleteSql = "DELETE FROM system_logs WHERE id = ?";
        jdbcTemplate.update(deleteSql, id);
        return true;
    }

    /**
     * 批量删除日志
     */
    public int deleteLogs(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String deleteSql = "DELETE FROM system_logs WHERE id IN (" + placeholders + ")";
        return jdbcTemplate.update(deleteSql, ids.toArray());
    }

    /**
     * 导出日志（不分页）
     */
    public List<Map<String, Object>> listLogs(
            String startTime,
            String endTime,
            String operator,
            String operation,
            String status) {
        StringBuilder whereSql = new StringBuilder("WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (startTime != null && !startTime.isEmpty()) {
            whereSql.append(" AND operate_time >= ?");
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            whereSql.append(" AND operate_time <= ?");
            params.put("endTime", endTime);
        }
        if (operator != null && !operator.isEmpty()) {
            whereSql.append(" AND operator = ?");
            params.put("operator", operator);
        }
        if (operation != null && !operation.isEmpty()) {
            whereSql.append(" AND operation = ?");
            params.put("operation", operation);
        }
        if (status != null && !status.isEmpty()) {
            whereSql.append(" AND status = ?");
            params.put("status", status);
        }

        String dataSql = "SELECT id, operator, operation, content, operate_time as operateTime, " +
                "ip_address as ipAddress, status FROM system_logs " +
                whereSql + " ORDER BY operate_time DESC";

        List<Object> dataParams = getParamsList(params);
        return jdbcTemplate.queryForList(dataSql, dataParams.toArray());
    }

    // 修复：适配Java 1.8，替换List.of()和Stream.toList()
    private List<Object> getParamsList(Map<String, Object> params) {
        List<Object> paramList = new ArrayList<>();
        // 逐个添加参数，替代List.of()
        paramList.add(params.get("startTime"));
        paramList.add(params.get("endTime"));
        paramList.add(params.get("operator"));
        paramList.add(params.get("operation"));
        paramList.add(params.get("status"));

        // Java 8兼容的过滤空值写法（替换Stream.toList()）
        List<Object> filteredList = new ArrayList<>();
        for (Object p : paramList) {
            if (p != null) {
                filteredList.add(p);
            }
        }
        return filteredList;
    }

    // 修复：基于适配后的getParamsList实现，替代原逻辑
    private Object[] getParamsArray(Map<String, Object> params) {
        return getParamsList(params).toArray();
    }
}