package com.media.share.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.FileSystemException;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(FileSystemException.class)
    public ResponseEntity<String> handleFileSystemException(FileSystemException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File system error occurred");
    }
}
