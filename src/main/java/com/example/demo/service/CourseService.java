// CourseService.java
package com.example.demo.service;

import com.example.demo.dao.CourseDao;
import com.example.demo.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    @Autowired
    private CourseDao courseDao;

    // 分页查询课程
    public Map<String, Object> getCoursesByPage(String keyword, int page, int pageSize) {
        List<Course> courses = courseDao.findCoursesByPage(keyword, page, pageSize);
        int total = courseDao.countCourses(keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("data", courses);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }

    // 根据ID查询课程
    public Course getCourseById(Integer id) {
        return courseDao.findById(id);
    }

    // 添加课程
    public boolean addCourse(Course course) {
        // 检查课程名称是否已存在
        if (courseDao.existsByTitle(course.getTitle(), null)) {
            throw new RuntimeException("课程名称已存在");
        }

        int result = courseDao.insert(course);
        return result > 0;
    }

    // 更新课程
    public boolean updateCourse(Course course) {
        // 检查课程名称是否已存在（排除当前课程）
        if (courseDao.existsByTitle(course.getTitle(), course.getId())) {
            throw new RuntimeException("课程名称已存在");
        }

        Course existingCourse = courseDao.findById(course.getId());
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在");
        }

        int result = courseDao.update(course);
        return result > 0;
    }

    // 删除课程
    public boolean deleteCourse(Integer id) {
        Course course = courseDao.findById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        int result = courseDao.delete(id);
        return result > 0;
    }
}