package com.example.demo.service;

import com.example.demo.dao.CarListDao;
import com.example.demo.entity.CarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarListService {
    @Autowired
    private CarListDao carListDao;

    public Map<String, Object> listCars(String plateNumber, String vehicleType, String illegalType, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<CarList> list = carListDao.listCars(plateNumber, vehicleType, illegalType, offset, pageSize);
        int total = carListDao.countCars(plateNumber, vehicleType, illegalType);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> deleteCars(List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的车辆");
            return result;
        }
        int deleted = carListDao.deleteCarsByIds(ids);
        result.put("success", deleted > 0);
        result.put("message", deleted > 0 ? "批量删除成功" : "未删除任何车辆");
        result.put("deleted", deleted);
        return result;
    }

    public Map<String, Object> statsCars(String plateNumber, String vehicleType, String illegalType) {
        Map<String, Object> data = carListDao.statsCars(plateNumber, vehicleType, illegalType);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }

    public List<CarList> listCarsAll(String plateNumber, String vehicleType, String illegalType) {
        return carListDao.listCars(plateNumber, vehicleType, illegalType, 0, Integer.MAX_VALUE);
    }
}
