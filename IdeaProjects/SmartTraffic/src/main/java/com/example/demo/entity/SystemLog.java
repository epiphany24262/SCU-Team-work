package com.example.demo.entity;

import java.util.Date;

// 假设数据库表名为system_logs，字段：id, operator, operation, content, operate_time, ip_address, status
public class SystemLog {
    private Integer id;
    private String operator;   // 操作用户
    private String operation;  // 操作类型
    private String content;    // 操作内容
    private Date operateTime;  // 操作时间（对应数据库operate_time）
    private String ipAddress;  // IP地址（对应数据库ip_address）
    private String status;     // 操作状态（success/fail）

    // 构造方法（必须包含所有字段，确保addLog时赋值）
    public SystemLog(String operator, String operation, String content, String ipAddress, String status) {
        this.operator = operator;
        this.operation = operation;
        this.content = content;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    // 空构造方法（MyBatis映射必需）
    public SystemLog() {}

    // 完整的getter/setter（缺一不可！）
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getOperateTime() { return operateTime; }
    public void setOperateTime(Date operateTime) { this.operateTime = operateTime; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}