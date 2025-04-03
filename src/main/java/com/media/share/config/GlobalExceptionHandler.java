package com.media.share.config;

import com.github.kokorin.jaffree.nut.StreamHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.FileSystemException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileSystemException.class)
    public ResponseEntity<String> handleFileSystemException(FileSystemException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File system error occurred");
    }
    @ExceptionHandler(VideoProcessingException.class)
    public ResponseEntity<String> handleVideoProcessingException(VideoProcessingException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());

    }
}
