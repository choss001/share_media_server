package com.media.share.service;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.media.share.config.VideoProcessingException;
import com.media.share.dto.MediaFileDto;
import com.media.share.model.MediaFile;
import com.media.share.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MediaService implements IMediaService{

    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);
    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${file.path}")
    private String filePathBase;

    @Value("${app.environment}")
    private String environment;


    @Override
    public String upload(MediaFileDto mediaFileDto, MultipartFile file) {
        return null;
    }
    public String convertFormat(String fileName){
        Optional<MediaFile> mediaFileFiltered = mediaFileRepository.findByFilePathHlsIsNullAndDeleteYn('N')
                .stream()
                .filter(mediaFile -> mediaFile.getFileName().equals(fileName)).findFirst();
        if (mediaFileFiltered.isEmpty()){
            return "Not found file name";
        }

        logger.info(mediaFileFiltered.get().getFilePath());
        logger.info(mediaFileFiltered.get().getFileName());
        mediaFileFiltered.get().setFilePathHls(convertMethod(mediaFileFiltered.get()));
        mediaFileRepository.save(mediaFileFiltered.get());
        logger.info("FINISH");
        return "Conversion successful";
    }

    private String convertMethod(MediaFile mediaFile){

        Path outputPath = mediaFile.getPublicYn() == 'Y' ? Paths.get(filePathBase, "public", "media", "hls", mediaFile.getFileNameUid())
                : Paths.get(filePathBase, "private", "media", "hls", mediaFile.getFileNameUid());

        Path videoPath = Paths.get(mediaFile.getFilePath());

        try {

            File file = new File(videoPath.toString());
            System.out.println(file);
            Files.createDirectories(outputPath);

            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%4d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, outputPath, outputPath
            );
            System.out.println(ffmpegCmd);
            ProcessBuilder processBuilder = (environment.equals("prod")) ? new ProcessBuilder("/bin/bash", "-c", ffmpegCmd)
                    : new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);;
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            logger.info("wait for exit");
            if (exit != 0) {
                logger.info("exit != 0!");
                throw new RuntimeException("video processing failed!!");
            }
        }catch(IOException | InterruptedException ex){
            logger.error("Error processing video: {}", ex.getMessage(), ex);
            throw new VideoProcessingException("Error processing video: "+ ex.getMessage());

        }
        logger.info("convert FINISH!");
        return outputPath.toString();
    }

    public String convertFormatAll(){
        mediaFileRepository
                .findByFilePathHlsIsNullAndDeleteYn('N')
                .forEach(mediaFile -> {
                    mediaFile.setFilePathHls(convertMethod(mediaFile));
                    mediaFileRepository.save(mediaFile);
                });
        return "Conversion successful";
    }


}
