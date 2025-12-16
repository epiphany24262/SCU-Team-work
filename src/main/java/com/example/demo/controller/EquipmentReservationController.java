package com.example.demo.controller;

import com.example.demo.entity.EquipmentReservation;
import com.example.demo.service.EquipmentReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment-reservations")
@CrossOrigin(origins = "*")
public class EquipmentReservationController {

    @Autowired
    private EquipmentReservationService equipmentReservationService;

    // 获取预约列表
    @GetMapping
    public Map<String, Object> getReservations(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return equipmentReservationService.getReservations(keyword, status, page, pageSize);
    }

    // 根据ID获取预约信息
    @GetMapping("/{id}")
    public Map<String, Object> getReservationById(@PathVariable Integer id) {
        return equipmentReservationService.getReservationById(id);
    }

    // 添加预约
    @PostMapping
    public Map<String, Object> addReservation(@RequestBody EquipmentReservation reservation) {
        return equipmentReservationService.addReservation(reservation);
    }

    // 审核预约（批准）
    @PostMapping("/{id}/approve")
    public Map<String, Object> approveReservation(@PathVariable Integer id, 
                                                 @RequestParam(required = false) String comment,
                                                 @RequestParam Integer approvedBy) {
        return equipmentReservationService.approveReservation(id, comment, approvedBy);
    }

    // 审核预约（拒绝）
    @PostMapping("/{id}/reject")
    public Map<String, Object> rejectReservation(@PathVariable Integer id,
                                                @RequestParam(required = false) String comment,
                                                @RequestParam Integer approvedBy) {
        return equipmentReservationService.rejectReservation(id, comment, approvedBy);
    }

    // 批量审核预约
    @PostMapping("/batch-approve")
    public Map<String, Object> batchApproveReservations(@RequestBody List<Integer> ids,
                                                       @RequestParam(required = false) String comment,
                                                       @RequestParam Integer approvedBy) {
        return equipmentReservationService.batchApproveReservations(ids, comment, approvedBy);
    }

    // 取消预约
    @PutMapping("/{id}/cancel")
    public Map<String, Object> cancelReservation(@PathVariable Integer id,
                                                @RequestParam(required = false) String comment,
                                                @RequestParam Integer cancelledBy) {
        return equipmentReservationService.cancelReservation(id, comment, cancelledBy);
    }

    // 删除预约
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteReservation(@PathVariable Integer id) {
        return equipmentReservationService.deleteReservation(id);
    }

    // 获取我的预约记录
    @GetMapping("/my")
    public Map<String, Object> getMyReservations(@RequestParam Integer userId,
                                                @RequestParam(required = false) String status) {
        return equipmentReservationService.getMyReservations(userId, status);
    }

    // 获取某日期的预约情况
    @GetMapping("/date/{date}")
    public Map<String, Object> getReservationsByDate(@RequestParam Integer equipmentId,
                                                     @PathVariable String date) {
        return equipmentReservationService.getReservationsByDate(equipmentId, date);
    }

    // 获取器材的预约统计
    @GetMapping("/stats/{equipmentId}")
    public Map<String, Object> getReservationStats(@PathVariable Integer equipmentId) {
        return equipmentReservationService.getReservationStats(equipmentId);
    }

    // 检查时间冲突
    @GetMapping("/check-conflict")
    public Map<String, Object> checkTimeConflict(@RequestParam Integer equipmentId,
                                                 @RequestParam String date,
                                                 @RequestParam String startTime,
                                                 @RequestParam String endTime,
                                                 @RequestParam(required = false) Integer excludeId) {
        return equipmentReservationService.checkTimeConflict(equipmentId, date, startTime, endTime, excludeId);
    }
}