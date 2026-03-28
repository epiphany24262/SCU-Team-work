package com.example.demo.dao;

import com.example.demo.entity.CarInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class ProcessDetailDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SAVE_DIR = "D:\\Desktop\\SmartTraffic\\save\\";

    /**
     * 保存Base64图片到本地
     */
    public String saveBase64Image(String base64Data, String fileName) {
        try {
            // 确保目录存在
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 解码Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 保存文件
            String filePath = SAVE_DIR + fileName;
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            }

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存车辆信息到car_list表
     */
    public int saveCarInfo(CarInfo carInfo, String violationType) {
        try {
            String sql = "INSERT INTO car_list (" +
                    "plate_number, plate_color, vehicle_type, confidence, " +
                    "image_path, illegal_type" +
                    ") VALUES (?, ?, ?, ?, ?, ?)";

            return jdbcTemplate.update(sql,
                    carInfo.getPlateNumber(),
                    carInfo.getPlateColor(),
                    carInfo.getVehicleType(),
                    carInfo.getConfidence(),
                    carInfo.getImageName(),
                    violationType
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}