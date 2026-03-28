package com.example.demo.controller;

import com.example.demo.service.BaiduOCRService;
import com.example.demo.service.ProcessDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/process")
@CrossOrigin(origins = "*")
public class ProcessDetailController {

    @Autowired
    private ProcessDetailService processDetailService;

    @Autowired
    private BaiduOCRService baiduOCRService;


    /**
     * 获取处理详情（只返回基本信息）
     */
    @GetMapping("/detail/{jobId}")
    public Map<String, Object> getProcessDetail(@PathVariable String jobId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> detail = new HashMap<>();
            detail.put("jobId", jobId);
            detail.put("captureCount", processDetailService.getAllCaptures().size());
            result.put("success", true);
            result.put("data", detail);
            result.put("message", "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取处理详情失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有抓拍图片列表
     */
    @GetMapping("/captures/all")
    public Map<String, Object> getAllCaptures(@RequestParam(required = false) String type) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> captures = (type == null || type.trim().isEmpty())
                    ? processDetailService.getAllCaptures()
                    : processDetailService.getCapturesByType(type.trim());
            result.put("success", true);
            result.put("data", captures);
            result.put("message", "获取成功");
            result.put("count", captures.size());
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取抓拍列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 批量车牌识别接口
     * 对应前端调用的 /api/batch_recognize_plates
     */
    @PostMapping("/recognize_plates")
    public Map<String, Object> batchRecognizePlates(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String jobId = (String) request.get("job_id");
            List<String> images = (List<String>) request.get("image_urls");
            List<String> vehicleIds = (List<String>) request.get("vehicle_ids");
            String imageType = (String) request.get("image_type");

            if (images == null || images.isEmpty()) {
                response.put("success", false);
                response.put("message", "图片列表不能为空");
                return response;
            }

            System.out.println("开始批量车牌识别，任务ID: " + jobId);
            System.out.println("图片数量: " + images.size());
            System.out.println("图片类型: " + imageType);
            // 根据类型调用不同方法
            Map<String, Object> result = baiduOCRService.batchRecognizeBase64(jobId, images.toArray(new String[0]), vehicleIds.toArray(new String[0]));
            response.putAll(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器处理错误: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    /**
     * 根据任务ID获取抓拍图片
     */
    @GetMapping("/captures/{jobId}")
    public Map<String, Object> getCapturesByJob(@PathVariable String jobId,
                                                @RequestParam(required = false) String type) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> captures = (type == null || type.trim().isEmpty())
                    ? processDetailService.getCapturesByJobId(jobId)
                    : processDetailService.getCapturesByJobId(jobId, type.trim());
            result.put("success", true);
            result.put("data", captures);
            result.put("message", "获取成功");
            result.put("count", captures.size());
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取任务抓拍失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取图片文件（Spring方式）
     */
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImageFile(@PathVariable String filename,
                                                 @RequestParam(required = false) String type) {
        try {
            File imageFile = processDetailService.getImageFile(filename, type);
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(imageFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除单个图片
     */
    @DeleteMapping("/capture/{filename}")
    public Map<String, Object> deleteCapture(@PathVariable String filename) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleted = processDetailService.deleteCapture(filename);
            if (deleted) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，文件不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 批量删除图片
     */
    @PostMapping("/captures/batch-delete")
    public Map<String, Object> batchDeleteCaptures(@RequestBody List<String> filenames) {
        Map<String, Object> result = new HashMap<>();
        try {
            int deletedCount = processDetailService.batchDeleteCaptures(filenames);
            result.put("success", true);
            result.put("message", "成功删除 " + deletedCount + " 个文件");
            result.put("deletedCount", deletedCount);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取抓拍目录信息
     */
    @GetMapping("/capture-dir/info")
    public Map<String, Object> getCaptureDirInfo() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> info = processDetailService.getCaptureDirInfo();
            result.put("success", true);
            result.put("data", info);
            result.put("message", "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取目录信息失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 保存处理后车辆数据
     */
    @PostMapping("/save-data")
    public Map<String, Object> saveProcessData(@RequestBody Map<String, Object> requestData) {
        return processDetailService.saveProcessData(requestData);
    }
}