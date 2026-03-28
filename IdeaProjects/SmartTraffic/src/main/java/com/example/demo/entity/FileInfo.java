package com.example.demo.entity;

import java.time.LocalDateTime;

public class FileInfo {
    private Long id;
    private String name;
    private String url;
    private String type;
    private Long size;
    private LocalDateTime createTime;
    private LocalDateTime lastModified;

    public FileInfo() {}

    public FileInfo(Long id, String name, String url, String type, Long size) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
        this.createTime = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    // Getter 方法
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public Long getSize() {
        return size;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    // Setter 方法
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    // toString 方法
    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", createTime=" + createTime +
                ", lastModified=" + lastModified +
                '}';
    }

    // equals 和 hashCode 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (id != null ? !id.equals(fileInfo.id) : fileInfo.id != null) return false;
        if (name != null ? !name.equals(fileInfo.name) : fileInfo.name != null) return false;
        if (url != null ? !url.equals(fileInfo.url) : fileInfo.url != null) return false;
        if (type != null ? !type.equals(fileInfo.type) : fileInfo.type != null) return false;
        if (size != null ? !size.equals(fileInfo.size) : fileInfo.size != null) return false;
        if (createTime != null ? !createTime.equals(fileInfo.createTime) : fileInfo.createTime != null) return false;
        return lastModified != null ? lastModified.equals(fileInfo.lastModified) : fileInfo.lastModified == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }
}