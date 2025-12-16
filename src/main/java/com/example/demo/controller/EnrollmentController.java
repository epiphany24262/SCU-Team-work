package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // 获取报名列表
    @GetMapping
    public Map<String, Object> getEnrollments(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return enrollmentService.getEnrollments(keyword, status, page, pageSize);
    }

    // 根据ID获取报名信息
    @GetMapping("/{id}")
    public Map<String, Object> getEnrollmentById(@PathVariable Integer id) {
        return enrollmentService.getEnrollmentById(id);
    }

    // 添加报名
    @PostMapping
    public Map<String, Object> addEnrollment(@RequestBody Enrollment enrollment) {
        return enrollmentService.addEnrollment(enrollment);
    }

    // 审核报名（单个）
    @PostMapping("/{id}/approve")
    public Map<String, Object> approveEnrollment(@PathVariable Integer id) {
        return enrollmentService.approveEnrollment(id);
    }

    // 批量审核报名
    @PostMapping("/batch-approve")
    public Map<String, Object> batchApproveEnrollments(@RequestBody List<Integer> ids) {
        return enrollmentService.batchApproveEnrollments(ids);
    }

    // 取消审核
    @PostMapping("/{id}/cancel-approval")
    public Map<String, Object> cancelApproval(@PathVariable Integer id) {
        return enrollmentService.cancelApproval(id);
    }

    // 删除报名
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteEnrollment(@PathVariable Integer id) {
        return enrollmentService.deleteEnrollment(id);
    }

    // 获取我的报名记录
    @GetMapping("/my")
    public Map<String, Object> getMyEnrollments(@RequestParam Integer userId) {
        return enrollmentService.getMyEnrollments(userId);
    }

    // 获取我的课程表（已报名且审核通过的）
    @GetMapping("/my-schedule")
    public Map<String, Object> getMySchedule(
            @RequestParam Integer userId,
            @RequestParam(required = false) String date) {
        return enrollmentService.getMySchedule(userId, date);
    }

    // 获取所有可报名课程
    @GetMapping("/available-courses")
    public Map<String, Object> getAvailableCourses(
            @RequestParam Integer userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date) {
        return enrollmentService.getAvailableCourses(userId, keyword, date);
    }
}