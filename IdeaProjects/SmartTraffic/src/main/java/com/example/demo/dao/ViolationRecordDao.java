package com.example.demo.dao;

import com.example.demo.entity.ViolationRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ViolationRecordDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ViolationRecord> listViolations(String plateNumber, String illegalType, String startTime, String endTime, int offset, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.plate_number, c.illegal_type, c.created_at, c.image_path, c.confidence, " +
                        "addr.address " +
                        "FROM car_list c " +
                        "LEFT JOIN (" +
                        "  SELECT g1.chepai, g1.address " +
                        "  FROM gps_history g1 " +
                        "  INNER JOIN (" +
                        "    SELECT chepai, MAX(gpstime) AS max_time FROM gps_history GROUP BY chepai" +
                        "  ) g2 ON g1.chepai = g2.chepai AND g1.gpstime = g2.max_time" +
                        ") addr ON addr.chepai = c.plate_number " +
                        "WHERE c.illegal_type IS NOT NULL AND c.illegal_type <> ''");
        List<Object> params = new ArrayList<>();
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            sql.append(" AND c.plate_number LIKE ?");
            params.add("%" + plateNumber.trim() + "%");
        }
        if (illegalType != null && !illegalType.trim().isEmpty()) {
            sql.append(" AND c.illegal_type = ?");
            params.add(illegalType.trim());
        }
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND c.created_at >= ?");
            params.add(startTime.trim());
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND c.created_at <= ?");
            params.add(endTime.trim());
        }
        sql.append(" ORDER BY c.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql.toString(), params.toArray(), new ViolationRecordRowMapper());
    }

    public int countViolations(String plateNumber, String illegalType, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM car_list c WHERE c.illegal_type IS NOT NULL AND c.illegal_type <> ''");
        List<Object> params = new ArrayList<>();
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            sql.append(" AND c.plate_number LIKE ?");
            params.add("%" + plateNumber.trim() + "%");
        }
        if (illegalType != null && !illegalType.trim().isEmpty()) {
            sql.append(" AND c.illegal_type = ?");
            params.add(illegalType.trim());
        }
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND c.created_at >= ?");
            params.add(startTime.trim());
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND c.created_at <= ?");
            params.add(endTime.trim());
        }
        Integer count = jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
        return count == null ? 0 : count;
    }

    public List<Map<String, Object>> statsViolations(String plateNumber, String illegalType, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT c.illegal_type AS name, COUNT(*) AS value FROM car_list c WHERE c.illegal_type IS NOT NULL AND c.illegal_type <> ''");
        List<Object> params = new ArrayList<>();
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            sql.append(" AND c.plate_number LIKE ?");
            params.add("%" + plateNumber.trim() + "%");
        }
        if (illegalType != null && !illegalType.trim().isEmpty()) {
            sql.append(" AND c.illegal_type = ?");
            params.add(illegalType.trim());
        }
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND c.created_at >= ?");
            params.add(startTime.trim());
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND c.created_at <= ?");
            params.add(endTime.trim());
        }
        sql.append(" GROUP BY c.illegal_type ORDER BY value DESC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public int clearViolationsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = "UPDATE car_list SET illegal_type = NULL WHERE id IN (" + placeholders + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    private static class ViolationRecordRowMapper implements RowMapper<ViolationRecord> {
        @Override
        public ViolationRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            ViolationRecord record = new ViolationRecord();
            record.setId(rs.getInt("id"));
            record.setLicensePlate(rs.getString("plate_number"));
            record.setViolationType(rs.getString("illegal_type"));
            record.setViolationTime(rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toString());
            record.setViolationAddr(rs.getString("address"));
            record.setImagePath(rs.getString("image_path"));
            Object confidenceObj = rs.getObject("confidence");
            record.setConfidence(confidenceObj == null ? null : rs.getDouble("confidence"));
            return record;
        }
    }
}
