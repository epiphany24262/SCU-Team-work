package com.example.demo.service;

import com.example.demo.dao.SysRoleDao;
import com.example.demo.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {
    @Autowired
    private SysRoleDao sysRoleDao;

    public Map<String, Object> listRoles(String roleName, String roleCode, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<SysRole> list = sysRoleDao.listRoles(roleName, roleCode, offset, pageSize);
        int total = sysRoleDao.countRoles(roleName, roleCode);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> createRole(SysRole role) {
        Map<String, Object> result = new HashMap<>();
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入角色名称");
            return result;
        }
        if (role.getRoleCode() == null || role.getRoleCode().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入角色标识");
            return result;
        }
        if (role.getDescription() == null || role.getDescription().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入角色描述");
            return result;
        }
        role.setStatus(1);
        int rows = sysRoleDao.insertRole(role);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "新增角色成功");
        } else {
            result.put("success", false);
            result.put("message", "新增角色失败");
        }
        return result;
    }

    public Map<String, Object> updateRole(SysRole role) {
        Map<String, Object> result = new HashMap<>();
        if (role.getId() == null) {
            result.put("success", false);
            result.put("message", "角色ID不能为空");
            return result;
        }
        int rows = sysRoleDao.updateRole(role);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "编辑角色成功");
        } else {
            result.put("success", false);
            result.put("message", "编辑角色失败");
        }
        return result;
    }

    public Map<String, Object> deleteRole(Integer id) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "角色ID不能为空");
            return result;
        }
        int rows = sysRoleDao.deleteRole(id);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "删除角色成功");
        } else {
            result.put("success", false);
            result.put("message", "删除角色失败");
        }
        return result;
    }
}
