// CourseController.java
package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 分页查询课程
    @PostMapping("/page")
    public Map<String, Object> getCoursesByPage(@RequestBody Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        Integer page = (Integer) params.get("page");
        Integer pageSize = (Integer) params.get("pageSize");

        if (page == null) page = 1;
        if (pageSize == null) pageSize = 10;

        Map<String, Object> result = courseService.getCoursesByPage(keyword, page, pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result.get("data"));
        response.put("total", result.get("total"));
        response.put("message", "查询成功");

        return response;
    }

    // 添加课程
    @PostMapping("/add")
    public Map<String, Object> addCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = courseService.addCourse(course);
            if (success) {
                response.put("success", true);
                response.put("message", "添加课程成功");
            } else {
                response.put("success", false);
                response.put("message", "添加课程失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 更新课程
    @PostMapping("/update")
    public Map<String, Object> updateCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = courseService.updateCourse(course);
            if (success) {
                response.put("success", true);
                response.put("message", "更新课程成功");
            } else {
                response.put("success", false);
                response.put("message", "更新课程失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 删除课程
    @PostMapping("/delete")
    public Map<String, Object> deleteCourse(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = courseService.deleteCourse(id);
            if (success) {
                response.put("success", true);
                response.put("message", "删除课程成功");
            } else {
                response.put("success", false);
                response.put("message", "删除课程失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 根据ID查询课程详情
    @GetMapping("/{id}")
    public Map<String, Object> getCourseById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Course course = courseService.getCourseById(id);
            if (course != null) {
                response.put("success", true);
                response.put("data", course);
                response.put("message", "查询成功");
            } else {
                response.put("success", false);
                response.put("message", "课程不存在");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败");
        }

        return response;
    }
}