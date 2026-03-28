package com.example.demo.service;

import com.example.demo.entity.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FileService {

    @Value("${upload.path}")
    private String uploadPath;

    private final Map<Long, FileInfo> fileStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * 初始化服务，扫描文件系统
     */
    public void init() {
        createDirectories();
        scanFileSystem();
        System.out.println("文件服务初始化完成，共加载 " + fileStore.size() + " 个文件");
    }

    /**
     * 创建必要的目录
     */
    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(uploadPath, "video"));
            Files.createDirectories(Paths.get(uploadPath, "image"));
            System.out.println("创建目录: " + Paths.get(uploadPath, "video").toAbsolutePath());
            System.out.println("创建目录: " + Paths.get(uploadPath, "image").toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("创建目录失败", e);
        }
    }

    /**
     * 扫描文件系统
     */
    private void scanFileSystem() {
        scanDirectory("video");
        scanDirectory("image");
    }

    /**
     * 扫描指定类型的目录
     */
    private void scanDirectory(String fileType) {
        // 修复：使用正确的路径分隔符
        File directory = Paths.get(uploadPath, fileType).toFile();
        System.out.println("扫描目录: " + directory.getAbsolutePath());

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("目录不存在: " + directory.getAbsolutePath());
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("目录为空: " + directory.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isFile() && isMediaFile(file, fileType)) {
                addFileToStore(file, fileType);
            }
        }
    }

    /**
     * 添加文件到存储
     */
    private void addFileToStore(File file, String fileType) {
        // 检查是否已存在
        boolean exists = fileStore.values().stream()
                .anyMatch(info -> info.getName().equals(file.getName()));

        if (!exists) {
            Long fileId = idCounter.getAndIncrement();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(fileId);
            fileInfo.setName(file.getName());
            fileInfo.setUrl("/uploads/" + fileType + "/" + file.getName());
            fileInfo.setType(fileType);
            fileInfo.setSize(file.length());
            fileInfo.setLastModified(LocalDateTime.now());

            fileStore.put(fileId, fileInfo);
            System.out.println("添加文件到存储: " + file.getName());
        }
    }

    /**
     * 判断是否为媒体文件
     */
    private boolean isMediaFile(File file, String expectedType) {
        String fileName = file.getName().toLowerCase();
        if ("video".equals(expectedType)) {
            return fileName.endsWith(".mp4") || fileName.endsWith(".avi") ||
                    fileName.endsWith(".mov") || fileName.endsWith(".webm") ||
                    fileName.endsWith(".mkv");
        } else if ("image".equals(expectedType)) {
            return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                    fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                    fileName.endsWith(".bmp") || fileName.endsWith(".webp");
        }
        return false;
    }

    /**
     * 上传文件
     */
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("video/") && !contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("只能上传视频或图片文件");
        }

        // 确定子目录
        String subDir = contentType.startsWith("video/") ? "video" : "image";

        // 修复：使用 Paths.get 来构建路径，避免字符串拼接问题
        Path uploadDirPath = Paths.get(uploadPath, subDir);

        // 确保目录存在
        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        // 生成文件名 - 使用原始文件名但要处理重名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 检查文件是否已存在，如果存在则在文件名后添加数字
        String baseName = originalFilename != null ?
                originalFilename.substring(0, originalFilename.lastIndexOf(".")) : "file";
        String newFilename = originalFilename != null ? originalFilename : "file" + fileExtension;
        int counter = 1;

        while (Files.exists(uploadDirPath.resolve(newFilename))) {
            newFilename = baseName + "(" + counter + ")" + fileExtension;
            counter++;
        }

        // 保存文件
        Path destPath = uploadDirPath.resolve(newFilename);
        System.out.println("保存文件到: " + destPath.toAbsolutePath());
        file.transferTo(destPath.toFile());

        // 创建文件信息
        Long fileId = idCounter.getAndIncrement();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(fileId);
        fileInfo.setName(newFilename);  // 使用实际保存的文件名
        fileInfo.setUrl("/uploads/" + subDir + "/" + newFilename);
        fileInfo.setType(subDir);
        fileInfo.setSize(file.getSize());

        // 存储文件信息
        fileStore.put(fileId, fileInfo);

        return fileInfo;
    }

    /**
     * 获取所有文件
     */
    public List<FileInfo> getAllFiles() {
        return new ArrayList<>(fileStore.values());
    }

    /**
     * 根据ID获取文件
     */
    public FileInfo getFileById(Long id) {
        return fileStore.get(id);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(Long id) {
        FileInfo fileInfo = fileStore.get(id);
        if (fileInfo == null) return false;

        // 删除物理文件
        String urlPath = fileInfo.getUrl().replace("/uploads/", "");
        File file = Paths.get(uploadPath, urlPath).toFile();
        if (file.exists()) {
            file.delete();
        }

        // 删除存储记录
        fileStore.remove(id);
        return true;
    }

    /**
     * 刷新文件列表
     */
    public void refreshFiles() {
        fileStore.clear();
        scanFileSystem();
    }

    /**
     * 获取文件路径
     */
    public Path getFilePath(String subDir, String filename) {
        if (!"video".equals(subDir) && !"image".equals(subDir)) {
            throw new IllegalArgumentException("无效的子目录");
        }
        return Paths.get(uploadPath, subDir, filename).normalize();
    }
}