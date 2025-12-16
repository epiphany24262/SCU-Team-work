// CoachDao.java
package com.example.demo.dao;

import com.example.demo.entity.Coach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CoachDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 查询所有教练
    public List<Coach> findAll() {
        String sql = "SELECT * FROM coaches ORDER BY id DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Coach.class));
    }

    // 分页查询教练
    public List<Coach> findByPage(int page, int pageSize, String keyword) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM coaches WHERE name LIKE ? OR specialty LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";
        String likeKeyword = "%" + keyword + "%";
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Coach.class),
                likeKeyword, likeKeyword, pageSize, offset);
    }

    // 统计总数
    public int count(String keyword) {
        String sql = "SELECT COUNT(*) FROM coaches WHERE name LIKE ? OR specialty LIKE ?";
        String likeKeyword = "%" + keyword + "%";
        return jdbcTemplate.queryForObject(sql, Integer.class, likeKeyword, likeKeyword);
    }

    // 根据ID查询教练
    public Coach findById(Integer id) {
        String sql = "SELECT * FROM coaches WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Coach.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 添加教练
    public int insert(Coach coach) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("coaches")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", coach.getName());
        parameters.put("gender", coach.getGender());
        parameters.put("age", coach.getAge());
        parameters.put("specialty", coach.getSpecialty());
        parameters.put("phone", coach.getPhone());
        parameters.put("created_at", coach.getCreatedAt());
        parameters.put("updated_at", coach.getUpdatedAt());

        Number key = jdbcInsert.executeAndReturnKey(parameters);
        return key.intValue();
    }

    // 更新教练信息
    public int update(Coach coach) {
        String sql = "UPDATE coaches SET name = :name, gender = :gender, age = :age, " +
                "specialty = :specialty, phone = :phone, updated_at = :updatedAt " +
                "WHERE id = :id";

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(coach);
        return namedParameterJdbcTemplate.update(sql, paramSource);
    }

    // 删除教练
    public int delete(Integer id) {
        String sql = "DELETE FROM coaches WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 检查手机号是否已存在
    public boolean existsByPhone(String phone, Integer excludeId) {
        if (excludeId != null) {
            String sql = "SELECT COUNT(*) FROM coaches WHERE phone = ? AND id != ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phone, excludeId);
            return count != null && count > 0;
        } else {
            String sql = "SELECT COUNT(*) FROM coaches WHERE phone = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phone);
            return count != null && count > 0;
        }
    }
}