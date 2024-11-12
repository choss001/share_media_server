package com.media.share.controller;

import com.media.share.model.MediaFile;
import com.media.share.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@RestController
public class MediaController {

    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Value("${spring.application.name}")
    private String name;

    @Autowired
    private MediaFileRepository mediaFileRepository;


    @GetMapping("/test")
    @ResponseBody
    public HashMap<String, String> getTest() {
        logger.info(name);
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "Hello from Spring boot!");

        return map;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from Spring Boot!");
    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable("videoId") Long videoId) throws MalformedURLException, FileNotFoundException {
        //Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        File file = ResourceUtils.getFile("classpath:static/test.mp4");
        Path videoPath = Paths.get(file.getPath());

        Resource videoResource = new UrlResource(videoPath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(videoResource);
    }

    private final String UPLOAD_DIR = "C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp"; // Set your directory path here

    @PostMapping("/upload/media")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file
            Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.createDirectories(filePath.getParent()); // Ensure directory exists
            file.transferTo(filePath.toFile());

            // Generate a download URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(file.getOriginalFilename())
                    .toUriString();

            // file save to table
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileName(file.getOriginalFilename());
            mediaFile.setFilePath(filePath.toString());
            mediaFile.setFileType(file.getContentType());
            mediaFile.setUploadDate(java.time.LocalDateTime.now());

            mediaFileRepository.save(mediaFile);


            return ResponseEntity.ok(fileDownloadUri);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
