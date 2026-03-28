package com.example.demo.dao;

import com.example.demo.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SysRoleDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<SysRole> listRoles(String roleName, String roleCode, int offset, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, role_name, role_code, description, status, create_time FROM sys_role WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (roleName != null && !roleName.trim().isEmpty()) {
            sql.append(" AND role_name LIKE ?");
            params.add("%" + roleName.trim() + "%");
        }
        if (roleCode != null && !roleCode.trim().isEmpty()) {
            sql.append(" AND role_code LIKE ?");
            params.add("%" + roleCode.trim() + "%");
        }
        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql.toString(), params.toArray(), new SysRoleRowMapper());
    }

    public int countRoles(String roleName, String roleCode) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM sys_role WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (roleName != null && !roleName.trim().isEmpty()) {
            sql.append(" AND role_name LIKE ?");
            params.add("%" + roleName.trim() + "%");
        }
        if (roleCode != null && !roleCode.trim().isEmpty()) {
            sql.append(" AND role_code LIKE ?");
            params.add("%" + roleCode.trim() + "%");
        }
        Integer count = jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
        return count == null ? 0 : count;
    }

    public int insertRole(SysRole role) {
        String sql = "INSERT INTO sys_role (role_name, role_code, description, status, create_time) VALUES (?, ?, ?, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql,
                role.getRoleName(),
                role.getRoleCode(),
                role.getDescription(),
                role.getStatus(),
                now
        );
    }

    public int updateRole(SysRole role) {
        String sql = "UPDATE sys_role SET role_name = ?, description = ? WHERE id = ?";
        return jdbcTemplate.update(sql, role.getRoleName(), role.getDescription(), role.getId());
    }

    public int deleteRole(Integer id) {
        String sql = "DELETE FROM sys_role WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private static class SysRoleRowMapper implements RowMapper<SysRole> {
        @Override
        public SysRole mapRow(ResultSet rs, int rowNum) throws SQLException {
            SysRole role = new SysRole();
            role.setId(rs.getInt("id"));
            role.setRoleName(rs.getString("role_name"));
            role.setRoleCode(rs.getString("role_code"));
            role.setDescription(rs.getString("description"));
            role.setStatus(rs.getObject("status") == null ? null : rs.getInt("status"));
            role.setCreateTime(rs.getTimestamp("create_time"));
            return role;
        }
    }
}
