package com.example.demo.controller;

import com.example.demo.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolePermissionController {
    @Autowired
    private RolePermissionService rolePermissionService;

    @GetMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> getPermissions(@PathVariable Integer id) {
        return ResponseEntity.ok(rolePermissionService.getRolePermissions(id));
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> savePermissions(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Object permIdsObj = body.get("permIds");
        List<Integer> permIds = null;
        if (permIdsObj instanceof List) {
            //noinspection unchecked
            permIds = (List<Integer>) permIdsObj;
        }
        return ResponseEntity.ok(rolePermissionService.saveRolePermissions(id, permIds));
    }
}
