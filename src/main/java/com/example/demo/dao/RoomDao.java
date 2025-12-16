package com.example.demo.dao;

import com.example.demo.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RoomDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 查询所有教室
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Room.class));
    }

    // 查询所有教室（分页）
    public List<Room> findAll(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM rooms WHERE name LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Room.class),
                "%" + keyword + "%", pageSize, offset);
    }

    // 查询总数
    public int count(String keyword) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE name LIKE ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, "%" + keyword + "%");
    }

    // 根据ID查询
    public Room findById(Integer id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Room.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增教室
    public int insert(Room room) {
        String sql = "INSERT INTO rooms (name, capacity, equipment, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getEquipment());
            ps.setObject(4, LocalDateTime.now());
            ps.setObject(5, LocalDateTime.now());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 更新教室
    public int update(Room room) {
        String sql = "UPDATE rooms SET name = ?, capacity = ?, equipment = ?, updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, room.getName(), room.getCapacity(),
                room.getEquipment(), LocalDateTime.now(), room.getId());
    }

    // 删除教室
    public int delete(Integer id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}