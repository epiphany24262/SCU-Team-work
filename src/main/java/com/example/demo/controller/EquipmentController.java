package com.example.demo.controller;

import com.example.demo.entity.Equipment;
import com.example.demo.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    // 获取器材列表
    @GetMapping
    public Map<String, Object> getEquipments(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return equipmentService.getEquipments(keyword, type, status, page, pageSize);
    }

    // 根据ID获取器材信息
    @GetMapping("/{id}")
    public Map<String, Object> getEquipmentById(@PathVariable Integer id) {
        return equipmentService.getEquipmentById(id);
    }

    // 添加器材
    @PostMapping
    public Map<String, Object> addEquipment(@RequestBody Equipment equipment) {
        return equipmentService.addEquipment(equipment);
    }

    // 更新器材
    @PutMapping("/{id}")
    public Map<String, Object> updateEquipment(@PathVariable Integer id, @RequestBody Equipment equipment) {
        equipment.setId(id);
        return equipmentService.updateEquipment(equipment);
    }

    // 删除器材
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteEquipment(@PathVariable Integer id) {
        return equipmentService.deleteEquipment(id);
    }

    // 更新器材状态
    @PostMapping("/{id}/status")
    public Map<String, Object> updateEquipmentStatus(@PathVariable Integer id, @RequestParam String status) {
        return equipmentService.updateEquipmentStatus(id, status);
    }

    // 获取所有可用器材
    @GetMapping("/available")
    public Map<String, Object> getAvailableEquipments() {
        return equipmentService.getAvailableEquipments();
    }

    // 根据类型获取器材
    @GetMapping("/type/{type}")
    public Map<String, Object> getEquipmentsByType(@PathVariable String type) {
        return equipmentService.getEquipmentsByType(type);
    }

    // 保养器材
    @PostMapping("/{id}/maintain")
    public Map<String, Object> maintainEquipment(@PathVariable Integer id, @RequestParam String nextMaintenanceDate) {
        return equipmentService.maintainEquipment(id, nextMaintenanceDate);
    }
}