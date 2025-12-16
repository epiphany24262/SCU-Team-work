package com.example.demo.dao;

import com.example.demo.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 根据角色获取菜单列表
    public List<Menu> getMenusByRole(String role) {
        String sql = "SELECT m.* FROM menu m " +
                "INNER JOIN role_permission rp ON m.id = rp.menu_id " +
                "WHERE rp.role = ? AND m.status = 1 " +
                "ORDER BY m.sort ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Menu.class), role);
    }

    // 获取所有菜单
    public List<Menu> getAllMenus() {
        String sql = "SELECT * FROM menu WHERE status = 1 ORDER BY sort ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Menu.class));
    }

    // 根据ID获取菜单
    public Menu getMenuById(Integer id) {
        String sql = "SELECT * FROM menu WHERE id = ? AND status = 1";
        List<Menu> menus = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Menu.class), id);
        return menus.isEmpty() ? null : menus.get(0);
    }
}