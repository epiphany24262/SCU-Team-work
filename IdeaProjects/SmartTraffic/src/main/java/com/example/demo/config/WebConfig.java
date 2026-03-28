package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = uploadPath;
        if (basePath != null && !basePath.endsWith("/") && !basePath.endsWith("\\")) {
            basePath = basePath + "/";
        }
        // 配置视频文件静态资源
        registry.addResourceHandler("/uploads/video/**")
            .addResourceLocations("file:" + basePath + "video/");

        // 配置图片文件静态资源
        registry.addResourceHandler("/uploads/image/**")
            .addResourceLocations("file:" + basePath + "image/");
    }
}