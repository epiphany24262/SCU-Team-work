package com.example.demo.service;

import com.example.demo.dao.SysRolePermissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RolePermissionService {
    @Autowired
    private SysRolePermissionDao rolePermissionDao;

    public Map<String, Object> getRolePermissions(Integer roleId) {
        Map<String, Object> result = new HashMap<>();
        if (roleId == null) {
            result.put("success", false);
            result.put("message", "角色ID不能为空");
            return result;
        }
        List<Integer> permIds = rolePermissionDao.listPermissionIdsByRoleId(roleId);
        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("permIds", permIds);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> saveRolePermissions(Integer roleId, List<Integer> permIds) {
        Map<String, Object> result = new HashMap<>();
        if (roleId == null) {
            result.put("success", false);
            result.put("message", "角色ID不能为空");
            return result;
        }
        rolePermissionDao.replaceRolePermissions(roleId, permIds);
        result.put("success", true);
        result.put("message", "权限保存成功");
        return result;
    }
}
