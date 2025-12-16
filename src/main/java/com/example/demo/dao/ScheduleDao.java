package com.example.demo.dao;

import com.example.demo.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ScheduleDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 查询一周的课程表
    public List<Schedule> findWeeklySchedules(LocalDateTime weekStart, LocalDateTime weekEnd) {
        String sql = "SELECT s.*, c.title as course_title, co.name as coach_name, " +
                "r.name as room_name, " +
                "CASE DAYOFWEEK(s.start_time) " +
                "WHEN 1 THEN '周日' WHEN 2 THEN '周一' WHEN 3 THEN '周二' " +
                "WHEN 4 THEN '周三' WHEN 5 THEN '周四' WHEN 6 THEN '周五' WHEN 7 THEN '周六' " +
                "END as day_of_week " +
                "FROM schedules s " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE s.start_time >= ? AND s.start_time < ? AND s.status = 1 " +
                "ORDER BY s.start_time";

        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Schedule.class),
                weekStart, weekEnd);
    }

    // 根据ID查询
    public Schedule findById(Integer id) {
        String sql = "SELECT s.*, c.title as course_title, co.name as coach_name, " +
                "r.name as room_name FROM schedules s " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN coaches co ON s.coach_id = co.id " +
                "LEFT JOIN rooms r ON s.room_id = r.id " +
                "WHERE s.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Schedule.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增课程安排
    public int insert(Schedule schedule) {
        String sql = "INSERT INTO schedules (course_id, coach_id, room_id, start_time, end_time, " +
                "max_students, current_students, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, schedule.getCourseId());
            ps.setInt(2, schedule.getCoachId());
            ps.setInt(3, schedule.getRoomId());
            ps.setObject(4, schedule.getStartTime());
            ps.setObject(5, schedule.getEndTime());
            ps.setInt(6, schedule.getMaxStudents());
            ps.setInt(7, schedule.getCurrentStudents());
            ps.setInt(8, schedule.getStatus());
            ps.setObject(9, LocalDateTime.now());
            ps.setObject(10, LocalDateTime.now());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 更新课程安排
    public int update(Schedule schedule) {
        String sql = "UPDATE schedules SET course_id = ?, coach_id = ?, room_id = ?, " +
                "start_time = ?, end_time = ?, max_students = ?, status = ?, updated_at = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql,
                schedule.getCourseId(), schedule.getCoachId(), schedule.getRoomId(),
                schedule.getStartTime(), schedule.getEndTime(), schedule.getMaxStudents(),
                schedule.getStatus(), LocalDateTime.now(), schedule.getId());
    }

    // 删除课程安排
    public int delete(Integer id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 检查时间冲突
    public boolean hasTimeConflict(Integer roomId, LocalDateTime startTime, LocalDateTime endTime, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM schedules WHERE room_id = ? AND status = 1 " +
                "AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?)) ";

        if (excludeId != null) {
            sql += "AND id != ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
                    roomId, endTime, startTime, endTime, startTime, excludeId);
            return count != null && count > 0;
        } else {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
                    roomId, endTime, startTime, endTime, startTime);
            return count != null && count > 0;
        }
    }
}