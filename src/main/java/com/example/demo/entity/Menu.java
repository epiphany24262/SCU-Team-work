package com.example.demo.entity;

import java.util.Date;
import java.util.List;

public class Menu {
    private Integer id;
    private Integer parentId;
    private String name;
    private String icon;
    private String index;
    private String path;
    private Integer sort;
    private String type;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // 子菜单列表
    private List<Menu> children;

    // 构造方法
    public Menu() {}

    public Menu(Integer id, Integer parentId, String name, String icon, String index,
                String path, Integer sort, String type, Integer status,
                Date createTime, Date updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.icon = icon;
        this.index = index;
        this.path = path;
        this.sort = sort;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getter和Setter方法
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getIndex() { return index; }
    public void setIndex(String index) { this.index = index; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public List<Menu> getChildren() { return children; }
    public void setChildren(List<Menu> children) { this.children = children; }
}