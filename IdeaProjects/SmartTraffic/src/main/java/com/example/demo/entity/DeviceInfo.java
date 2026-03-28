package com.example.demo.entity;

public class DeviceInfo {
    private String sn;      // 设备SN
    private String tname;   // 设备名称

    // 构造方法
    public DeviceInfo() {}

    public DeviceInfo(String sn, String tname) {
        this.sn = sn;
        this.tname = tname;
    }

    // Getter和Setter
    public String getSn() { return sn; }
    public void setSn(String sn) { this.sn = sn; }

    public String getTname() { return tname; }
    public void setTname(String tname) { this.tname = tname; }
}