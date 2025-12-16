package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Equipment {
    private Integer id;
    private String name;
    private String type;
    private String brand;
    private String model;
    private Integer quantity;
    private Integer availableQuantity;
    private String location;
    private String description;
    private String specifications;
    private LocalDate maintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String status; // AVAILABLE, MAINTENANCE, BROKEN, UNAVAILABLE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数
    public Equipment() {}

    public Equipment(String name, String type, String brand, String model, 
                    Integer quantity, String location, String description, 
                    String specifications) {
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.quantity = quantity;
        this.availableQuantity = quantity;
        this.location = location;
        this.description = description;
        this.specifications = specifications;
        this.status = "AVAILABLE";
    }

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    @JsonProperty("quantity")
    public Integer getQuantity() { return quantity; }
    @JsonProperty("quantity")
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    @JsonProperty("available_quantity")
    public Integer getAvailableQuantity() { return availableQuantity; }
    @JsonProperty("available_quantity")
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    @JsonProperty("location")
    public String getLocation() { return location; }
    @JsonProperty("location")
    public void setLocation(String location) { this.location = location; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(String description) { this.description = description; }

    @JsonProperty("specifications")
    public String getSpecifications() { return specifications; }
    @JsonProperty("specifications")
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    @JsonProperty("maintenance_date")
    public LocalDate getMaintenanceDate() { return maintenanceDate; }
    @JsonProperty("maintenance_date")
    public void setMaintenanceDate(LocalDate maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    @JsonProperty("next_maintenance_date")
    public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }
    @JsonProperty("next_maintenance_date")
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }

    @JsonProperty("status")
    public String getStatus() { return status; }
    @JsonProperty("status")
    public void setStatus(String status) { this.status = status; }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() { return createdAt; }
    @JsonProperty("created_at")
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @JsonProperty("updated_at")
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    @JsonProperty("updated_at")
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}