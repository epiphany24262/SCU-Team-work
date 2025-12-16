// CourseDao.java
package com.example.demo.dao;

import com.example.demo.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Course> rowMapper = new BeanPropertyRowMapper<>(Course.class);

    // 查询所有
    public List<Course> findAll() {
        String sql = "SELECT * FROM courses";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class));
    }

    // 分页查询课程
    public List<Course> findCoursesByPage(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM courses WHERE 1=1";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (title LIKE ? OR type LIKE ?)";
            keyword = "%" + keyword + "%";
        }

        sql += " ORDER BY id DESC LIMIT ? OFFSET ?";

        if (keyword != null && !keyword.trim().isEmpty()) {
            return jdbcTemplate.query(sql, rowMapper, keyword, keyword, pageSize, offset);
        } else {
            return jdbcTemplate.query(sql, rowMapper, pageSize, offset);
        }
    }

    // 查询总数
    public int countCourses(String keyword) {
        String sql = "SELECT COUNT(*) FROM courses WHERE 1=1";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (title LIKE ? OR type LIKE ?)";
            keyword = "%" + keyword + "%";
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return jdbcTemplate.queryForObject(sql, Integer.class, keyword, keyword);
        } else {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        }
    }

    // 根据ID查询
    public Course findById(Integer id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    // 添加课程
    public int insert(Course course) {
        String sql = "INSERT INTO courses (title, content, type, difficulty, duration, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, course.getTitle(), course.getContent(), course.getType(),
                course.getDifficulty(), course.getDuration());
    }

    // 更新课程
    public int update(Course course) {
        String sql = "UPDATE courses SET title = ?, content = ?, type = ?, difficulty = ?, " +
                "duration = ?, updated_at = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql, course.getTitle(), course.getContent(), course.getType(),
                course.getDifficulty(), course.getDuration(), course.getId());
    }

    // 删除课程
    public int delete(Integer id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 检查课程名称是否已存在
    public boolean existsByTitle(String title, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM courses WHERE title = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, title, excludeId);
            return count != null && count > 0;
        } else {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, title);
            return count != null && count > 0;
        }
    }
}