package com.example.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class BaiduOCRService {
    @Value("${baidu.ocr.api-key}")
    private String apiKey;

    @Value("${baidu.ocr.secret-key}")
    private String secretKey;

    @Value("${baidu.ocr.connect-timeout}")
    private int connectTimeout;

    @Value("${baidu.ocr.read-timeout}")
    private int readTimeout;

    private String accessToken;
    private long tokenExpireTime;


    // Service中新增batchRecognizeBase64方法
    public Map<String, Object> batchRecognizeBase64(String jobId, String[] base64Images, String[] originalVehicleIds) {
        Map<String, Object> result = new HashMap<>();
        JSONArray results = new JSONArray();
        int successCount = 0;
        int failureCount = 0;

        try {
            // 确保有访问令牌
            ensureAccessToken();

            for (int i = 0; i < base64Images.length; i++) {
                String base64Image = base64Images[i];
                try {
                    // 直接调用Base64识别方法
                    JSONObject plateResult = recognizeLicensePlate(base64Image);

                    if (plateResult.getBooleanValue("success")) {
                        successCount++;

                        // 构建返回结果
                        JSONObject item = new JSONObject();
                        item.put("id", UUID.randomUUID().toString());
                        item.put("vehicleId", originalVehicleIds[i]);
                        item.put("url", "base64_image_" + i); // Base64没有URL
                        item.put("plateNumber", plateResult.getString("licensePlate"));
                        item.put("plateColor", plateResult.getString("plateColor"));
                        item.put("confidence", plateResult.getDouble("confidence"));
                        item.put("vehicleType", plateResult.getString("vehicleType"));
                        results.add(item);
                    } else {
                        failureCount++;
                        System.err.println("Base64识别失败: " + plateResult.getString("message"));
                    }
                    if (i < base64Images.length - 1) {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    failureCount++;
                    System.err.println("处理Base64图片出错: " + e.getMessage());
                    Thread.sleep(1000);
                }
            }

            result.put("success", true);
            result.put("job_id", jobId);
            result.put("total", base64Images.length);
            result.put("success_count", successCount);
            result.put("failure_count", failureCount);
            result.put("results", results);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量识别失败: " + e.getMessage());
        }

        return result;
    }

    // 新增recognizeLicensePlate方法
    private JSONObject recognizeLicensePlate(String base64Image) throws Exception {
        JSONObject result = new JSONObject();
        result.put("success", false);

        try {
            // 直接使用Base64，不用下载图片
            String params = "image=" + URLEncoder.encode(base64Image, StandardCharsets.UTF_8.name())
                    + "&multi_detect=false&multi_scale=false&detect_complete=true";

            String requestUrl = String.format("https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate?access_token=%s",
                    accessToken);

            String responseBody = httpPost(requestUrl, params);

            if (responseBody != null && !responseBody.isEmpty()) {
                JSONObject jsonResponse = JSONObject.parseObject(responseBody);

                if (jsonResponse.containsKey("error_code")) {
                    throw new Exception("百度OCR API错误: " + jsonResponse.getString("error_msg")
                            + " (错误码: " + jsonResponse.getString("error_code") + ")");
                }

                JSONObject wordsResult = jsonResponse.getJSONObject("words_result");
                if (wordsResult != null && wordsResult.containsKey("number")) {
                    result.put("licensePlate", wordsResult.getString("number"));
                    result.put("plateColor", convertPlateColor(wordsResult.getString("color")));

                    // 计算置信度
                    double confidence = 0.0;
                    if (wordsResult.containsKey("probability")) {
                        JSONArray probabilityArray = wordsResult.getJSONArray("probability");
                        if (probabilityArray != null && probabilityArray.size() > 0) {
                            double sum = 0.0;
                            for (int i = 0; i < probabilityArray.size(); i++) {
                                sum += probabilityArray.getDoubleValue(i);
                            }
                            confidence = (sum / probabilityArray.size()) * 100.0;
                        }
                    }
                    result.put("confidence", Math.round(confidence * 10.0) / 10.0);

                    result.put("vehicleType", extractVehicleType(wordsResult));
                    result.put("success", true);

                } else {
                    result.put("message", "未识别到车牌");
                    fillDefaultResult(result);
                }
            }

        } catch (Exception e) {
            result.put("message", e.getMessage());
            fillDefaultResult(result);
        }

        return result;
    }

    /**
     * 确保访问令牌有效
     */
    private synchronized void ensureAccessToken() throws Exception {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpireTime) {
            System.out.println("获取/刷新百度访问令牌...");
            refreshAccessToken();
        }
    }

    /**
     * 刷新访问令牌
     */
    private void refreshAccessToken() throws Exception {
        String authUrl = String.format("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s",
                apiKey, secretKey);

        String responseBody = httpPost(authUrl, "");

        if (responseBody != null && !responseBody.isEmpty()) {
            JSONObject jsonResult = JSONObject.parseObject(responseBody);
            if (jsonResult.containsKey("error")) {
                throw new Exception("获取百度访问令牌失败: " + jsonResult.getString("error_description"));
            } else {
                this.accessToken = jsonResult.getString("access_token");
                int expiresIn = jsonResult.getIntValue("expires_in");
                this.tokenExpireTime = System.currentTimeMillis() + (long) ((expiresIn - 300) * 1000);
                System.out.println("百度访问令牌获取成功，有效期至: " + tokenExpireTime);
            }
        } else {
            throw new Exception("获取访问令牌响应为空");
        }
    }

    /**
     * HTTP POST请求
     */
    private String httpPost(String url, String params) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStreamWriter out = null;

        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            if (params != null && !params.isEmpty()) {
                out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                out.write(params);
                out.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                String errorResponse = readErrorStream(connection);
                throw new IOException("HTTP请求失败，响应码: " + responseCode
                        + ", 响应消息: " + connection.getResponseMessage()
                        + ", 错误详情: " + errorResponse);
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            return responseBuilder.toString();

        } finally {
            if (out != null) {
                try { out.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 读取错误流
     */
    private String readErrorStream(HttpURLConnection connection) {
        if (connection == null) return "";

        BufferedReader errorReader = null;
        try {
            errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorBuilder = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorBuilder.append(line);
            }
            return errorBuilder.toString();
        } catch (IOException e) {
            return "无法读取错误信息: " + e.getMessage();
        } finally {
            if (errorReader != null) {
                try { errorReader.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * 下载图片
     */
    private byte[] downloadImage(String imageUrl) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("下载图片失败，HTTP状态码: " + responseCode);
            }

            inputStream = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();

        } finally {
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            if (outputStream != null) {
                try { outputStream.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 车牌颜色转换
     */
    private String convertPlateColor(String color) {
        if (color == null) return "未知";

        switch (color.toLowerCase()) {
            case "blue": return "蓝色";
            case "yellow": return "黄色";
            case "black": return "黑色";
            case "white": return "白色";
            case "green": return "绿色";
            default: return color;
        }
    }

    /**
     * 提取车辆类型
     */
    private String extractVehicleType(JSONObject plateInfo) {
        String color = plateInfo.getString("color");
        if (color == null) return "未知";

        if ("yellow".equalsIgnoreCase(color)) {
            return "大型车辆";
        } else if ("green".equalsIgnoreCase(color)) {
            return "新能源车辆";
        } else {
            return "小型车辆";
        }
    }

    /**
     * 填充默认结果
     */
    private void fillDefaultResult(JSONObject result) {
        result.put("licensePlate", "未识别");
        result.put("plateColor", "未知");
        result.put("vehicleType", "未知");
        result.put("confidence", 0.0);
    }
}