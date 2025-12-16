package com.example.demo.controller;

import com.example.demo.entity.Schedule;
import com.example.demo.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 获取一周课程表
    @GetMapping("/weekly")
    public Map<String, Object> getWeeklySchedule(
            @RequestParam(required = false) String date) {
        LocalDateTime localDate;
        if (date == null) {
            localDate = LocalDateTime.now();
        } else {
            localDate = LocalDate.parse(date).atStartOfDay();
        }
        return scheduleService.getWeeklySchedule(localDate);
    }

    // 根据ID获取课程安排
    @GetMapping("/{id}")
    public Map<String, Object> getScheduleById(@PathVariable Integer id) {
        return scheduleService.getScheduleById(id);
    }

    // 添加课程安排
    @PostMapping
    public Map<String, Object> addSchedule(@RequestBody Schedule schedule) {
        return scheduleService.addSchedule(schedule);
    }

    // 更新课程安排
    @PutMapping("/{id}")
    public Map<String, Object> updateSchedule(@PathVariable Integer id, @RequestBody Schedule schedule) {
        schedule.setId(id);
        return scheduleService.updateSchedule(schedule);
    }

    // 删除课程安排
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable Integer id) {
        return scheduleService.deleteSchedule(id);
    }

    // 获取课程选项
    @GetMapping("/courses")
    public Map<String, Object> getCourses() {
        return scheduleService.getAllCourses();
    }

    // 获取教练选项
    @GetMapping("/coaches")
    public Map<String, Object> getCoaches() {
        return scheduleService.getAllCoaches();
    }

    // 获取教室选项
    @GetMapping("/rooms")
    public Map<String, Object> getRooms() {
        return scheduleService.getAllRooms();
    }
}