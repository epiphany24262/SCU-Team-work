package com.example.demo.controller;

import com.example.demo.service.ViolationRecordService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/violations")
@CrossOrigin(origins = "*")
public class ViolationRecordController {
    @Autowired
    private ViolationRecordService violationRecordService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listViolations(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String illegalType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(violationRecordService.listViolations(plateNumber, illegalType, startTime, endTime, page, pageSize));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> statsViolations(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String illegalType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return ResponseEntity.ok(violationRecordService.statsViolations(plateNumber, illegalType, startTime, endTime));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportViolationsCsv(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String illegalType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<com.example.demo.entity.ViolationRecord> list = violationRecordService.listViolationsAll(plateNumber, illegalType, startTime, endTime);
        StringBuilder csv = new StringBuilder();
        csv.append("ID,车牌号码,违章时间,违章地点,违章类型,描述,处罚结果,置信度\n");
        for (com.example.demo.entity.ViolationRecord item : list) {
            csv.append(escapeCsv(String.valueOf(item.getId()))).append(',')
                    .append(escapeCsv(item.getLicensePlate())).append(',')
                    .append(escapeCsv(item.getViolationTime())).append(',')
                    .append(escapeCsv(item.getViolationAddr())).append(',')
                    .append(escapeCsv(item.getViolationType())).append(',')
                    .append(escapeCsv(item.getViolationDesc())).append(',')
                    .append(escapeCsv(item.getPunishResult())).append(',')
                    .append(escapeCsv(item.getConfidence() == null ? "" : item.getConfidence().toString()))
                    .append('\n');
        }
        String filename = "violations-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv";
        byte[] bytes = ("\uFEFF" + csv.toString()).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/clear-batch")
    public ResponseEntity<Map<String, Object>> clearBatch(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        if (!(idsObj instanceof List)) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "参数ids不能为空");
            return ResponseEntity.ok(result);
        }
        List<?> rawList = (List<?>) idsObj;
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (Object item : rawList) {
            if (item != null) {
                ids.add(Integer.parseInt(item.toString()));
            }
        }
        return ResponseEntity.ok(violationRecordService.clearViolations(ids));
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
