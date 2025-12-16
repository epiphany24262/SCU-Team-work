package com.example.demo.dao;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRealName(rs.getString("real_name"));
            user.setPhone(rs.getString("phone"));
            user.setBirthDate(rs.getDate("birth_date"));
            user.setCardTime(rs.getDate("card_time"));
            user.setExpireTime(rs.getDate("expire_time"));
            user.setRole(rs.getString("role"));
            user.setStatus(rs.getInt("status"));
            user.setCreateTime(rs.getTimestamp("create_time"));
            user.setUpdateTime(rs.getTimestamp("update_time"));
            user.setGender(rs.getString("gender"));
            user.setAge(rs.getInt("age"));
            return user;
        }
    }

    // 根据用户名查找用户
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
        } catch (Exception e) {
            return null;
        }
    }

    // 获取所有用户
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // 根据ID查询用户
    public User findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 搜索用户
    public List<User> findByUsernameAndPhone(String username, String phone) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE status = 1");

        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND username LIKE ?");
        }
        if (phone != null && !phone.trim().isEmpty()) {
            sql.append(" AND phone LIKE ?");
        }

        if (username != null && !username.trim().isEmpty() && phone != null && !phone.trim().isEmpty()) {
            return jdbcTemplate.query(sql.toString(), new UserRowMapper(),
                    "%" + username + "%", "%" + phone + "%");
        } else if (username != null && !username.trim().isEmpty()) {
            return jdbcTemplate.query(sql.toString(), new UserRowMapper(), "%" + username + "%");
        } else if (phone != null && !phone.trim().isEmpty()) {
            return jdbcTemplate.query(sql.toString(), new UserRowMapper(), "%" + phone + "%");
        } else {
            return jdbcTemplate.query(sql.toString(), new UserRowMapper());
        }
    }

    // 新增用户
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password, real_name, phone, birth_date, " +
                "card_time, expire_time, role, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getPhone(),
                user.getBirthDate(),
                user.getCardTime(),
                user.getExpireTime(),
                user.getRole(),
                user.getStatus() != null ? user.getStatus() : 1
        );
    }

    // 更新用户
    public int update(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            // 不更新密码
            String sql = "UPDATE users SET username = ?, real_name = ?, phone = ?, " +
                    "birth_date = ?, card_time = ?, expire_time = ?, role = ?, status = ? WHERE id = ?";
            return jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getRealName(),
                    user.getPhone(),
                    user.getBirthDate(),
                    user.getCardTime(),
                    user.getExpireTime(),
                    user.getRole(),
                    user.getStatus(),
                    user.getId()
            );
        } else {
            // 更新密码
            String sql = "UPDATE users SET username = ?, password = ?, real_name = ?, phone = ?, " +
                    "birth_date = ?, card_time = ?, expire_time = ?, role = ?, status = ? WHERE id = ?";
            return jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getPassword(),
                    user.getRealName(),
                    user.getPhone(),
                    user.getBirthDate(),
                    user.getCardTime(),
                    user.getExpireTime(),
                    user.getRole(),
                    user.getStatus(),
                    user.getId()
            );
        }
    }

    // 删除用户
    public int delete(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 检查用户名是否存在
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    // 统计用户总数
    public Long count() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // 根据角色统计
    public Long countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, role);
    }

    // 检查用户是否存在
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}