package com.example.demo.dao;

import com.example.demo.entity.CarList;
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
public class CarListDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CarList> listCars(String plateNumber, String vehicleType, String illegalType, int offset, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, plate_number, plate_color, vehicle_type, confidence, illegal_type, image_path, created_at, updated_at " +
                        "FROM car_list WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            sql.append(" AND plate_number LIKE ?");
            params.add("%" + plateNumber.trim() + "%");
        }
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            sql.append(" AND vehicle_type = ?");
            params.add(vehicleType.trim());
        }
        if (illegalType != null && !illegalType.trim().isEmpty()) {
            sql.append(" AND illegal_type = ?");
            params.add(illegalType.trim());
        }
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql.toString(), params.toArray(), new CarListRowMapper());
    }

    public int countCars(String plateNumber, String vehicleType, String illegalType) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM car_list WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            sql.append(" AND plate_number LIKE ?");
            params.add("%" + plateNumber.trim() + "%");
        }
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            sql.append(" AND vehicle_type = ?");
            params.add(vehicleType.trim());
        }
        if (illegalType != null && !illegalType.trim().isEmpty()) {
            sql.append(" AND illegal_type = ?");
            params.add(illegalType.trim());
        }
        Integer count = jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
        return count == null ? 0 : count;
    }

    public int deleteCarsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = "DELETE FROM car_list WHERE id IN (" + placeholders + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    public Map<String, Object> statsCars(String plateNumber, String vehicleType, String illegalType) {
        Map<String, Object> result = new java.util.HashMap<>();
        StringBuilder where = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            where.append(" AND plate_number LIKE ?");
            params.add("%" + plateNumber.trim() + "%");
        }
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            where.append(" AND vehicle_type = ?");
            params.add(vehicleType.trim());
        }
        if (illegalType != null && !illegalType.trim().isEmpty()) {
            where.append(" AND illegal_type = ?");
            params.add(illegalType.trim());
        }

        String totalSql = "SELECT COUNT(*) FROM car_list " + where;
        Integer total = jdbcTemplate.queryForObject(totalSql, params.toArray(), Integer.class);

        String illegalSql = "SELECT COUNT(*) FROM car_list " + where + " AND illegal_type IS NOT NULL AND illegal_type <> ''";
        Integer illegalTotal = jdbcTemplate.queryForObject(illegalSql, params.toArray(), Integer.class);

        String byIllegalTypeSql = "SELECT CASE WHEN illegal_type IS NULL OR illegal_type = '' THEN 'none' ELSE illegal_type END AS name, " +
            "COUNT(*) AS value FROM car_list " + where + " GROUP BY name ORDER BY value DESC";
        List<Map<String, Object>> byIllegalType = jdbcTemplate.queryForList(byIllegalTypeSql, params.toArray());

        String byVehicleTypeSql = "SELECT CASE WHEN vehicle_type IS NULL OR vehicle_type = '' THEN 'unknown' ELSE vehicle_type END AS name, " +
            "COUNT(*) AS value FROM car_list " + where + " GROUP BY name ORDER BY value DESC";
        List<Map<String, Object>> byVehicleType = jdbcTemplate.queryForList(byVehicleTypeSql, params.toArray());

        result.put("total", total == null ? 0 : total);
        result.put("illegalTotal", illegalTotal == null ? 0 : illegalTotal);
        result.put("byIllegalType", byIllegalType);
        result.put("byVehicleType", byVehicleType);
        return result;
    }

    private static class CarListRowMapper implements RowMapper<CarList> {
        @Override
        public CarList mapRow(ResultSet rs, int rowNum) throws SQLException {
            CarList car = new CarList();
            car.setId(rs.getInt("id"));
            car.setPlateNumber(rs.getString("plate_number"));
            car.setPlateColor(rs.getString("plate_color"));
            car.setVehicleType(rs.getString("vehicle_type"));
            Object confidenceObj = rs.getObject("confidence");
            car.setConfidence(confidenceObj == null ? null : rs.getDouble("confidence"));
            car.setIllegalType(rs.getString("illegal_type"));
            car.setImagePath(rs.getString("image_path"));
            car.setCreatedAt(rs.getTimestamp("created_at"));
            car.setUpdatedAt(rs.getTimestamp("updated_at"));
            return car;
        }
    }
}
