package com.example.demo.entity;

import java.util.Date;

public class TrackPoint {
    private Double lng;         // 经度
    private Double lat;         // 纬度
    private Integer speed;      // 速度
    private String direction;   // 方向（字符串：正东、正西等）
    private Date time;          // GPS时间

    // 构造方法
    public TrackPoint() {}

    public TrackPoint(Double lng, Double lat, Integer speed, String direction, Date time) {
        this.lng = lng;
        this.lat = lat;
        this.speed = speed;
        this.direction = direction;
        this.time = time;
    }

    // Getter和Setter
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
}