package com.example.demo.service;

import com.example.demo.dao.EnrollmentDao;
import com.example.demo.entity.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentDao enrollmentDao;

    // 获取报名列表
    public Map<String, Object> getEnrollments(String keyword, Integer status, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        List<Enrollment> enrollments = enrollmentDao.findAll(keyword, status, page, pageSize);
        int total = enrollmentDao.count(keyword, status);

        result.put("data", enrollments);
        result.put("total", total);
        result.put("success", true);

        return result;
    }

    // 根据ID获取报名信息
    public Map<String, Object> getEnrollmentById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Enrollment enrollment = enrollmentDao.findById(id);

        if (enrollment != null) {
            result.put("data", enrollment);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "报名记录不存在");
        }

        return result;
    }

    // 添加报名
    public Map<String, Object> addEnrollment(Enrollment enrollment) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查是否已报名
            if (enrollmentDao.existsByUserAndSchedule(enrollment.getUserId(), enrollment.getScheduleId())) {
                result.put("success", false);
                result.put("message", "您已报名该课程");
                return result;
            }

            // 设置时间字段
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setCreatedAt(LocalDateTime.now());
            enrollment.setUpdatedAt(LocalDateTime.now());

            // 确保状态为待审核
            if (enrollment.getStatus() == null) {
                enrollment.setStatus(0);
            }

            int id = enrollmentDao.insert(enrollment);
            result.put("success", true);
            result.put("data", id);
            result.put("message", "报名成功，等待审核");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "报名失败: " + e.getMessage());
            e.printStackTrace(); // 添加这行查看具体错误
        }

        return result;
    }

    // 审核报名（单个）
    public Map<String, Object> approveEnrollment(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            Enrollment enrollment = enrollmentDao.findById(id);
            if (enrollment == null) {
                result.put("success", false);
                result.put("message", "报名记录不存在");
                return result;
            }

            if (enrollment.getStatus() == 1) {
                result.put("success", false);
                result.put("message", "该报名已审核通过");
                return result;
            }

            int rows = enrollmentDao.updateStatus(id, 1);
            if (rows > 0) {
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

    // 批量审核报名
    public Map<String, Object> batchApproveEnrollments(List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = enrollmentDao.batchUpdateStatus(ids, 1);
            result.put("success", true);
            result.put("message", "成功审核 " + rows + " 条报名记录");
            result.put("data", rows);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量审核失败: " + e.getMessage());
        }

        return result;
    }

    // 取消审核（将状态改为未审核）
    public Map<String, Object> cancelApproval(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = enrollmentDao.updateStatus(id, 0);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "取消审核成功");
            } else {
                result.put("success", false);
                result.put("message", "报名记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消审核失败: " + e.getMessage());
        }

        return result;
    }

    // 删除报名
    public Map<String, Object> deleteEnrollment(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = enrollmentDao.delete(id);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "报名记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }
    // 获取我的报名记录
    public Map<String, Object> getMyEnrollments(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Enrollment> enrollments = enrollmentDao.findMyEnrollments(userId);
            result.put("success", true);
            result.put("data", enrollments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取报名记录失败: " + e.getMessage());
        }
        return result;
    }

    // 获取我的课程表（已报名且审核通过的）- 按周获取
    public Map<String, Object> getMySchedule(Integer userId, String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 计算周范围
            LocalDateTime queryDate;
            if (date != null && !date.isEmpty()) {
                queryDate = LocalDate.parse(date).atStartOfDay();
            } else {
                queryDate = LocalDateTime.now();
            }

            LocalDateTime monday = queryDate.with(DayOfWeek.MONDAY);
            LocalDateTime nextMonday = monday.plusWeeks(1);

            List<Enrollment> enrollments = enrollmentDao.findMySchedule(userId, monday, nextMonday);

            // 按星期几分组，格式与 ScheduleService 保持一致
            Map<String, List<Map<String, Object>>> scheduleData = new HashMap<>();
            String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

            // 初始化所有星期几
            for (String day : weekDays) {
                scheduleData.put(day, new ArrayList<>());
            }

            for (Enrollment enrollment : enrollments) {
                String dayOfWeek = enrollment.getDayOfWeek();

                Map<String, Object> schedule = new HashMap<>();
                schedule.put("id", enrollment.getScheduleId());
                schedule.put("courseTitle", enrollment.getCourseTitle());
                schedule.put("coachName", enrollment.getCoachName());
                schedule.put("roomName", enrollment.getRoomName());
                schedule.put("startTime", enrollment.getStartTime());
                schedule.put("endTime", enrollment.getEndTime());

                if (scheduleData.containsKey(dayOfWeek)) {
                    scheduleData.get(dayOfWeek).add(schedule);
                }
            }

            result.put("data", scheduleData);
            result.put("weekStart", monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            result.put("weekEnd", nextMonday.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取课程表失败: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    // 获取所有可报名课程
    public Map<String, Object> getAvailableCourses(Integer userId, String keyword, String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> courses = enrollmentDao.findAvailableCourses(userId, keyword, date);
            result.put("success", true);
            result.put("data", courses);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取课程列表失败: " + e.getMessage());
        }
        return result;
    }

}