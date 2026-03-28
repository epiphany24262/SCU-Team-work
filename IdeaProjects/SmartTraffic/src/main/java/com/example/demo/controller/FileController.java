package com.example.demo.controller;

import com.example.demo.entity.FileInfo;
import com.example.demo.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        fileService.init();
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            FileInfo fileInfo = fileService.uploadFile(file);

            response.put("success", true);
            response.put("message", "上传成功");
            response.put("file", fileInfo);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "上传失败: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/uploads/{subDir}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String subDir,
                                                 @PathVariable String filename) {
        try {
            java.nio.file.Path filePath = fileService.getFilePath(subDir, filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = fileService.deleteFile(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "文件不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> getFiles() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", fileService.getAllFiles());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取文件列表失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshFiles() {
        Map<String, Object> response = new HashMap<>();
        try {
            fileService.refreshFiles();
            response.put("success", true);
            response.put("message", "刷新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刷新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}