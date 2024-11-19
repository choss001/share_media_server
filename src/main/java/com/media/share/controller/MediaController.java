package com.media.share.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.media.share.dto.MediaFileDto;
import com.media.share.dto.MediaResponse;
import com.media.share.model.MediaFile;
import com.media.share.repository.MediaFileRepository;
import com.media.share.service.MakeThumbNail;
import com.media.share.service.ThumbnailCacheService;
import org.jcodec.common.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
public class MediaController {

    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Value("${spring.application.name}")
    private String name;

    @Value("${file.path}")
    private String filePathBase;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private ThumbnailCacheService thumbnailCacheService;

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


    private final Cache<Long, byte[]> thumbnailCache = Caffeine.newBuilder()
            .maximumSize(100) // Limit cache size
            .expireAfterWrite(Duration.ofMinutes(10)) // Cache expiry
            .build();

    @GetMapping("/mediaList")
    public ResponseEntity<List<MediaResponse>> getList() {

        List<MediaResponse> responseList = mediaFileRepository.findAll().stream().filter(mediaFile -> mediaFile.getDeleteYn() == 'N').map(mediaFile -> {
            MediaResponse response = new MediaResponse();
            response.setId(mediaFile.getId());
            response.setFileName(mediaFile.getFileName());
            response.setFilePath(mediaFile.getFilePath());
            response.setFileType(mediaFile.getFileType());
            response.setUploadDate(mediaFile.getUploadDate());
            response.setThumbnailName(mediaFile.getThumbnail_name());
            response.setThumbnailPath(mediaFile.getThumbnailPath());
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok()
                .body(responseList);
    }

    @GetMapping(
            value = "/image/{id}",
            produces = MediaType.IMAGE_PNG_VALUE
            )
    public @ResponseBody byte[] getImage(@PathVariable("id") Long id) throws IOException{
        Optional<MediaFile> byId = mediaFileRepository.findById(id);

        InputStream in = new FileInputStream(byId.get().getThumbnailPath());
        return IOUtils.toByteArray(in);
    }

    @GetMapping("/stream/test/{videoId}")
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


    @PostMapping("/upload/media")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {

            // Save and move the file
            Path filePath = Paths.get(filePathBase, UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
            Files.createDirectories(filePath.getParent()); // Ensure directory exists
            file.transferTo(filePath.toFile());

            Optional<MediaFile> byFileNameAndFileSize = mediaFileRepository.findByFileNameAndFileSize(file.getOriginalFilename(), file.getSize());
            if (!byFileNameAndFileSize.isEmpty()) return ResponseEntity.ok("이미 있는 동영상입니다.");

            //make thumbnail
            MediaFileDto mediaFileDto = makeThumbNail.doMake(file, filePath);
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
            mediaFileDto.setFileSize(file.getSize());
            mediaFileDto.setDeleteYn('N');

            MediaFile mediaFile = new MediaFile(mediaFileDto);
            mediaFileRepository.save(mediaFile);



            return ResponseEntity.ok("SUCCESS");

        } catch (IOException e) {
            logger.info(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/refined/size")
    public ResponseEntity<String> refined(){
        List<MediaFile> all = mediaFileRepository.findAll();
        all.forEach(item -> {
           if (item.getFileSize()==null || item.getFileSize() == 0){
               File videoFile = new File(item.getFilePath());
               item.setFileSize(videoFile.length());
               mediaFileRepository.save(item);
           }
        });
        return ResponseEntity.ok("SUCCESS");
    }


    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable("videoId") Long videoId, @RequestHeader(value = "Range", required = false) String range) throws IOException {
        Optional<MediaFile> mediaFile = mediaFileRepository.findById(videoId);
        if (mediaFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }
        String filePath = mediaFile.get().getFilePath();
        File videoFile = new File(filePath);

        long fileLength = videoFile.length();
        long start = 0, end = fileLength - 1;

        // Parse Range header
        if (range != null) {
            String[] ranges = range.replace("bytes=", "").split("-");
            System.out.println("range[0]: "+ranges[0]);
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                System.out.println("range[1] :"+ ranges[1]);
                end = Long.parseLong(ranges[1]);
            }
        }

        long contentLength = end - start + 1;

        InputStream inputStream = new FileInputStream(videoFile);
        inputStream.skip(start);

        return ResponseEntity.status(range != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .header("Content-Type", "video/mp4")
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", String.valueOf(contentLength))
                .header("Content-Range", "bytes " + start + "-" + end + "/" + fileLength)
                .body(new InputStreamResource(inputStream));
    }

    @DeleteMapping("/deleteMedia/{videoId}")
    public  ResponseEntity<String> deleteMedia(@PathVariable("videoId") Long videoId) throws MalformedURLException, FileNotFoundException {

        //db update
        Optional<MediaFile> mediaFile = mediaFileRepository.findById(videoId);
        if(mediaFile.isEmpty())return ResponseEntity.ok().body("not found");
        mediaFile.get().setDeleteYn('Y');
        mediaFileRepository.save(mediaFile.get());

        //delete file
        File fileMedia = new File(mediaFile.get().getFilePath());
        File fileThumbnail = new File(mediaFile.get().getThumbnailPath());

        if (fileMedia.delete() && fileThumbnail.delete()) System.out.println("File deleted successfully");
        else System.out.println("Filed to delete file.");
        return ResponseEntity.ok("Delete success");
    }
}
