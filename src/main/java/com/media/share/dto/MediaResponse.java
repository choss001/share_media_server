package com.media.share.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MediaResponse {


    private Long id;
    private String fileName;

    private String filePath;

    private String thumbnailName;

    private String thumbnailPath;
    private String fileType;
    private byte[] image;


    private LocalDateTime uploadDate;
    private Character publicYn;
    // Getters and setters

}
