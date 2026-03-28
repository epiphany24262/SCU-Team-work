package com.example.demo.service;

import com.example.demo.dao.SysUserDao;
import com.example.demo.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final int ROLE_OPERATOR = 3;

    @Autowired
    private SysUserDao sysUserDao;

    public Map<String, Object> login(String username, String password, String requestedRole) {
        Map<String, Object> result = new HashMap<>();
        String validationError = validateLogin(username, password, requestedRole);
        if (validationError != null) {
            result.put("success", false);
            result.put("message", validationError);
            return result;
        }

        SysUser user = sysUserDao.findByUsernameAndPassword(username, password);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }

        String roleCode = normalizeRoleCode(user.getRoleCode(), user.getRoleId());
        if (!isRoleMatched(requestedRole, roleCode)) {
            result.put("success", false);
            result.put("message", "角色不匹配，请选择正确的角色");
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("roleCode", roleCode);
        data.put("roleName", normalizeRoleName(user.getRoleName(), roleCode));

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("data", data);
        return result;
    }

    public Map<String, Object> register(String username, String password, String confirmPassword,
                                        String realName, String phone, String email) {
        Map<String, Object> result = new HashMap<>();
        String validationError = validateRegister(username, password, confirmPassword, realName, phone, email);
        if (validationError != null) {
            result.put("success", false);
            result.put("message", validationError);
            return result;
        }

        SysUser existing = sysUserDao.findByUsername(username);
        if (existing != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAvatar(null);
        user.setStatus(1);
        user.setRoleId(ROLE_OPERATOR);

        int rows = sysUserDao.insert(user);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            result.put("success", false);
            result.put("message", "注册失败，请稍后重试");
        }

        return result;
    }

    public Map<String, Object> getProfile(String username) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return result;
        }
        SysUser user = sysUserDao.findByUsername(username);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        data.put("roleCode", normalizeRoleCode(user.getRoleCode(), user.getRoleId()));
        data.put("roleName", normalizeRoleName(user.getRoleName(), data.get("roleCode").toString()));
        data.put("status", user.getStatus());

        result.put("success", true);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> updateProfile(String username, String realName, String phone, String email) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return result;
        }
        if (realName == null || realName.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入姓名");
            return result;
        }
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            result.put("success", false);
            result.put("message", "手机号格式不正确");
            return result;
        }
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            result.put("success", false);
            result.put("message", "邮箱格式不正确");
            return result;
        }

        SysUser existing = sysUserDao.findByUsername(username);
        if (existing == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        int rows = sysUserDao.updateProfileByUsername(username, realName, phone, email);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
            result.put("message", "更新失败，请稍后重试");
        }
        return result;
    }

    private String validateLogin(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            return "请输入用户名";
        }
        if (password == null || password.trim().isEmpty()) {
            return "请输入密码";
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "用户名格式不合法（3-20位字母、数字或下划线）";
        }
        if (password.length() < 6 || password.length() > 32) {
            return "密码长度需为6-32位";
        }
        if (role != null && !role.isEmpty() && !role.equals("manager") && !role.equals("user")) {
            return "角色参数不合法";
        }
        return null;
    }

    private String validateRegister(String username, String password, String confirmPassword,
                                    String realName, String phone, String email) {
        if (username == null || username.trim().isEmpty()) {
            return "请输入用户名";
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "用户名格式不合法（3-20位字母、数字或下划线）";
        }
        if (password == null || password.trim().isEmpty()) {
            return "请输入密码";
        }
        if (password.length() < 6 || password.length() > 32) {
            return "密码长度需为6-32位";
        }
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }
        if (realName == null || realName.trim().isEmpty()) {
            return "请输入姓名";
        }
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            return "手机号格式不正确";
        }
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return "邮箱格式不正确";
        }
        return null;
    }

    private boolean isRoleMatched(String requestedRole, String roleCode) {
        if (requestedRole == null || requestedRole.isEmpty()) {
            return true;
        }
        if (roleCode == null) {
            return "user".equals(requestedRole);
        }
        if ("manager".equals(requestedRole)) {
            return "SUPER_ADMIN".equalsIgnoreCase(roleCode) || "ADMIN".equalsIgnoreCase(roleCode);
        }
        return "OPERATOR".equalsIgnoreCase(roleCode);
    }

    private String normalizeRoleCode(String roleCode, Integer roleId) {
        if (roleCode != null && !roleCode.trim().isEmpty()) {
            return roleCode;
        }
        if (roleId != null) {
            if (roleId == 1) {
                return "SUPER_ADMIN";
            }
            if (roleId == 2) {
                return "ADMIN";
            }
            if (roleId == 3) {
                return "OPERATOR";
            }
        }
        return "OPERATOR";
    }

    private String normalizeRoleName(String roleName, String roleCode) {
        if (roleName != null && !roleName.trim().isEmpty()) {
            return roleName;
        }
        if ("SUPER_ADMIN".equalsIgnoreCase(roleCode)) {
            return "超级管理员";
        }
        if ("ADMIN".equalsIgnoreCase(roleCode)) {
            return "普通管理员";
        }
        return "操作员";
    }
}
