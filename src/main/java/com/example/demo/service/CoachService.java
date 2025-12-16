// CoachService.java
package com.example.demo.service;

import com.example.demo.dao.CoachDao;
import com.example.demo.entity.Coach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoachService {

    @Autowired
    private CoachDao coachDao;

    // 获取所有教练
    public List<Coach> getAllCoaches() {
        return coachDao.findAll();
    }

    // 分页查询教练
    public Map<String, Object> getCoachesByPage(int page, int pageSize, String keyword) {
        List<Coach> coaches = coachDao.findByPage(page, pageSize, keyword);
        int total = coachDao.count(keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("data", coaches);
        result.put("total", total);
        result.put("success", true);

        return result;
    }

    // 根据ID获取教练
    public Coach getCoachById(Integer id) {
        return coachDao.findById(id);
    }

    // 添加教练
    public Map<String, Object> addCoach(Coach coach) {
        Map<String, Object> result = new HashMap<>();

        // 验证数据
        if (!validateCoach(coach, result, null)) {
            return result;
        }

        // 设置时间戳
        coach.setCreatedAt(LocalDateTime.now());
        coach.setUpdatedAt(LocalDateTime.now());

        try {
            int id = coachDao.insert(coach);
            result.put("success", true);
            result.put("message", "添加教练成功");
            result.put("data", id);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加教练失败: " + e.getMessage());
        }

        return result;
    }

    // 更新教练
    public Map<String, Object> updateCoach(Coach coach) {
        Map<String, Object> result = new HashMap<>();

        // 检查教练是否存在
        Coach existingCoach = coachDao.findById(coach.getId());
        if (existingCoach == null) {
            result.put("success", false);
            result.put("message", "教练不存在");
            return result;
        }

        // 验证数据
        if (!validateCoach(coach, result, coach.getId())) {
            return result;
        }

        // 设置更新时间
        coach.setUpdatedAt(LocalDateTime.now());

        try {
            int affectedRows = coachDao.update(coach);
            if (affectedRows > 0) {
                result.put("success", true);
                result.put("message", "更新教练成功");
            } else {
                result.put("success", false);
                result.put("message", "更新教练失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新教练失败: " + e.getMessage());
        }

        return result;
    }

    // 删除教练
    public Map<String, Object> deleteCoach(Integer id) {
        Map<String, Object> result = new HashMap<>();

        // 检查教练是否存在
        Coach coach = coachDao.findById(id);
        if (coach == null) {
            result.put("success", false);
            result.put("message", "教练不存在");
            return result;
        }

        try {
            int affectedRows = coachDao.delete(id);
            if (affectedRows > 0) {
                result.put("success", true);
                result.put("message", "删除教练成功");
            } else {
                result.put("success", false);
                result.put("message", "删除教练失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除教练失败: " + e.getMessage());
        }

        return result;
    }

    // 验证教练数据
    private boolean validateCoach(Coach coach, Map<String, Object> result, Integer excludeId) {
        if (!StringUtils.hasText(coach.getName())) {
            result.put("success", false);
            result.put("message", "教练姓名不能为空");
            return false;
        }

        if (coach.getName().length() < 2 || coach.getName().length() > 20) {
            result.put("success", false);
            result.put("message", "教练姓名长度必须在2-20个字符之间");
            return false;
        }

        if (!StringUtils.hasText(coach.getGender()) ||
                (!"男".equals(coach.getGender()) && !"女".equals(coach.getGender()))) {
            result.put("success", false);
            result.put("message", "性别必须为男或女");
            return false;
        }

        if (coach.getAge() == null || coach.getAge() < 18 || coach.getAge() > 65) {
            result.put("success", false);
            result.put("message", "年龄必须在18-65岁之间");
            return false;
        }

        if (!StringUtils.hasText(coach.getSpecialty())) {
            result.put("success", false);
            result.put("message", "专业特长不能为空");
            return false;
        }

        // 验证手机号格式
        if (StringUtils.hasText(coach.getPhone())) {
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!coach.getPhone().matches(phoneRegex)) {
                result.put("success", false);
                result.put("message", "手机号格式不正确");
                return false;
            }

            // 检查手机号是否已存在
            if (coachDao.existsByPhone(coach.getPhone(), excludeId)) {
                result.put("success", false);
                result.put("message", "手机号已存在");
                return false;
            }
        }

        return true;
    }
}