package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return upload(file, "images", request);
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return upload(file, "avatars", request);
    }

    private Result<Map<String, String>> upload(MultipartFile file, String subDir, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fail(4001, "文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "unknown";
        String ext = "";
        if (originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            Path savePath = Paths.get(uploadDir, subDir, filename).normalize().toAbsolutePath();
            Files.createDirectories(savePath.getParent());
            file.transferTo(savePath.toFile());
            String url = request.getContextPath() + "/uploads/" + subDir + "/" + filename;
            log.info("file saved: {} -> {} (size={})", originalName, savePath, file.getSize());
            return Result.ok(Map.of("url", url));
        } catch (IOException e) {
            log.error("upload failed: {}", e.getMessage(), e);
            return Result.fail(5001, "文件上传失败: " + e.getMessage());
        }
    }
}
