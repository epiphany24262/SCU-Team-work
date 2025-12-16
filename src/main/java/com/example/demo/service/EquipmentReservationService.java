package com.example.demo.service;

import com.example.demo.dao.EquipmentDao;
import com.example.demo.dao.EquipmentReservationDao;
import com.example.demo.entity.EquipmentReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentReservationService {

    @Autowired
    private EquipmentReservationDao equipmentReservationDao;

    @Autowired
    private EquipmentDao equipmentDao;

    // 获取预约列表
    public Map<String, Object> getReservations(String keyword, String status, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        List<EquipmentReservation> reservations = equipmentReservationDao.findAll(keyword, status, page, pageSize);
        int total = equipmentReservationDao.count(keyword, status);

        result.put("data", reservations);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        result.put("success", true);

        return result;
    }

    // 根据ID获取预约信息
    public Map<String, Object> getReservationById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        EquipmentReservation reservation = equipmentReservationDao.findById(id);

        if (reservation != null) {
            result.put("data", reservation);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "预约记录不存在");
        }

        return result;
    }

    // 添加预约
    public Map<String, Object> addReservation(EquipmentReservation reservation) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查时间冲突
            if (equipmentReservationDao.hasTimeConflict(
                    reservation.getEquipmentId(),
                    reservation.getReservationDate().toString(),
                    reservation.getStartTime().toString(),
                    reservation.getEndTime().toString(),
                    null)) {
                result.put("success", false);
                result.put("message", "该时间段已有预约，请选择其他时间");
                return result;
            }

            // 检查器材可用性
            com.example.demo.entity.Equipment equipment = equipmentDao.findById(reservation.getEquipmentId());
            if (equipment == null || !"AVAILABLE".equals(equipment.getStatus()) || 
                equipment.getAvailableQuantity() < reservation.getQuantity()) {
                result.put("success", false);
                result.put("message", "器材不可用或数量不足");
                return result;
            }

            // 设置时间字段
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setUpdatedAt(LocalDateTime.now());

            // 确保状态为待审核
            if (reservation.getStatus() == null) {
                reservation.setStatus("PENDING");
            }

            int id = equipmentReservationDao.insert(reservation);
            result.put("success", true);
            result.put("data", id);
            result.put("message", "预约成功，等待审核");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预约失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // 审核预约（单个）
    public Map<String, Object> approveReservation(Integer id, String comment, Integer approvedBy) {
        Map<String, Object> result = new HashMap<>();

        try {
            EquipmentReservation reservation = equipmentReservationDao.findById(id);
            if (reservation == null) {
                result.put("success", false);
                result.put("message", "预约记录不存在");
                return result;
            }

            if ("APPROVED".equals(reservation.getStatus())) {
                result.put("success", false);
                result.put("message", "该预约已审核通过");
                return result;
            }

            // 检查器材可用性
            com.example.demo.entity.Equipment equipment = equipmentDao.findById(reservation.getEquipmentId());
            if (equipment == null || !"AVAILABLE".equals(equipment.getStatus()) || 
                equipment.getAvailableQuantity() < reservation.getQuantity()) {
                result.put("success", false);
                result.put("message", "器材不可用或数量不足，无法批准");
                return result;
            }

            int rows = equipmentReservationDao.updateStatus(id, "APPROVED", comment, approvedBy);
            if (rows > 0) {
                // 更新器材可用数量
                int newAvailableQuantity = equipment.getAvailableQuantity() - reservation.getQuantity();
                equipmentDao.updateAvailableQuantity(equipment.getId(), newAvailableQuantity);
                
                result.put("success", true);
                result.put("message", "审核通过");
            } else {
                result.put("success", false);
                result.put("message", "审核失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "审核失败: " + e.getMessage());
        }

        return result;
    }

    // 拒绝预约
    public Map<String, Object> rejectReservation(Integer id, String comment, Integer approvedBy) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = equipmentReservationDao.updateStatus(id, "REJECTED", comment, approvedBy);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "已拒绝该预约");
            } else {
                result.put("success", false);
                result.put("message", "预约记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "拒绝失败: " + e.getMessage());
        }

        return result;
    }

    // 批量审核预约
    public Map<String, Object> batchApproveReservations(List<Integer> ids, String comment, Integer approvedBy) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = equipmentReservationDao.batchUpdateStatus(ids, "APPROVED", comment, approvedBy);
            result.put("success", true);
            result.put("message", "成功审核 " + rows + " 条预约记录");
            result.put("data", rows);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量审核失败: " + e.getMessage());
        }

        return result;
    }

    // 取消预约
    public Map<String, Object> cancelReservation(Integer id, String comment, Integer cancelledBy) {
        Map<String, Object> result = new HashMap<>();

        try {
            EquipmentReservation reservation = equipmentReservationDao.findById(id);
            if (reservation == null) {
                result.put("success", false);
                result.put("message", "预约记录不存在");
                return result;
            }

            // 如果预约已批准，需要恢复器材可用数量
            if ("APPROVED".equals(reservation.getStatus())) {
                com.example.demo.entity.Equipment equipment = equipmentDao.findById(reservation.getEquipmentId());
                if (equipment != null) {
                    int newAvailableQuantity = equipment.getAvailableQuantity() + reservation.getQuantity();
                    equipmentDao.updateAvailableQuantity(equipment.getId(), newAvailableQuantity);
                }
            }

            int rows = equipmentReservationDao.cancelReservation(id, comment, cancelledBy);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "取消成功");
            } else {
                result.put("success", false);
                result.put("message", "预约记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消失败: " + e.getMessage());
        }

        return result;
    }

    // 删除预约
    public Map<String, Object> deleteReservation(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = equipmentReservationDao.delete(id);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "预约记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    // 获取我的预约记录
    public Map<String, Object> getMyReservations(Integer userId, String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<EquipmentReservation> reservations = equipmentReservationDao.findMyReservations(userId, status);
            result.put("success", true);
            result.put("data", reservations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取预约记录失败: " + e.getMessage());
        }
        return result;
    }

    // 获取某日期的预约情况
    public Map<String, Object> getReservationsByDate(Integer equipmentId, String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> reservations = equipmentReservationDao.getReservationsByDate(equipmentId, date);
            result.put("success", true);
            result.put("data", reservations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取预约情况失败: " + e.getMessage());
        }
        return result;
    }

    // 获取器材的预约统计
    public Map<String, Object> getReservationStats(Integer equipmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> stats = equipmentReservationDao.getReservationStats(equipmentId);
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }
        return result;
    }

    // 检查时间冲突
    public Map<String, Object> checkTimeConflict(Integer equipmentId, String date, String startTime, String endTime, Integer excludeId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean hasConflict = equipmentReservationDao.hasTimeConflict(equipmentId, date, startTime, endTime, excludeId);
            result.put("success", true);
            result.put("hasConflict", hasConflict);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查时间冲突失败: " + e.getMessage());
        }
        return result;
    }
}