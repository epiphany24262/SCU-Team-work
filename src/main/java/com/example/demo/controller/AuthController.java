package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "用户名不能为空");
                return response;
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "密码不能为空");
                return response;
            }

            // 使用现有的UserService的login方法
            User user = userService.login(username, password);
            if (user != null) {
                // 生成token（简单实现）
                String token = "token_" + System.currentTimeMillis();

                response.put("success", true);
                response.put("message", "登录成功");
                response.put("data", new HashMap<String, Object>() {{
                    put("token", token);
                    put("user", user);
                }});
            } else {
                response.put("success", false);
                response.put("message", "用户名或密码错误");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登录失败: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            User savedUser = userService.addUser(user);
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", savedUser);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
        }

        return response;
    }
}