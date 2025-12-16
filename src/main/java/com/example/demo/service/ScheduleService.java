package com.example.demo.service;

import com.example.demo.dao.ScheduleDao;
import com.example.demo.dao.CourseDao;
import com.example.demo.dao.CoachDao;
import com.example.demo.dao.RoomDao;
import com.example.demo.entity.Coach;
import com.example.demo.entity.Course;
import com.example.demo.entity.Room;
import com.example.demo.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private CoachDao coachDao;

    @Autowired
    private RoomDao roomDao;

    // 获取所有课程选项
    public Map<String, Object> getAllCourses() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用CourseDao的方法
            List<Course> courses = courseDao.findAll();
            // 转换为前端需要的格式
            List<Map<String, Object>> courseList = courses.stream()
                    .map(course -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", course.getId());
                        map.put("title", course.getTitle());
                        return map;
                    })
                    .collect(Collectors.toList());

            result.put("success", true);
            result.put("data", courseList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取课程列表失败: " + e.getMessage());
        }
        return result;
    }

    // 获取所有教练选项
    public Map<String, Object> getAllCoaches() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用CoachDao的方法
            List<Coach> coaches = coachDao.findAll();
            List<Map<String, Object>> coachList = coaches.stream()
                    .map(coach -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", coach.getId());
                        map.put("name", coach.getName());
                        return map;
                    })
                    .collect(Collectors.toList());

            result.put("success", true);
            result.put("data", coachList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取教练列表失败: " + e.getMessage());
        }
        return result;
    }

    // 获取所有教室选项
    public Map<String, Object> getAllRooms() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用RoomDao的方法
            List<Room> rooms = roomDao.findAll();
            List<Map<String, Object>> roomList = rooms.stream()
                    .map(room -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", room.getId());
                        map.put("name", room.getName());
                        return map;
                    })
                    .collect(Collectors.toList());

            result.put("success", true);
            result.put("data", roomList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取教室列表失败: " + e.getMessage());
        }
        return result;
    }

    // 获取一周课程表
    public Map<String, Object> getWeeklySchedule(LocalDateTime date) {
        Map<String, Object> result = new HashMap<>();

        // 计算周一的日期
        LocalDateTime monday = date.with(DayOfWeek.MONDAY);
        LocalDateTime nextMonday = monday.plusWeeks(1);

        List<Schedule> schedules = scheduleDao.findWeeklySchedules(monday, nextMonday);

        // 按星期几分组
        Map<String, List<Schedule>> weeklyData = new HashMap<>();
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (String day : weekDays) {
            final String currentDay = day;
            List<Schedule> daySchedules = schedules.stream()
                    .filter(s -> s.getDayOfWeek().equals(currentDay))
                    .collect(Collectors.toList());
            weeklyData.put(day, daySchedules);
        }

        result.put("data", weeklyData);
        result.put("weekStart", monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.put("weekEnd", nextMonday.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.put("success", true);

        return result;
    }

    // 添加课程安排
    public Map<String, Object> addSchedule(Schedule schedule) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查时间冲突
            if (scheduleDao.hasTimeConflict(schedule.getRoomId(),
                    schedule.getStartTime(), schedule.getEndTime(), null)) {
                result.put("success", false);
                result.put("message", "该教室在该时间段已被占用");
                return result;
            }

            int id = scheduleDao.insert(schedule);
            result.put("success", true);
            result.put("data", id);
            result.put("message", "添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }

        return result;
    }

    // 更新课程安排
    public Map<String, Object> updateSchedule(Schedule schedule) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查时间冲突（排除自身）
            if (scheduleDao.hasTimeConflict(schedule.getRoomId(),
                    schedule.getStartTime(), schedule.getEndTime(), schedule.getId())) {
                result.put("success", false);
                result.put("message", "该教室在该时间段已被占用");
                return result;
            }

            int rows = scheduleDao.update(schedule);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "更新成功");
            } else {
                result.put("success", false);
                result.put("message", "课程安排不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    // 删除课程安排
    public Map<String, Object> deleteSchedule(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = scheduleDao.delete(id);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "课程安排不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    // 获取课程详情
    public Map<String, Object> getScheduleById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Schedule schedule = scheduleDao.findById(id);

        if (schedule != null) {
            result.put("data", schedule);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "课程安排不存在");
        }

        return result;
    }
}