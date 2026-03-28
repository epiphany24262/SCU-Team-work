package com.example.demo.dao;

import com.example.demo.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SysUserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SELECT_BY_USERNAME =
            "SELECT u.id, u.username, u.password, u.real_name, u.phone, u.email, u.avatar, " +
            "u.status, u.create_time, u.update_time, u.role_id, r.role_code, r.role_name " +
            "FROM sys_user u LEFT JOIN sys_role r ON u.role_id = r.id WHERE u.username = ? LIMIT 1";

    private static final String SELECT_BY_USERNAME_AND_PASSWORD =
            "SELECT u.id, u.username, u.password, u.real_name, u.phone, u.email, u.avatar, " +
            "u.status, u.create_time, u.update_time, u.role_id, r.role_code, r.role_name " +
            "FROM sys_user u LEFT JOIN sys_role r ON u.role_id = r.id " +
            "WHERE u.username = ? AND u.password = ? LIMIT 1";

    private static final String INSERT_USER =
            "INSERT INTO sys_user (username, password, real_name, phone, email, avatar, status, create_time, update_time, role_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        private static final String UPDATE_PROFILE_BY_USERNAME =
            "UPDATE sys_user SET real_name = ?, phone = ?, email = ?, update_time = ? WHERE username = ?";

        private static final String UPDATE_USER_CORE =
            "UPDATE sys_user SET role_id = ?, phone = ?, status = ?, update_time = ? WHERE id = ?";

        private static final String UPDATE_USER_STATUS =
            "UPDATE sys_user SET status = ?, update_time = ? WHERE id = ?";

        private static final String UPDATE_USER_PASSWORD =
            "UPDATE sys_user SET password = ?, update_time = ? WHERE id = ?";

        private static final String DELETE_USER =
            "DELETE FROM sys_user WHERE id = ?";

    public SysUser findByUsername(String username) {
        List<SysUser> list = jdbcTemplate.query(SELECT_BY_USERNAME, new Object[]{username}, new SysUserRowMapper());
        return list.isEmpty() ? null : list.get(0);
    }

    public SysUser findById(Long id) {
        String sql = "SELECT u.id, u.username, u.password, u.real_name, u.phone, u.email, u.avatar, " +
                "u.status, u.create_time, u.update_time, u.role_id, r.role_code, r.role_name " +
                "FROM sys_user u LEFT JOIN sys_role r ON u.role_id = r.id WHERE u.id = ? LIMIT 1";
        List<SysUser> list = jdbcTemplate.query(sql, new Object[]{id}, new SysUserRowMapper());
        return list.isEmpty() ? null : list.get(0);
    }

    public SysUser findByUsernameAndPassword(String username, String password) {
        List<SysUser> list = jdbcTemplate.query(SELECT_BY_USERNAME_AND_PASSWORD, new Object[]{username, password}, new SysUserRowMapper());
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(SysUser user) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(
                INSERT_USER,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                user.getStatus(),
                now,
                now,
                user.getRoleId()
        );
    }

    public int updateProfileByUsername(String username, String realName, String phone, String email) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(
                UPDATE_PROFILE_BY_USERNAME,
                realName,
                phone,
                email,
                now,
                username
        );
    }

    public List<SysUser> listUsers(String username, String role, String status, int offset, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT u.id, u.username, u.password, u.real_name, u.phone, u.email, u.avatar, " +
                        "u.status, u.create_time, u.update_time, u.role_id, r.role_code, r.role_name " +
                        "FROM sys_user u LEFT JOIN sys_role r ON u.role_id = r.id WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();
        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND u.username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (role != null && !role.trim().isEmpty()) {
            if ("manager".equalsIgnoreCase(role)) {
                sql.append(" AND (r.role_code IN ('SUPER_ADMIN','ADMIN'))");
            } else if ("user".equalsIgnoreCase(role)) {
                sql.append(" AND (r.role_code = 'OPERATOR' OR r.role_code IS NULL)");
            }
        }
        if (status != null && !status.trim().isEmpty()) {
            if ("enabled".equalsIgnoreCase(status)) {
                sql.append(" AND (u.status = 1 OR u.status IS NULL)");
            } else if ("disabled".equalsIgnoreCase(status)) {
                sql.append(" AND u.status = 0");
            }
        }
        sql.append(" ORDER BY u.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql.toString(), params.toArray(), new SysUserRowMapper());
    }

    public int countUsers(String username, String role, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM sys_user u LEFT JOIN sys_role r ON u.role_id = r.id WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();
        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND u.username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (role != null && !role.trim().isEmpty()) {
            if ("manager".equalsIgnoreCase(role)) {
                sql.append(" AND (r.role_code IN ('SUPER_ADMIN','ADMIN'))");
            } else if ("user".equalsIgnoreCase(role)) {
                sql.append(" AND (r.role_code = 'OPERATOR' OR r.role_code IS NULL)");
            }
        }
        if (status != null && !status.trim().isEmpty()) {
            if ("enabled".equalsIgnoreCase(status)) {
                sql.append(" AND (u.status = 1 OR u.status IS NULL)");
            } else if ("disabled".equalsIgnoreCase(status)) {
                sql.append(" AND u.status = 0");
            }
        }
        Integer count = jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
        return count == null ? 0 : count;
    }

    public int updateUserCore(Long id, Integer roleId, String phone, Integer status) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(UPDATE_USER_CORE, roleId, phone, status, now, id);
    }

    public int updateUserStatus(Long id, Integer status) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(UPDATE_USER_STATUS, status, now, id);
    }

    public int updateUserPassword(Long id, String password) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(UPDATE_USER_PASSWORD, password, now, id);
    }

    public int deleteUser(Long id) {
        return jdbcTemplate.update(DELETE_USER, id);
    }

    private static class SysUserRowMapper implements RowMapper<SysUser> {
        @Override
        public SysUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            SysUser user = new SysUser();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRealName(rs.getString("real_name"));
            user.setPhone(rs.getString("phone"));
            user.setEmail(rs.getString("email"));
            user.setAvatar(rs.getString("avatar"));
            Integer status = rs.getObject("status") == null ? null : rs.getInt("status");
            user.setStatus(status);
            user.setCreateTime(rs.getTimestamp("create_time"));
            user.setUpdateTime(rs.getTimestamp("update_time"));
            Integer roleId = rs.getObject("role_id") == null ? null : rs.getInt("role_id");
            user.setRoleId(roleId);
            user.setRoleCode(rs.getString("role_code"));
            user.setRoleName(rs.getString("role_name"));
            return user;
        }
    }
}
