package com.example.demo.entity;

import java.util.Date;

public class RolePermission {
    private Integer id;
    private String role;
    private Integer menuId;
    private Date createTime;

    // 构造方法
    public RolePermission() {}

    public RolePermission(Integer id, String role, Integer menuId, Date createTime) {
        this.id = id;
        this.role = role;
        this.menuId = menuId;
        this.createTime = createTime;
    }

    // Getter和Setter方法
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getMenuId() { return menuId; }
    public void setMenuId(Integer menuId) { this.menuId = menuId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}