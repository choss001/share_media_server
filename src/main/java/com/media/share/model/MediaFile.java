package com.media.share.model;


import com.media.share.dto.MediaFileDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MediaFile {

    public MediaFile(){ }

    public MediaFile(MediaFileDto mediaFileDto){
        setThumbnailName(mediaFileDto.getThumbnail_name());
        setThumbnailPath(mediaFileDto.getThumbnail_path());
        setFileType(mediaFileDto.getFileType());
        setFileName(mediaFileDto.getFileName());
        setUploadDate(mediaFileDto.getUploadDate());
        setFilePath(mediaFileDto.getFilePath());
        setDeleteYn(mediaFileDto.getDeleteYn());
        setFileSize(mediaFileDto.getFileSize());
        setRequiredRole(mediaFileDto.getRequiredRole());
        setUserId(mediaFileDto.getUserId());
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


    private String thumbnailName;

    private String thumbnailPath;
    private String fileType;

    private LocalDateTime uploadDate;


    private Integer requiredRole;
    private Long userId;

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


    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(Integer requiredRole) {
        this.requiredRole = requiredRole;
    }
}