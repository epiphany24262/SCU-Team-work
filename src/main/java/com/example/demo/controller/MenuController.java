package com.example.demo.controller;

import com.example.demo.entity.Menu;
import com.example.demo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取用户菜单 - POST
     */
    @PostMapping("/getUserMenus")
    public ResponseEntity<Map<String, Object>> getUserMenus(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String role = request.get("role");
            if (role == null || role.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "角色参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            List<Menu> menus = menuService.getMenuTreeByRole(role);
            response.put("success", true);
            response.put("data", menus);
            response.put("message", "获取菜单成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有菜单（管理员用）- POST
     */
    @PostMapping("/all-menus")
    public ResponseEntity<Map<String, Object>> getAllMenus() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuService.getAllMenuTree();
            response.put("success", true);
            response.put("data", menus);
            response.put("message", "获取所有菜单成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取所有菜单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}