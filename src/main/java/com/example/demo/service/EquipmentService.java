package com.example.demo.service;

import com.example.demo.dao.EquipmentDao;
import com.example.demo.entity.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentDao equipmentDao;

    // 获取器材列表
    public Map<String, Object> getEquipments(String keyword, String type, String status, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        List<Equipment> equipments = equipmentDao.findAll(keyword, type, status, page, pageSize);
        int total = equipmentDao.count(keyword, type, status);

        result.put("data", equipments);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        result.put("success", true);

        return result;
    }

    // 根据ID获取器材信息
    public Map<String, Object> getEquipmentById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Equipment equipment = equipmentDao.findById(id);

        if (equipment != null) {
            result.put("data", equipment);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "器材不存在");
        }

        return result;
    }

    // 添加器材
    public Map<String, Object> addEquipment(Equipment equipment) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查器材名称是否已存在
            if (equipmentDao.existsByName(equipment.getName())) {
                result.put("success", false);
                result.put("message", "器材名称已存在");
                return result;
            }

            // 设置时间字段
            equipment.setCreatedAt(LocalDateTime.now());
            equipment.setUpdatedAt(LocalDateTime.now());

            // 确保状态和数量正确
            if (equipment.getStatus() == null) {
                equipment.setStatus("AVAILABLE");
            }
            if (equipment.getAvailableQuantity() == null) {
                equipment.setAvailableQuantity(equipment.getQuantity());
            }

            int id = equipmentDao.insert(equipment);
            result.put("success", true);
            result.put("data", id);
            result.put("message", "添加器材成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加器材失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // 更新器材
    public Map<String, Object> updateEquipment(Equipment equipment) {
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println("=== 开始更新器材 ===");
            System.out.println("接收到的器材数据:");
            System.out.println("ID: " + equipment.getId());
            System.out.println("名称: " + equipment.getName());
            System.out.println("类型: " + equipment.getType());
            System.out.println("品牌: " + equipment.getBrand());
            System.out.println("型号: " + equipment.getModel());
            System.out.println("数量: " + equipment.getQuantity());
            System.out.println("可用数量: " + equipment.getAvailableQuantity());
            System.out.println("位置: " + equipment.getLocation());
            System.out.println("状态: " + equipment.getStatus());
            
            Equipment existingEquipment = equipmentDao.findById(equipment.getId());
            if (existingEquipment == null) {
                result.put("success", false);
                result.put("message", "器材不存在");
                return result;
            }

            // 检查器材名称是否已存在（排除当前器材）
            if (!existingEquipment.getName().equals(equipment.getName()) && 
                equipmentDao.existsByName(equipment.getName())) {
                result.put("success", false);
                result.put("message", "器材名称已存在");
                return result;
            }

            // 更新可用数量逻辑
            if (!equipment.getQuantity().equals(existingEquipment.getQuantity())) {
                int quantityDiff = equipment.getQuantity() - existingEquipment.getQuantity();
                int newAvailableQuantity = existingEquipment.getAvailableQuantity() + quantityDiff;
                equipment.setAvailableQuantity(Math.max(0, newAvailableQuantity));
            } else {
                // 如果数量没有变化，保持原有的available_quantity
                equipment.setAvailableQuantity(existingEquipment.getAvailableQuantity());
            }
            
            // 确保available_quantity不为null
            if (equipment.getAvailableQuantity() == null) {
                System.out.println("WARNING: available_quantity为null，设置为quantity值: " + equipment.getQuantity());
                equipment.setAvailableQuantity(equipment.getQuantity());
            }

            System.out.println("=== 处理后的器材数据 ===");
            System.out.println("最终可用数量: " + equipment.getAvailableQuantity());
            
            equipment.setUpdatedAt(LocalDateTime.now());

            int rows = equipmentDao.update(equipment);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "更新器材成功");
            } else {
                result.put("success", false);
                result.put("message", "更新器材失败");
            }
        } catch (Exception e) {
            System.out.println("=== 更新器材异常 ===");
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "更新器材失败: " + e.getMessage());
        }

        return result;
    }

    // 删除器材
    public Map<String, Object> deleteEquipment(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = equipmentDao.delete(id);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除器材成功");
            } else {
                result.put("success", false);
                result.put("message", "器材不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除器材失败: " + e.getMessage());
        }

        return result;
    }

    // 更新器材状态
    public Map<String, Object> updateEquipmentStatus(Integer id, String status) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = equipmentDao.updateStatus(id, status);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "更新状态成功");
            } else {
                result.put("success", false);
                result.put("message", "器材不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新状态失败: " + e.getMessage());
        }

        return result;
    }

    // 获取所有可用器材
    public Map<String, Object> getAvailableEquipments() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Equipment> equipments = equipmentDao.findAvailableEquipments();
            result.put("success", true);
            result.put("data", equipments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取器材列表失败: " + e.getMessage());
        }
        return result;
    }

    // 根据类型获取器材
    public Map<String, Object> getEquipmentsByType(String type) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Equipment> equipments = equipmentDao.findByType(type);
            result.put("success", true);
            result.put("data", equipments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取器材列表失败: " + e.getMessage());
        }
        return result;
    }

    // 保养器材
    public Map<String, Object> maintainEquipment(Integer id, String nextMaintenanceDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            Equipment equipment = equipmentDao.findById(id);
            if (equipment == null) {
                result.put("success", false);
                result.put("message", "器材不存在");
                return result;
            }

            // 更新保养日期
            equipment.setMaintenanceDate(java.time.LocalDate.now());
            equipment.setNextMaintenanceDate(java.time.LocalDate.parse(nextMaintenanceDate));
            equipment.setStatus("AVAILABLE");
            equipment.setUpdatedAt(LocalDateTime.now());

            int rows = equipmentDao.update(equipment);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "保养记录更新成功");
            } else {
                result.put("success", false);
                result.put("message", "保养记录更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保养记录更新失败: " + e.getMessage());
        }

        return result;
    }
}