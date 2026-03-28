package com.example.demo.service;

import com.example.demo.dao.SysUserDao;
import com.example.demo.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AdminUserService {
    @Autowired
    private SysUserDao sysUserDao;

    public Map<String, Object> listUsers(String username, String role, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<SysUser> list = sysUserDao.listUsers(username, role, status, offset, pageSize);
        int total = sysUserDao.countUsers(username, role, status);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list.stream().map(this::toAdminUserMap).toArray());
        result.put("data", data);
        return result;
    }

    public Map<String, Object> createUser(String username, String password, String role, String phone, String status) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入用户名");
            return result;
        }
        if (password == null || password.length() < 6) {
            result.put("success", false);
            result.put("message", "密码不少于6位");
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
        user.setPhone(phone);
        user.setStatus(parseStatus(status));
        user.setRoleId(parseRoleId(role));

        int rows = sysUserDao.insert(user);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "新增用户成功");
        } else {
            result.put("success", false);
            result.put("message", "新增用户失败");
        }
        return result;
    }

    public Map<String, Object> updateUser(Long id, String role, String phone, String status) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }
        Integer roleId = parseRoleId(role);
        SysUser existing = sysUserDao.findById(id);
        if (existing != null && existing.getRoleId() != null && existing.getRoleId() == 1) {
            roleId = 1;
        }
        int rows = sysUserDao.updateUserCore(id, roleId, phone, parseStatus(status));
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "编辑用户成功");
        } else {
            result.put("success", false);
            result.put("message", "编辑用户失败");
        }
        return result;
    }

    public Map<String, Object> deleteUser(Long id) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }
        int rows = sysUserDao.deleteUser(id);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "删除用户成功");
        } else {
            result.put("success", false);
            result.put("message", "删除用户失败");
        }
        return result;
    }

    public Map<String, Object> updateStatus(Long id, String status) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }
        int rows = sysUserDao.updateUserStatus(id, parseStatus(status));
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "状态更新成功");
        } else {
            result.put("success", false);
            result.put("message", "状态更新失败");
        }
        return result;
    }

    public Map<String, Object> resetPassword(Long id) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }
        String newPwd = randomPassword();
        int rows = sysUserDao.updateUserPassword(id, newPwd);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "密码重置成功");
            Map<String, Object> data = new HashMap<>();
            data.put("newPassword", newPwd);
            result.put("data", data);
        } else {
            result.put("success", false);
            result.put("message", "密码重置失败");
        }
        return result;
    }

    private Map<String, Object> toAdminUserMap(SysUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("phone", user.getPhone());
        map.put("createTime", user.getCreateTime() == null ? "" : user.getCreateTime().toString());
        map.put("role", toRoleLabel(user.getRoleCode(), user.getRoleId()));
        map.put("status", toStatusLabel(user.getStatus()));
        return map;
    }

    private String toRoleLabel(String roleCode, Integer roleId) {
        if (roleCode == null) {
            if (roleId != null && (roleId == 1 || roleId == 2)) {
                return "manager";
            }
            return "user";
        }
        if ("SUPER_ADMIN".equalsIgnoreCase(roleCode) || "ADMIN".equalsIgnoreCase(roleCode)) {
            return "manager";
        }
        return "user";
    }

    private String toStatusLabel(Integer status) {
        if (status == null || status == 1) {
            return "enabled";
        }
        return "disabled";
    }

    private Integer parseStatus(String status) {
        if ("disabled".equalsIgnoreCase(status) || "0".equals(status)) {
            return 0;
        }
        return 1;
    }

    private Integer parseRoleId(String role) {
        if ("manager".equalsIgnoreCase(role)) {
            return 2;
        }
        return 3;
    }

    private String randomPassword() {
        String chars = "abcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
