// Course.java
package com.example.demo.entity;

import java.util.Date;

public class Course {
    private Integer id;
    private String title;
    private String content;
    private String type;
    private String difficulty;
    private String imagePath;
    private Integer duration;
    private Date createdAt;
    private Date updatedAt;

    // 构造方法
    public Course() {}

    public Course(String title, String content, String type, String difficulty, Integer duration) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.difficulty = difficulty;
        this.duration = duration;
    }

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

}