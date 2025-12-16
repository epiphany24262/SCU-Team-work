package com.example.demo.dao;

import com.example.demo.entity.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EnrollmentDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 查询报名列表（分页+关联查询）
    public List<Enrollment> findAll(String keyword, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT e.*, c.title as course_title, co.name as coach_name, " +
                "r.name as room_name, s.start_time, s.end_time " +
                "FROM enrollments e " +
                "LEFT JOIN schedules s ON e.schedule_id = s.id " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE (e.real_name LIKE ? OR c.title LIKE ?) ";

        if (status != null) {
            sql += "AND e.status = ? ";
        }

        sql += "ORDER BY e.created_at DESC LIMIT ? OFFSET ?";

        if (status != null) {
            return jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(Enrollment.class),
                    "%" + keyword + "%", "%" + keyword + "%", status, pageSize, offset);
        } else {
            return jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(Enrollment.class),
                    "%" + keyword + "%", "%" + keyword + "%", pageSize, offset);
        }
    }

    // 查询总数
    public int count(String keyword, Integer status) {
        String sql = "SELECT COUNT(*) FROM enrollments e " +
                "LEFT JOIN schedules s ON e.schedule_id = s.id " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "WHERE (e.real_name LIKE ? OR c.title LIKE ?) ";

        if (status != null) {
            sql += "AND e.status = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class,
                    "%" + keyword + "%", "%" + keyword + "%", status);
        } else {
            sql += "AND 1=1";
            return jdbcTemplate.queryForObject(sql, Integer.class,
                    "%" + keyword + "%", "%" + keyword + "%");
        }
    }

    // 根据ID查询
    public Enrollment findById(Integer id) {
        String sql = "SELECT e.*, c.title as course_title, co.name as coach_name, " +
                "r.name as room_name, s.start_time, s.end_time " +
                "FROM enrollments e " +
                "LEFT JOIN schedules s ON e.schedule_id = s.id " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE e.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Enrollment.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增报名
    public int insert(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (schedule_id, user_id, real_name, gender, age, phone, " +
                "status, notes, enrolled_at, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, enrollment.getScheduleId());
            ps.setInt(2, enrollment.getUserId());
            ps.setString(3, enrollment.getRealName());
            ps.setString(4, enrollment.getGender());
            ps.setInt(5, enrollment.getAge());
            ps.setString(6, enrollment.getPhone());
            ps.setInt(7, enrollment.getStatus());
            ps.setString(8, enrollment.getNotes());
            ps.setObject(9, LocalDateTime.now());
            ps.setObject(10, LocalDateTime.now());
            ps.setObject(11, LocalDateTime.now());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 更新报名状态（单个）
    public int updateStatus(Integer id, Integer status) {
        String sql = "UPDATE enrollments SET status = ?, updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, LocalDateTime.now(), id);
    }

    // 批量更新报名状态
    public int batchUpdateStatus(List<Integer> ids, Integer status) {
        String sql = "UPDATE enrollments SET status = ?, updated_at = ? WHERE id = ?";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Integer id : ids) {
            batchArgs.add(new Object[]{status, LocalDateTime.now(), id});
        }

        int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);

        // 返回成功更新的记录数
        int successCount = 0;
        for (int result : results) {
            if (result > 0) {
                successCount++;
            }
        }
        return successCount;
    }

    // 删除报名
    public int delete(Integer id) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 检查用户是否已报名该课程
    public boolean existsByUserAndSchedule(Integer userId, Integer scheduleId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE user_id = ? AND schedule_id = ? AND status = 1";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, scheduleId);
        return count != null && count > 0;
    }

    // 获取课程报名人数
    public int countBySchedule(Integer scheduleId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE schedule_id = ? AND status = 1";
        return jdbcTemplate.queryForObject(sql, Integer.class, scheduleId);
    }

    // 获取我的报名记录
    public List<Enrollment> findMyEnrollments(Integer userId) {
        String sql = "SELECT e.*, c.title as course_title, co.name as coach_name, " +
                "r.name as room_name, s.start_time, s.end_time, " +
                "CASE DAYOFWEEK(s.start_time) " +
                "WHEN 1 THEN '周日' WHEN 2 THEN '周一' WHEN 3 THEN '周二' " +
                "WHEN 4 THEN '周三' WHEN 5 THEN '周四' WHEN 6 THEN '周五' WHEN 7 THEN '周六' " +
                "END as day_of_week " +
                "FROM enrollments e " +
                "LEFT JOIN schedules s ON e.schedule_id = s.id " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE e.user_id = ? " +
                "ORDER BY s.start_time DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Enrollment.class), userId);
    }

    // 获取我的课程表（已报名且审核通过的）- 按周获取
    public List<Enrollment> findMySchedule(Integer userId, LocalDateTime weekStart, LocalDateTime weekEnd) {
        String sql = "SELECT e.*, c.title as course_title, co.name as coach_name, " +
                "r.name as room_name, s.start_time, s.end_time, " +
                "CASE DAYOFWEEK(s.start_time) " +
                "WHEN 1 THEN '周日' WHEN 2 THEN '周一' WHEN 3 THEN '周二' " +
                "WHEN 4 THEN '周三' WHEN 5 THEN '周四' WHEN 6 THEN '周五' WHEN 7 THEN '周六' " +
                "END as day_of_week " +
                "FROM enrollments e " +
                "LEFT JOIN schedules s ON e.schedule_id = s.id " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE e.user_id = ? AND e.status = 1 " +
                "AND s.start_time >= ? AND s.start_time < ? " +
                "ORDER BY s.start_time";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Enrollment.class),
                userId, weekStart, weekEnd);
    }

    // 获取所有可报名课程
    public List<Map<String, Object>> findAvailableCourses(Integer userId, String keyword, String date) {
        String sql = "SELECT s.*, c.title as course_title, " +
                "c.image_path as course_image_path, " +
                "co.name as coach_name, r.name as room_name, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.schedule_id = s.id AND e.status = 1) as current_students, " +
                "(CASE WHEN EXISTS (SELECT 1 FROM enrollments e WHERE e.schedule_id = s.id AND e.user_id = ?) THEN 1 ELSE 0 END) as has_enrolled " +
                "FROM schedules s " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE s.status = 1 ";

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (keyword != null && !keyword.isEmpty()) {
            sql += "AND (c.title LIKE ? OR co.name LIKE ?) ";
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (date != null && !date.isEmpty()) {
            sql += "AND DATE(s.start_time) = ? ";
            params.add(date);
        }

        sql += "ORDER BY s.start_time";

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            Map<String, Object> course = new HashMap<>();
            course.put("id", rs.getInt("id"));
            course.put("courseTitle", rs.getString("course_title"));
            course.put("coachName", rs.getString("coach_name"));
            course.put("roomName", rs.getString("room_name"));
            course.put("startTime", rs.getTimestamp("start_time"));
            course.put("endTime", rs.getTimestamp("end_time"));
            course.put("maxStudents", rs.getInt("max_students"));
            course.put("currentStudents", rs.getInt("current_students"));
            course.put("hasEnrolled", rs.getInt("has_enrolled") == 1);
            course.put("imagePath", rs.getString("course_image_path"));
            return course;
        });
    }
}