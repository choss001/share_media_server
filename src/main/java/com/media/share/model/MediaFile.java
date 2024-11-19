package com.media.share.model;


import com.media.share.dto.MediaFileDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MediaFile {

    public MediaFile(){ }

    public MediaFile(MediaFileDto mediaFileDto){
        setThumbnail_name(mediaFileDto.getThumbnail_name());
        setThumbnail_path(mediaFileDto.getThumbnail_path());
        setFileType(mediaFileDto.getFileType());
        setFileName(mediaFileDto.getFileName());
        setUploadDate(mediaFileDto.getUploadDate());
        setFilePath(mediaFileDto.getFilePath());
        setDeleteYn(mediaFileDto.getDeleteYn());
        setFileSize(mediaFileDto.getFileSize());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;


    @Column(nullable = false)
    private Long fileSize;


    public Character getDeleteYn() {
        return deleteYn;
    }

    public void setDeleteYn(Character deleteYn) {
        this.deleteYn = deleteYn;
    }

    @Column(nullable = false)
    private Character deleteYn;


    private String thumbnail_name;

    private String thumbnail_path;
    private String fileType;

    private LocalDateTime uploadDate;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }


    public String getThumbnail_name() {
        return thumbnail_name;
    }

    public void setThumbnail_name(String thumbnail_name) {
        this.thumbnail_name = thumbnail_name;
    }

    public String getThumbnailPath() {
        return thumbnail_path;
    }

    public void setThumbnail_path(String thumbnail_path) {
        this.thumbnail_path = thumbnail_path;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}