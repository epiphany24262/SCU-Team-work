package com.example.demo.entity;

import java.sql.Timestamp;

public class CarInfo {
    private Integer id;
    private String plateNumber;     // 车牌号
    private String plateColor;      // 车牌颜色
    private String vehicleType;     // 车辆类型
    private Double confidence;      // 置信度
    private String imagePath;       // 图片路径
    private String imageName;       // 图片名称
    private Timestamp detectionTime; // 检测时间
    private String cameraId;        // 摄像头ID
    private Timestamp createdAt;    // 创建时间
    private Timestamp updatedAt;    // 更新时间

    // 无参构造函数
    public CarInfo() {
    }

    // 全参构造函数
    public CarInfo(String plateNumber, String plateColor, String vehicleType,
                   Double confidence, String imagePath, String imageName,
                   Timestamp detectionTime, String cameraId) {
        this.plateNumber = plateNumber;
        this.plateColor = plateColor;
        this.vehicleType = vehicleType;
        this.confidence = confidence;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.detectionTime = detectionTime;
        this.cameraId = cameraId;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getPlateColor() { return plateColor; }
    public void setPlateColor(String plateColor) { this.plateColor = plateColor; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public Timestamp getDetectionTime() { return detectionTime; }
    public void setDetectionTime(Timestamp detectionTime) { this.detectionTime = detectionTime; }

    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CarInfo{" +
                "id=" + id +
                ", plateNumber='" + plateNumber + '\'' +
                ", plateColor='" + plateColor + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", confidence=" + confidence +
                ", imagePath='" + imagePath + '\'' +
                ", imageName='" + imageName + '\'' +
                ", detectionTime=" + detectionTime +
                ", cameraId='" + cameraId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}