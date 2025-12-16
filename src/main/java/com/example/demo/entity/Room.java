package com.example.demo.entity;

import java.time.LocalDateTime;

public class Room {
    private Integer id;
    private String name;
    private Integer capacity;
    private String equipment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数
    public Room() {}

    public Room(String name, Integer capacity, String equipment) {
        this.name = name;
        this.capacity = capacity;
        this.equipment = equipment;
    }

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}