package com.example.demo.controller;

import com.example.demo.service.AdminUserService;
import com.example.demo.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private LogService logService;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ========== 需替换为你的真实逻辑 ==========
    // 示例1：从请求头获取（前端传递 X-Current-Username）
    // 示例2：从Session获取 request.getSession().getAttribute("currentUsername")
    // 示例3：从JWT Token解析
    private String getCurrentAdmin(HttpServletRequest request) {
        // 临时占位，替换成你系统获取当前登录管理员的方式
        return "admin";
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String adminUsername = getCurrentAdmin(request);

        try {
            Map<String, Object> response = adminUserService.listUsers(username, role, status, page, pageSize);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "管理员%s（IP：%s）%s查询用户列表，筛选条件：用户名=%s、角色=%s、状态=%s，分页：%s页/%s条，时间：%s",
                    adminUsername,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    username == null ? "无" : username,
                    role == null ? "无" : role,
                    status == null ? "无" : status,
                    page,
                    pageSize,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "list_users",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ========== 修复：删除错误的 id 变量引用 ==========
            String content = String.format(
                    "管理员%s（IP：%s）查询用户列表异常：%s，时间：%s",
                    adminUsername,
                    ip,
                    e.getMessage(),
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "list_users",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String adminUsername = getCurrentAdmin(request);
        String username = body.get("username") == null ? null : body.get("username").toString();
        String password = body.get("password") == null ? null : body.get("password").toString();
        String role = body.get("role") == null ? null : body.get("role").toString();
        String phone = body.get("phone") == null ? null : body.get("phone").toString();
        String status = body.get("status") == null ? null : body.get("status").toString();

        try {
            Map<String, Object> response = adminUserService.createUser(username, password, role, phone, status);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "管理员%s（IP：%s）%s创建用户，用户名=%s、角色=%s、手机号=%s、状态=%s，时间：%s",
                    adminUsername,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    username == null ? "无" : username,
                    role == null ? "无" : role,
                    phone == null ? "无" : phone,
                    status == null ? "无" : status,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "create_user",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "管理员%s（IP：%s）创建用户异常：%s，待创建用户名=%s，时间：%s",
                    adminUsername,
                    ip,
                    e.getMessage(),
                    username == null ? "无" : username,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "create_user",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String adminUsername = getCurrentAdmin(request);
        String role = body.get("role") == null ? null : body.get("role").toString();
        String phone = body.get("phone") == null ? null : body.get("phone").toString();
        String status = body.get("status") == null ? null : body.get("status").toString();

        try {
            Map<String, Object> response = adminUserService.updateUser(id, role, phone, status);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "管理员%s（IP：%s）%s编辑用户，用户ID=%s、新角色=%s、新手机号=%s、新状态=%s，时间：%s",
                    adminUsername,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    id,
                    role == null ? "无" : role,
                    phone == null ? "无" : phone,
                    status == null ? "无" : status,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "edit_user",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "管理员%s（IP：%s）编辑用户异常：%s，用户ID=%s，时间：%s",
                    adminUsername,
                    ip,
                    e.getMessage(),
                    id,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "edit_user",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String adminUsername = getCurrentAdmin(request);

        try {
            Map<String, Object> response = adminUserService.deleteUser(id);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "管理员%s（IP：%s）%s删除用户，用户ID=%s，时间：%s",
                    adminUsername,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    id,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "delete_user",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "管理员%s（IP：%s）删除用户异常：%s，用户ID=%s，时间：%s",
                    adminUsername,
                    ip,
                    e.getMessage(),
                    id,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "delete_user",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String adminUsername = getCurrentAdmin(request);
        String status = body.get("status") == null ? null : body.get("status").toString();

        try {
            Map<String, Object> response = adminUserService.updateStatus(id, status);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "管理员%s（IP：%s）%s修改用户状态，用户ID=%s、新状态=%s，时间：%s",
                    adminUsername,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    id,
                    status == null ? "无" : status,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "edit_user_status",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "管理员%s（IP：%s）修改用户状态异常：%s，用户ID=%s，时间：%s",
                    adminUsername,
                    ip,
                    e.getMessage(),
                    id,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "edit_user_status",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String adminUsername = getCurrentAdmin(request);

        try {
            Map<String, Object> response = adminUserService.resetPassword(id);
            boolean success = Boolean.TRUE.equals(response.get("success"));

            String content = String.format(
                    "管理员%s（IP：%s）%s重置用户密码，用户ID=%s，时间：%s",
                    adminUsername,
                    ip,
                    success ? "成功" : "失败：" + response.get("message"),
                    id,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "reset_user_password",
                    content,
                    ip,
                    success ? "success" : "fail"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String content = String.format(
                    "管理员%s（IP：%s）重置用户密码异常：%s，用户ID=%s，时间：%s",
                    adminUsername,
                    ip,
                    e.getMessage(),
                    id,
                    sdf.format(new Date())
            );
            logService.addLog(
                    adminUsername,
                    "reset_user_password",
                    content,
                    ip,
                    "fail"
            );
            throw new RuntimeException(e);
        }
    }
}