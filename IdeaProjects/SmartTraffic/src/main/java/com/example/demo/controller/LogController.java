package com.example.demo.controller;

import com.example.demo.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {
    @Autowired
    private LogService logService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Map<String, Object> logData = logService.getLogsByPage(
                startTime, endTime, operator, operation, status, page, pageSize
        );

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", logData);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addLog(@RequestBody Map<String, Object> body) {
        String operator = body.get("operator") == null ? null : body.get("operator").toString();
        String operation = body.get("operation") == null ? null : body.get("operation").toString();
        String content = body.get("content") == null ? null : body.get("content").toString();
        String ip = body.get("ipAddress") == null ? "127.0.0.1" : body.get("ipAddress").toString();
        String status = body.get("status") == null ? "success" : body.get("status").toString();

        Map<String, Object> result = new HashMap<>();
        if (operator == null || operator.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "操作人员和操作内容不能为空");
            return ResponseEntity.ok(result);
        }

        logService.addLog(operator, operation, content, ip, status);

        result.put("success", true);
        result.put("message", "新增日志成功");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateLog(@RequestBody Map<String, Object> body) {
        Integer id = body.get("id") == null ? null : Integer.parseInt(body.get("id").toString());
        String content = body.get("content") == null ? null : body.get("content").toString();
        String status = body.get("status") == null ? null : body.get("status").toString();

        Map<String, Object> result = new HashMap<>();
        if (id == null || content == null || content.trim().isEmpty() || status == null || status.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "日志ID、操作内容和状态不能为空");
            return ResponseEntity.ok(result);
        }

        boolean updateSuccess = logService.updateLog(id, content, status);

        if (updateSuccess) {
            result.put("success", true);
            result.put("message", "修改日志成功");
        } else {
            result.put("success", false);
            result.put("message", "日志ID不存在，修改失败");
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteLog(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "日志ID不能为空");
            return ResponseEntity.ok(result);
        }

        boolean deleteSuccess = logService.deleteLog(id);
        if (deleteSuccess) {
            result.put("success", true);
            result.put("message", "删除日志成功");
        } else {
            result.put("success", false);
            result.put("message", "日志ID不存在，删除失败");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLogs(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String status) {
        List<Map<String, Object>> logs = logService.listLogs(startTime, endTime, operator, operation, status);

        StringBuilder csv = new StringBuilder();
        csv.append("日志ID,操作人员,操作类型,操作内容,IP地址,操作状态,操作时间\n");
        for (Map<String, Object> log : logs) {
            csv.append(escapeCsv(valueOf(log.get("id")))).append(',')
                    .append(escapeCsv(valueOf(log.get("operator")))).append(',')
                    .append(escapeCsv(valueOf(log.get("operation")))).append(',')
                    .append(escapeCsv(valueOf(log.get("content")))).append(',')
                    .append(escapeCsv(valueOf(log.get("ipAddress")))).append(',')
                    .append(escapeCsv(valueOf(log.get("status")))).append(',')
                    .append(escapeCsv(formatTime(log.get("operateTime"))))
                    .append('\n');
        }

        String filename = "system-logs-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv";
        byte[] bytes = ("\uFEFF" + csv.toString()).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/delete-batch")
    public ResponseEntity<Map<String, Object>> deleteBatch(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        Map<String, Object> result = new HashMap<>();
        if (!(idsObj instanceof List)) {
            result.put("success", false);
            result.put("message", "参数ids不能为空");
            return ResponseEntity.ok(result);
        }

        List<?> rawList = (List<?>) idsObj;
        if (rawList.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的日志");
            return ResponseEntity.ok(result);
        }

        List<Integer> ids = new java.util.ArrayList<>();
        for (Object item : rawList) {
            if (item != null) {
                ids.add(Integer.parseInt(item.toString()));
            }
        }

        int deleted = logService.deleteLogs(ids);
        result.put("success", deleted > 0);
        result.put("message", deleted > 0 ? "批量删除成功" : "未删除任何日志");
        result.put("deleted", deleted);
        return ResponseEntity.ok(result);
    }

    private String valueOf(Object value) {
        return value == null ? "" : value.toString();
    }

    private String formatTime(Object time) {
        if (time == null) {
            return "";
        }
        if (time instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) time);
        }
        return time.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuote = value.contains(",") || value.contains("\n") || value.contains("\r") || value.contains("\"");
        String escaped = value.replace("\"", "\"\"");
        return needsQuote ? "\"" + escaped + "\"" : escaped;
    }
}