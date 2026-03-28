package com.example.demo.controller;

import com.example.demo.service.AuthService;
import com.example.demo.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private LogService logService;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String username = body.get("username") == null ? null : body.get("username").toString();
        String password = body.get("password") == null ? null : body.get("password").toString();
        String role = body.get("role") == null ? null : body.get("role").toString();

        try {
            Map<String, Object> response = authService.login(username, password, role);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "%s（IP：%s）%s，角色：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    success ? "登录成功" : "登录失败：" + response.get("message"),
                    role == null ? "无" : role,
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "login",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "%s（IP：%s）登录异常：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    e.getMessage(),
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "login",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String username = body.get("username") == null ? null : body.get("username").toString();
        String password = body.get("password") == null ? null : body.get("password").toString();
        String confirmPassword = body.get("confirmPassword") == null ? null : body.get("confirmPassword").toString();
        String realName = body.get("realName") == null ? null : body.get("realName").toString();
        String phone = body.get("phone") == null ? null : body.get("phone").toString();
        String email = body.get("email") == null ? null : body.get("email").toString();

        try {
            Map<String, Object> response = authService.register(username, password, confirmPassword, realName, phone, email);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "%s（IP：%s）%s，真实姓名：%s，手机号：%s，邮箱：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    success ? "注册成功" : "注册失败：" + response.get("message"),
                    realName == null ? "无" : realName,
                    phone == null ? "无" : phone,
                    email == null ? "无" : email,
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "register",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            if (Boolean.TRUE.equals(response.get("success"))) {
                response.putIfAbsent("data", new HashMap<>());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "%s（IP：%s）注册异常：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    e.getMessage(),
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "register",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(@RequestParam("username") String username, HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        try {
            Map<String, Object> response = authService.getProfile(username);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "%s（IP：%s）%s获取个人信息，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "get_profile",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "%s（IP：%s）获取个人信息异常：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    e.getMessage(),
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "get_profile",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String username = body.get("username") == null ? null : body.get("username").toString();
        String realName = body.get("realName") == null ? null : body.get("realName").toString();
        String phone = body.get("phone") == null ? null : body.get("phone").toString();
        String email = body.get("email") == null ? null : body.get("email").toString();

        try {
            Map<String, Object> response = authService.updateProfile(username, realName, phone, email);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "%s（IP：%s）%s修改个人信息，新真实姓名：%s，新手机号：%s，新邮箱：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    realName == null ? "无" : realName,
                    phone == null ? "无" : phone,
                    email == null ? "无" : email,
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "edit_profile",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "%s（IP：%s）修改个人信息异常：%s，时间：%s",
                    username == null ? "未知用户" : username,
                    ip,
                    e.getMessage(),
                    sdf.format(new Date())
            );
            logService.addLog(
                    username == null ? "未知用户" : username,
                    "edit_profile",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }
}