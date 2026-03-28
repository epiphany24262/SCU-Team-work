package com.example.demo.service;

import com.example.demo.entity.CarInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.demo.dao.ProcessDetailDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProcessDetailService {

    @Value("${capture.image.dir}")
    private String captureImageDir;

    @Value("${capture.image.dir.line_press:}")
    private String captureLinePressDir;

    @Value("${capture.image.dir.illegal_parking:}")
    private String captureIllegalParkingDir;

    @Value("${car.image.save.dir}")
    private String carImageSaveDir;

    @Autowired
    private ProcessDetailDao processDetailDao;

    /**
     * 获取所有抓拍图片
     */
    public List<Map<String, Object>> getAllCaptures() {
        return getCapturesByType("wrong_way");
    }

    public List<Map<String, Object>> getCapturesByType(String type) {
        List<Map<String, Object>> captures = new ArrayList<>();
        String dirPath = resolveCaptureDir(type);
        if (dirPath == null || dirPath.trim().isEmpty()) {
            return captures;
        }

        try {
            File captureDir = new File(dirPath);
            if (captureDir.exists() && captureDir.isDirectory()) {
                File[] imageFiles = captureDir.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jpg") ||
                                name.toLowerCase().endsWith(".jpeg") ||
                                name.toLowerCase().endsWith(".png")
                );

                if (imageFiles != null) {
                    Arrays.sort(imageFiles, new Comparator<File>() {
                        @Override
                        public int compare(File f1, File f2) {
                            return Long.compare(f2.lastModified(), f1.lastModified());
                        }
                    });

                    for (File imageFile : imageFiles) {
                        captures.add(createCaptureInfo(imageFile, type));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return captures;
    }

    /**
     * 根据任务ID获取抓拍图片（目前返回所有图片，可按需过滤）
     */
    public List<Map<String, Object>> getCapturesByJobId(String jobId) {
        // 这里可以根据jobId过滤，目前返回所有
        return getAllCaptures();
    }

    public List<Map<String, Object>> getCapturesByJobId(String jobId, String type) {
        return getCapturesByType(type);
    }

    /**
     * 获取图片文件
     */
    public File getImageFile(String filename) {
        return new File(captureImageDir + File.separator + filename);
    }

    public File getImageFile(String filename, String type) {
        String dirPath = resolveCaptureDir(type);
        if (dirPath == null || dirPath.trim().isEmpty()) {
            dirPath = captureImageDir;
        }
        return new File(dirPath + File.separator + filename);
    }

    /**
     * 删除单个图片
     */
    public boolean deleteCapture(String filename) {
        try {
            File imageFile = getImageFile(filename);
            if (imageFile.exists()) {
                return imageFile.delete();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量删除图片
     */
    public int batchDeleteCaptures(List<String> filenames) {
        int deletedCount = 0;

        try {
            for (String filename : filenames) {
                if (deleteCapture(filename)) {
                    deletedCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deletedCount;
    }

    /**
     * 获取抓拍目录信息
     */
    public Map<String, Object> getCaptureDirInfo() {
        Map<String, Object> info = new HashMap<>();

        try {
            File captureDir = new File(captureImageDir);

            info.put("path", captureImageDir);
            info.put("exists", captureDir.exists());
            info.put("isDirectory", captureDir.isDirectory());

            if (captureDir.exists() && captureDir.isDirectory()) {
                File[] files = captureDir.listFiles();
                info.put("totalFiles", files != null ? files.length : 0);

                // 统计图片文件
                File[] imageFiles = captureDir.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jpg") ||
                                name.toLowerCase().endsWith(".jpeg") ||
                                name.toLowerCase().endsWith(".png")
                );
                info.put("imageCount", imageFiles != null ? imageFiles.length : 0);
                info.put("freeSpace", captureDir.getFreeSpace());
                info.put("totalSpace", captureDir.getTotalSpace());
                info.put("usableSpace", captureDir.getUsableSpace());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    /**
     * 保存处理数据（新增方法）
     */
    public Map<String, Object> saveProcessData(Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();

        try {
            String jobId = (String) requestData.get("job_id");
            String violationType = (String) requestData.get("violation_type");
            List<Map<String, Object>> captures = (List<Map<String, Object>>) requestData.get("captures");

            System.out.println("保存数量: " + (captures != null ? captures.size() : 0));

            // 保存车辆信息
            int carCount = 0;
            List<CarInfo> savedCars = new ArrayList<>();

            if (captures != null && !captures.isEmpty()) {
                for (Map<String, Object> capture : captures) {
                    // 只保存有车牌号的数据
                    if (capture.get("plateNumber") != null &&
                            !((String) capture.get("plateNumber")).isEmpty()) {

                        // 创建 CarInfo 对象
                        CarInfo carInfo = new CarInfo();
                        carInfo.setPlateNumber((String) capture.get("plateNumber"));
                        carInfo.setPlateColor((String) capture.getOrDefault("plateColor", "蓝色"));
                        carInfo.setVehicleType((String) capture.getOrDefault("vehicleType", "小型汽车"));

                        Object confidenceObj = capture.get("confidence");
                        if (confidenceObj != null) {
                            if (confidenceObj instanceof Number) {
                                carInfo.setConfidence(((Number) confidenceObj).doubleValue());
                            }
                        } else {
                            carInfo.setConfidence(0.0);
                        }

                        // 1. 先获取Base64图片数据
                        String base64Image = (String) capture.get("imageData");
                        if (base64Image == null || base64Image.isEmpty()) {
                            System.out.println("警告: 图片数据为空，跳过保存车牌: " + capture.get("plateNumber"));
                            continue;
                        }

                        // 2. 生成图片名，并与保存目录安全拼接
                        String imageName = generateImageName(jobId, capture);
                        String imagePath = new File(carImageSaveDir, imageName).getAbsolutePath();

                        // 3. 保存图片到本地
                        boolean imageSaved = saveBase64Image(base64Image, imagePath);
                        if (!imageSaved) {
                            System.out.println("警告: 保存图片失败，跳过保存车牌: " + capture.get("plateNumber"));
                            continue;
                        }

                        carInfo.setImagePath(imagePath);
                        carInfo.setImageName(imageName);
                        carInfo.setDetectionTime(new Timestamp(System.currentTimeMillis()));
                        carInfo.setCameraId(jobId);

                        // 4. 保存到数据库
                        int saved = processDetailDao.saveCarInfo(carInfo, violationType);
                        if (saved > 0) {
                            carCount++;
                            savedCars.add(carInfo);
                            System.out.println("保存图片到数据库成功");
                        }
                    }
                }
            }

            // 构建返回结果
            result.put("success", true);
            result.put("message", String.format("保存成功！保存了 %d 个车牌信息", carCount));

            Map<String, Object> data = new HashMap<>();
            data.put("jobId", jobId);
            data.put("carCount", carCount);
            data.put("captureCount", captures != null ? captures.size() : 0);
            data.put("savedCars", savedCars);

            result.put("data", data);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 保存Base64图片到本地
     */
    private boolean saveBase64Image(String base64Data, String filePath) {
        try {
            // 确保目录存在
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 解码Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 保存文件
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
                fos.flush();
            }

            System.out.println("图片保存到本地成功: " + filePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("保存图片失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 生成图片文件名
     */
    private String generateImageName(String jobId, Map<String, Object> capture) {
        String plateNumber = (String) capture.get("plateNumber");

        // 只移除空格，保留其他所有字符（包括汉字）
        String safePlate = "unknown";
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            safePlate = plateNumber.trim()
                    .replaceAll("\\s+", "");  // 只移除空格
        }

        // 获取当前时间戳（格式：yyyyMMddHHmmssSSS）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = sdf.format(new Date());

        // 格式：车牌_时间戳.jpg
        String name = String.format("%s_%s.jpg",
                safePlate,
                timestamp
        );

        return name;
    }

    /**
     * 创建图片信息
     */
    private Map<String, Object> createCaptureInfo(File imageFile, String type) {
        Map<String, Object> capture = new HashMap<>();
        String filename = imageFile.getName();
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));

        capture.put("id", filename.hashCode());
        capture.put("filename", filename);
        capture.put("url", "/api/process/image/" + filename + "?type=" + (type == null ? "" : type));
        capture.put("violationType", type);
        capture.put("fullPath", imageFile.getAbsolutePath());
        capture.put("size", formatFileSize(imageFile.length()));
        capture.put("lastModified", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(imageFile.lastModified())));

        // 从文件名解析车辆ID和时间戳
        // 假设文件名格式: 20250123_143022_id5.jpg
        if (nameWithoutExt.contains("_id")) {
            String[] parts = nameWithoutExt.split("_id");
            if (parts.length > 1) {
                capture.put("timestamp", parts[0]);
                capture.put("vehicleId", "id" + parts[1]);
            }
        }

        capture.put("selected", false);
        capture.put("plateNumber", "");
        capture.put("confidence", 0.0);

        return capture;
    }

    private String resolveCaptureDir(String type) {
        if (type == null) {
            return captureImageDir;
        }
        switch (type) {
            case "line_press":
                return (captureLinePressDir == null || captureLinePressDir.trim().isEmpty())
                        ? captureImageDir
                        : captureLinePressDir;
            case "illegal_parking":
                return (captureIllegalParkingDir == null || captureIllegalParkingDir.trim().isEmpty())
                        ? captureImageDir
                        : captureIllegalParkingDir;
            case "wrong_way":
            default:
                return captureImageDir;
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}