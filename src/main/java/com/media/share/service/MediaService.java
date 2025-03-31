package com.media.share.service;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.media.share.dto.MediaFileDto;
import com.media.share.model.MediaFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class MediaService implements IMediaService{


    @Value("${file.path}")
    private String filePathBase;


    @Override
    public String upload(MediaFileDto mediaFileDto, MultipartFile file) {
        try {
            // 1️⃣ Save uploaded file
            File uploadedFile = new File(filePathBase + file.getOriginalFilename());
//            file.transferTo(uploadedFile);

            // 2️⃣ Convert to HLS
            String hlsFileName = convertToHLS(uploadedFile, mediaFileDto);

            // 3️⃣ Return HLS URL
//            return ResponseEntity.ok("/hls/" + hlsFileName);

        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
            return null;
        }
        return null;
    }

    private String convertToHLS(File file, MediaFileDto mediaFileDto) {
        String outputFileName = file.getName().replace(".mp4", ".m3u8");
        String outputFilePath = filePathBase+ "/hls/" + outputFileName;
        Path path = Path.of(mediaFileDto.getFilePath());

        // Convert using Jaffree (FFmpeg)
        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(path))
                .addOutput(UrlOutput.toPath(new File(outputFilePath).toPath())
                        .setFormat("hls")
                        .addArguments("-hls_time", "10")  // 10s chunks
                        .addArguments("-hls_list_size", "0") // Unlimited playlist
                )
                .execute();

        return outputFileName;
    }
}
