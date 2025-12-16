package com.example.demo.controller;

import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // 获取教室列表
    @GetMapping
    public Map<String, Object> getRooms(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return roomService.getRooms(keyword, page, pageSize);
    }

    // 根据ID获取教室
    @GetMapping("/{id}")
    public Map<String, Object> getRoomById(@PathVariable Integer id) {
        return roomService.getRoomById(id);
    }

    // 添加教室
    @PostMapping
    public Map<String, Object> addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    // 更新教室
    @PutMapping("/{id}")
    public Map<String, Object> updateRoom(@PathVariable Integer id, @RequestBody Room room) {
        room.setId(id);
        return roomService.updateRoom(room);
    }

    // 删除教室
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteRoom(@PathVariable Integer id) {
        return roomService.deleteRoom(id);
    }
}