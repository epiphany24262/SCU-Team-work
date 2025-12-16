package com.example.demo.service;

import com.example.demo.dao.RoomDao;
import com.example.demo.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomService {

    @Autowired
    private RoomDao roomDao;

    // 获取教室列表（分页）
    public Map<String, Object> getRooms(String keyword, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        List<Room> rooms = roomDao.findAll(keyword, page, pageSize);
        int total = roomDao.count(keyword);

        result.put("data", rooms);
        result.put("total", total);
        result.put("success", true);

        return result;
    }

    // 根据ID获取教室
    public Map<String, Object> getRoomById(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Room room = roomDao.findById(id);

        if (room != null) {
            result.put("data", room);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "教室不存在");
        }

        return result;
    }

    // 添加教室
    public Map<String, Object> addRoom(Room room) {
        Map<String, Object> result = new HashMap<>();

        try {
            int id = roomDao.insert(room);
            result.put("success", true);
            result.put("data", id);
            result.put("message", "添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }

        return result;
    }

    // 更新教室
    public Map<String, Object> updateRoom(Room room) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = roomDao.update(room);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "更新成功");
            } else {
                result.put("success", false);
                result.put("message", "教室不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    // 删除教室
    public Map<String, Object> deleteRoom(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            int rows = roomDao.delete(id);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "教室不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }
}