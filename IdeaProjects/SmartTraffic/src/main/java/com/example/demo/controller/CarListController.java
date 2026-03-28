package com.example.demo.controller;

import com.example.demo.service.CarListService;
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
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarListController {
    @Autowired
    private CarListService carListService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listCars(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String illegalType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(carListService.listCars(plateNumber, vehicleType, illegalType, page, pageSize));
    }

    @PostMapping("/delete-batch")
    public ResponseEntity<Map<String, Object>> deleteBatch(@RequestBody Map<String, Object> body) {
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
        return ResponseEntity.ok(carListService.deleteCars(ids));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> statsCars(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String illegalType) {
        return ResponseEntity.ok(carListService.statsCars(plateNumber, vehicleType, illegalType));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCarsCsv(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String illegalType) {
        List<com.example.demo.entity.CarList> list = carListService.listCarsAll(plateNumber, vehicleType, illegalType);
        StringBuilder csv = new StringBuilder();
        csv.append("ID,车牌号码,车牌颜色,车辆类型,置信度,违规类型,图片,记录时间\n");
        for (com.example.demo.entity.CarList item : list) {
            csv.append(escapeCsv(String.valueOf(item.getId()))).append(',')
                    .append(escapeCsv(item.getPlateNumber())).append(',')
                    .append(escapeCsv(item.getPlateColor())).append(',')
                    .append(escapeCsv(item.getVehicleType())).append(',')
                    .append(escapeCsv(item.getConfidence() == null ? "" : item.getConfidence().toString())).append(',')
                    .append(escapeCsv(item.getIllegalType())).append(',')
                    .append(escapeCsv(item.getImagePath())).append(',')
                    .append(escapeCsv(item.getCreatedAt() == null ? "" : item.getCreatedAt().toString()))
                    .append('\n');
        }
        String filename = "cars-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv";
        byte[] bytes = ("\uFEFF" + csv.toString()).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
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
