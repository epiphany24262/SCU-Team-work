package com.example.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SysRolePermissionDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS sys_role_permission (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "role_id INT NOT NULL, " +
                "perm_id INT NOT NULL, " +
                "create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
    }

    public List<Integer> listPermissionIdsByRoleId(Integer roleId) {
        String sql = "SELECT perm_id FROM sys_role_permission WHERE role_id = ?";
        return jdbcTemplate.query(sql, new Object[]{roleId}, (rs, rowNum) -> rs.getInt("perm_id"));
    }

    public void replaceRolePermissions(Integer roleId, List<Integer> permIds) {
        String deleteSql = "DELETE FROM sys_role_permission WHERE role_id = ?";
        jdbcTemplate.update(deleteSql, roleId);
        if (permIds == null || permIds.isEmpty()) {
            return;
        }
        String insertSql = "INSERT INTO sys_role_permission (role_id, perm_id, create_time) VALUES (?, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<Object[]> batch = new ArrayList<>();
        for (Integer permId : permIds) {
            if (permId != null) {
                batch.add(new Object[]{roleId, permId, now});
            }
        }
        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, batch);
        }
    }
}
