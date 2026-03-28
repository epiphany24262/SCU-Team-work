package com.example.demo.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SystemLogMapper {
    /**
     * 分页查询日志（7个参数）
     */
    List<Map<String, Object>> selectLogsByPage(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("operator") String operator,
            @Param("operation") String operation,
            @Param("status") String status,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * 统计日志总数（5个参数）
     */
    int countLogs(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("operator") String operator,
            @Param("operation") String operation,
            @Param("status") String status
    );

    /**
     * 新增日志
     */
    void insertLog(
            @Param("operator") String operator,
            @Param("operation") String operation,
            @Param("content") String content,
            @Param("ip") String ip,
            @Param("status") String status
    );

    /**
     * 修改日志
     */
    void updateLog(
            @Param("id") Integer id,
            @Param("content") String content,
            @Param("status") String status
    );

    /**
     * 根据ID统计日志
     */
    int countLogById(@Param("id") Integer id);
}