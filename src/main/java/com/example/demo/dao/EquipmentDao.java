package com.example.demo.dao;

import com.example.demo.entity.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EquipmentDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 查询器材列表（分页）
    public List<Equipment> findAll(String keyword, String type, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM equipment WHERE 1=1 ";
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            sql += "AND (name LIKE ? OR brand LIKE ? OR model LIKE ?) ";
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        
        if (type != null && !type.isEmpty()) {
            sql += "AND type = ? ";
            params.add(type);
        }
        
        if (status != null && !status.isEmpty()) {
            sql += "AND status = ? ";
            params.add(status);
        }
        
        sql += "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add(offset);
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Equipment.class), params.toArray());
    }

    // 查询总数
    public int count(String keyword, String type, String status) {
        String sql = "SELECT COUNT(*) FROM equipment WHERE 1=1 ";
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            sql += "AND (name LIKE ? OR brand LIKE ? OR model LIKE ?) ";
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        
        if (type != null && !type.isEmpty()) {
            sql += "AND type = ? ";
            params.add(type);
        }
        
        if (status != null && !status.isEmpty()) {
            sql += "AND status = ? ";
            params.add(status);
        }
        
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    // 根据ID查询
    public Equipment findById(Integer id) {
        String sql = "SELECT * FROM equipment WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Equipment.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增器材
    public int insert(Equipment equipment) {
        String sql = "INSERT INTO equipment (name, type, brand, model, quantity, available_quantity, " +
                "location, description, specifications, maintenance_date, next_maintenance_date, " +
                "status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, equipment.getName());
            ps.setString(2, equipment.getType());
            ps.setString(3, equipment.getBrand());
            ps.setString(4, equipment.getModel());
            ps.setInt(5, equipment.getQuantity());
            ps.setInt(6, equipment.getAvailableQuantity());
            ps.setString(7, equipment.getLocation());
            ps.setString(8, equipment.getDescription());
            ps.setString(9, equipment.getSpecifications());
            ps.setObject(10, equipment.getMaintenanceDate());
            ps.setObject(11, equipment.getNextMaintenanceDate());
            ps.setString(12, equipment.getStatus());
            ps.setObject(13, LocalDateTime.now());
            ps.setObject(14, LocalDateTime.now());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 更新器材
    public int update(Equipment equipment) {
        String sql = "UPDATE equipment SET name = ?, type = ?, brand = ?, model = ?, " +
                "quantity = ?, available_quantity = ?, location = ?, description = ?, " +
                "specifications = ?, maintenance_date = ?, next_maintenance_date = ?, " +
                "status = ?, updated_at = ? WHERE id = ?";
        
        return jdbcTemplate.update(sql, 
                equipment.getName(),
                equipment.getType(),
                equipment.getBrand(),
                equipment.getModel(),
                equipment.getQuantity(),
                equipment.getAvailableQuantity(),
                equipment.getLocation(),
                equipment.getDescription(),
                equipment.getSpecifications(),
                equipment.getMaintenanceDate(),
                equipment.getNextMaintenanceDate(),
                equipment.getStatus(),
                LocalDateTime.now(),
                equipment.getId());
    }

    // 删除器材
    public int delete(Integer id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 更新器材状态
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE equipment SET status = ?, updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, LocalDateTime.now(), id);
    }

    // 更新可用数量
    public int updateAvailableQuantity(Integer id, Integer availableQuantity) {
        String sql = "UPDATE equipment SET available_quantity = ?, updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, availableQuantity, LocalDateTime.now(), id);
    }

    // 获取所有可用器材（状态为AVAILABLE且available_quantity > 0）
    public List<Equipment> findAvailableEquipments() {
        String sql = "SELECT * FROM equipment WHERE status = 'AVAILABLE' AND available_quantity > 0 ORDER BY name";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Equipment.class));
    }

    // 根据类型获取器材
    public List<Equipment> findByType(String type) {
        String sql = "SELECT * FROM equipment WHERE type = ? AND status = 'AVAILABLE' AND available_quantity > 0 ORDER BY name";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Equipment.class), type);
    }

    // 检查器材名称是否已存在
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM equipment WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }
}