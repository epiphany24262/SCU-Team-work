// CoachController.java
package com.example.demo.controller;

import com.example.demo.entity.Coach;
import com.example.demo.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/coaches")
@CrossOrigin(origins = "*") // 允许跨域访问
public class CoachController {

    @Autowired
    private CoachService coachService;

    // 分页查询教练列表
    @PostMapping("/page")
    public Map<String, Object> getCoachesByPage(@RequestBody Map<String, Object> params) {
        Integer page = (Integer) params.get("page");
        Integer pageSize = (Integer) params.get("pageSize");
        String keyword = (String) params.get("keyword");

        if (page == null) page = 1;
        if (pageSize == null) pageSize = 10;
        if (keyword == null) keyword = "";

        return coachService.getCoachesByPage(page, pageSize, keyword);
    }

    // 获取所有教练（用于下拉选择）
    @GetMapping("/all")
    public Map<String, Object> getAllCoaches() {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("data", coachService.getAllCoaches());
        return result;
    }

    // 根据ID获取教练详情
    @PostMapping("/detail")
    public Map<String, Object> getCoachById(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Map<String, Object> result = new java.util.HashMap<>();

        if (id == null) {
            result.put("success", false);
            result.put("message", "ID不能为空");
            return result;
        }

        Coach coach = coachService.getCoachById(id);
        if (coach != null) {
            result.put("success", true);
            result.put("data", coach);
        } else {
            result.put("success", false);
            result.put("message", "教练不存在");
        }

        return result;
    }

    // 添加教练
    @PostMapping("/add")
    public Map<String, Object> addCoach(@RequestBody Coach coach) {
        return coachService.addCoach(coach);
    }

    // 更新教练
    @PostMapping("/update")
    public Map<String, Object> updateCoach(@RequestBody Coach coach) {
        return coachService.updateCoach(coach);
    }

    // 删除教练
    @PostMapping("/delete")
    public Map<String, Object> deleteCoach(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Map<String, Object> result = new java.util.HashMap<>();

        if (id == null) {
            result.put("success", false);
            result.put("message", "ID不能为空");
            return result;
        }

        return coachService.deleteCoach(id);
    }
}