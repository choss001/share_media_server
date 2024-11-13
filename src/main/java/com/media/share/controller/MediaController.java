package com.media.share.controller;

import com.media.share.dto.MediaFileDto;
import com.media.share.model.MediaFile;
import com.media.share.repository.MediaFileRepository;
import com.media.share.service.MakeThumbNail;
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
import java.util.Optional;
import java.util.UUID;


@RestController
public class MediaController {

    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Value("${spring.application.name}")
    private String name;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private MakeThumbNail makeThumbNail;


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

        Optional<MediaFile> mediaFile = mediaFileRepository.findById(videoId);
        if (mediaFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }
        String filePath = mediaFile.get().getFilePath();
        //File file = ResourceUtils.getFile("classpath:static/test.mp4");
        File file = ResourceUtils.getFile(filePath);
        Path videoPath = Paths.get(file.getPath());

//        Resource videoResource = new UrlResource(videoPath.toUri());
        Resource videoResource = new UrlResource(videoPath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(videoResource);
    }

    private final String UPLOAD_DIR = "C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp"; // Set your directory path here

    @PostMapping("/upload/media")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {

            // Save and move the file
            Path filePath = Paths.get(UPLOAD_DIR, UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
            Files.createDirectories(filePath.getParent()); // Ensure directory exists
            file.transferTo(filePath.toFile());

            //make thumbnail
            MediaFileDto mediaFileDto = makeThumbNail.doMake(file);
            // Generate a download URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(file.getOriginalFilename())
                    .toUriString();

            // file save to table
            mediaFileDto.setFileName(file.getOriginalFilename());
            mediaFileDto.setFilePath(filePath.toString());
            mediaFileDto.setFileType(file.getContentType());
            mediaFileDto.setUploadDate(java.time.LocalDateTime.now());

            MediaFile mediaFile = new MediaFile(mediaFileDto);
            mediaFileRepository.save(mediaFile);



            return ResponseEntity.ok(fileDownloadUri);

        } catch (IOException e) {
            logger.info(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
