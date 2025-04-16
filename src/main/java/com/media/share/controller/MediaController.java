package com.media.share.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.media.share.dto.MediaFileDto;
import com.media.share.dto.MediaResponse;
import com.media.share.dto.SignInRequest;
import com.media.share.model.MediaFile;
import com.media.share.model.User;
import com.media.share.repository.MediaFileRepository;
import com.media.share.repository.UserRepository;
import com.media.share.service.MakeThumbNail;
import com.media.share.service.MediaService;
import com.media.share.service.ThumbnailCacheService;
import com.media.share.service.kafka.MediaUploadProducer;
import org.jcodec.common.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class MediaController {

    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Value("${spring.application.name}")
    private String name;

    @Value("${file.path}")
    private String filePathBase;

    @Value("${download.link}")
    private String downloadLinkValue;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private ThumbnailCacheService thumbnailCacheService;

    @Autowired
    private MakeThumbNail makeThumbNail;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private MediaUploadProducer mediaUploadProducer;


    @GetMapping("/test")
    @ResponseBody
    public HashMap<String, String> getTest(Authentication authentication) {
        logger.info(name);
        logger.info(authentication.getName());
        HashMap<String, String> map = new HashMap<>();
        //map.put("key", "Hello from Spring boot!");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((SignInRequest)principal).getUsername();
        map.put("key", username);

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
    public ResponseEntity<List<MediaResponse>> getList(Authentication authentication) {

        List<MediaResponse> responseList = new ArrayList<>();

        Optional<User> user = (authentication != null) ? userRepository.findByUsername(authentication.getName()) : Optional.empty();

        responseList = mediaFileRepository.findAll().stream()
                .filter(mediaFile -> mediaFile.getDeleteYn() == 'N')
                .filter(mediaFile -> {
                    if (mediaFile.getPublicYn() =='Y') return true ;
                    if (user.isEmpty()) return false;
                    return Objects.equals(mediaFile.getUserId(), user.get().getId());
                })
                .map(mediaFile -> {
                    MediaResponse response = new MediaResponse();
                    response.setId(mediaFile.getId());
                    response.setFileName(mediaFile.getFileName());
                    response.setFilePath(mediaFile.getFilePath());
                    response.setFileType(mediaFile.getFileType());
                    response.setUploadDate(mediaFile.getUploadDate());
                    response.setThumbnailName(mediaFile.getThumbnailName());
                    response.setThumbnailPath(mediaFile.getThumbnailPath());
                    response.setPublicYn(mediaFile.getPublicYn());
                    return response;
                })
                .collect(Collectors.toList());


        return ResponseEntity.ok()
                .body(responseList);
    }

    @GetMapping("/hlsMediaList")
    public ResponseEntity<List<MediaResponse>> getPersonalList(Authentication authentication){

        Optional<User> user = (authentication != null) ? userRepository.findByUsername(authentication.getName()) : Optional.empty();

        List<MediaResponse> responseList = mediaFileRepository.findAll()
                .stream()
                .filter(mediaFile -> mediaFile.getDeleteYn() == 'N')
                .filter(mediaFile -> mediaFile.getFilePathHls() != null)
                .filter(mediaFile -> {
                    if (mediaFile.getPublicYn() =='Y') return true ;
                    if (user.isEmpty()) return false;
                    return Objects.equals(mediaFile.getUserId(), user.get().getId());
                })
                .map(mediaFile -> {
            MediaResponse response = new MediaResponse();
            response.setId(mediaFile.getId());
            response.setFileName(mediaFile.getFileName());
            response.setFilePath(mediaFile.getFilePath());
            response.setFileType(mediaFile.getFileType());
            response.setUploadDate(mediaFile.getUploadDate());
            response.setThumbnailName(mediaFile.getThumbnailName());
            response.setThumbnailPath(mediaFile.getThumbnailPath());
            response.setFilePathHls(mediaFile.getFilePathHls());
            response.setPublicYn(mediaFile.getPublicYn());
            response.setFileNameUid(mediaFile.getFileNameUid());
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

    @PostMapping("/upload/file")
    public ResponseEntity<Map<String, Object>> handleFileUpload(@RequestParam("file") MultipartFile file){
        Map<String, Object> response = new HashMap<>();

        if(file.isEmpty()){
            response.put("message", "File is empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Save the file (e.g., to local disk or cloud storage)
            Path filePath = Path.of(filePathBase,"public","attachFile",file.getOriginalFilename());
            File targetFile = new File(filePath.toString());
            Files.createDirectories(filePath.getParent()); // Ensure directory exists
            file.transferTo(targetFile);

            String downloadLink = downloadLinkValue+"attachFile/"+file.getOriginalFilename();
            response.put("message", "File uploaded successfully");
            response.put("filename", file.getOriginalFilename());
            response.put("fileUrl", downloadLink);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("exception ",e);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/upload/media")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file,
                                              @RequestParam("title") String title,
                                              @RequestParam("content") String content,
                                              @RequestParam("publicYn") Character publicYn) {
        try {

            //get id from authentication
            String type = (publicYn == 'Y') ? "public" : "private";
            String principal = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)){
                principal = authentication.getPrincipal().toString();
            }

            Optional<User> user = (principal != null) ? userRepository.findByUsername(principal)
                                                      : userRepository.findByUsername("anonymous");
            if (publicYn == 'N' && user.get().getUsername().equals("anonymous")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication user can have private setting");
            }

            //check whether file is in there
            Optional<MediaFile> byFileNameAndFileSize = mediaFileRepository.findByFileNameAndFileSizeAndDeleteYn(
                    file.getOriginalFilename(),
                    file.getSize(), 'N');
            if (byFileNameAndFileSize.isPresent()) return ResponseEntity.ok("이미 있는 동영상입니다.");


            // Save and move the file
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path mediaFilePath = Paths.get(filePathBase,type,"media", uniqueFileName);
            Files.createDirectories(mediaFilePath.getParent()); // Ensure directory exists
            file.transferTo(mediaFilePath.toFile());

            //make thumbnail
            MediaFileDto mediaFileDto = makeThumbNail.doMake(file, mediaFilePath, type);

            // file save to table
            mediaFileDto.setFileName(file.getOriginalFilename());
            mediaFileDto.setFilePath(mediaFilePath.toString());
            mediaFileDto.setFileType(file.getContentType());
            mediaFileDto.setUploadDate(java.time.LocalDateTime.now());
            mediaFileDto.setFileSize(file.getSize());
            mediaFileDto.setTitle(title);
            mediaFileDto.setContent(content);
            mediaFileDto.setPublicYn(publicYn);
            mediaFileDto.setUserId(user.get().getId());
            mediaFileDto.setDeleteYn('N');
            mediaFileDto.setFileNameUid(uniqueFileName);

            if (user.get().getUsername().equals("anonymous")){
                mediaFileDto.setRequiredRole(1);
            } else {
                mediaFileDto.setRequiredRole(3);
            }

            mediaService.upload(mediaFileDto, file);

            MediaFile mediaFile = new MediaFile(mediaFileDto);
            mediaFileRepository.save(mediaFile);

            //kafka send
            Map<String, Object> metadata = Map.of(
                    "fileNameUid", mediaFile.getFileNameUid()
            );
            String message = new ObjectMapper().writeValueAsString(metadata);
            mediaUploadProducer.sendConversionRequest("media-convert-topic", message);

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
    public ResponseEntity<Resource> streamVideo(@PathVariable("videoId") Long videoId,
                                                @RequestHeader(value = "Range", required = false) String range,
                                                Authentication authentication) throws IOException {
        Optional<MediaFile> mediaFile = mediaFileRepository.findById(videoId);
        if (mediaFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }

        //check whether authenticate or not
        MediaFile file = mediaFile.get();

        boolean isPrivate = file.getPublicYn() == 'N';
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        boolean isOwner = isAuthenticated &&
                userRepository.findByUsername(authentication.getName())
                        .map(user -> Objects.equals(file.getUserId(), user.getId()))
                        .orElse(false);

        if (isPrivate && (!isAuthenticated || !isOwner)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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

    @DeleteMapping("/deleteMedia/{fileNameUid}")
    public  ResponseEntity<String> deleteMedia(@PathVariable("fileNameUid") String fileNameUid) throws IOException {

        //db update
        Optional<MediaFile> mediaFile = mediaFileRepository.findByFileNameUid(fileNameUid);
        if(mediaFile.isEmpty())return ResponseEntity.ok().body("not found");

        //delete file
        File fileMedia = new File(mediaFile.get().getFilePath());
        File fileThumbnail = new File(mediaFile.get().getThumbnailPath());
        File fileHls = new File(mediaFile.get().getFilePathHls());


        Path fileMediaPath = Paths.get(filePathBase,
                "recycleBin", "media", fileMedia.getName());
        Path fileThumbnailPath = Paths.get(filePathBase,
                "recycleBin", "thumbnail", fileThumbnail.getName());

        Path fileHlsPath = Paths.get(filePathBase,
                "recycleBin", "hls", fileHls.getName());

        Files.createDirectories(Paths.get(filePathBase,"recycleBin", "thumbnail"));
        Files.createDirectories(Paths.get(filePathBase,"recycleBin", "media"));
        Files.createDirectories(Paths.get(filePathBase,"recycleBin", "hls"));
        Files.move(Paths.get(fileMedia.getAbsolutePath()),fileMediaPath, StandardCopyOption.REPLACE_EXISTING);
        Files.move(Paths.get(fileThumbnail.getAbsolutePath()),fileThumbnailPath, StandardCopyOption.REPLACE_EXISTING);
        Files.move(Paths.get(fileHls.getAbsolutePath()),fileHlsPath, StandardCopyOption.REPLACE_EXISTING);

        mediaFile.get().setDeleteYn('Y');
        mediaFileRepository.save(mediaFile.get());

//        if (fileMedia.delete() && fileThumbnail.delete()) System.out.println("File deleted successfully");
//        else System.out.println("Filed to delete file.");
        return ResponseEntity.ok("Delete success");
    }

    @GetMapping("/private/{type}/{filename}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> medaiAuthCheck(@PathVariable("type") String type,
                                                   @PathVariable("filename") String filename,
                                                   Authentication authentication) throws IOException {
        Path filePath = Paths.get(filePathBase, "private", type, filename);;
        Resource file = new UrlResource(filePath.toUri());

        String headerType = "video/mp4";
        if ( type.equals("thumbnail")){
            headerType = "image/png";
        }

        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, headerType)
                    .body(file);
        }
        return null;
    }
    @GetMapping("/convert")
    public ResponseEntity<String> convertMedia(@RequestParam("fileName") String fileName){
        String result = mediaService.convertFormat(fileName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/convertAll")
    public ResponseEntity<String> convertMedia(){
        String result = mediaService.convertFormatAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/media/{fileNameUid}")
    public ResponseEntity<MediaFileDto> getMedia(@PathVariable("fileNameUid") String fileNameUid, Authentication authentication){
        Optional<MediaFile> mediaFile = mediaFileRepository.findByFileNameUid(fileNameUid);
        if (mediaFile.isEmpty())
            return ResponseEntity.internalServerError().body(null);

        MediaFileDto mediaFileDto = new MediaFileDto();
        mediaFileDto.setId(mediaFile.get().getId());
        mediaFileDto.setTitle(mediaFile.get().getTitle());
        mediaFileDto.setContent(mediaFile.get().getContent());
        mediaFileDto.setPublicYn(mediaFile.get().getPublicYn());
        if (mediaFile.get().getPublicYn() == 'Y') return ResponseEntity.ok().body(mediaFileDto);

        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Optional<User> user = userRepository.findByUsername(authentication.getName());

        if (Objects.equals(mediaFile.get().getUserId(), user.get().getId())){
            return ResponseEntity.ok().body(mediaFileDto);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
