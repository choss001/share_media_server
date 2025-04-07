package com.media.share.model;


import com.media.share.dto.MediaFileDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
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
        setPublicYn((mediaFileDto.getPublicYn()));
        setTitle(mediaFileDto.getTitle());
        setContent(mediaFileDto.getContent());
        setFileNameUid(mediaFileDto.getFileNameUid());
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

    @Column(nullable = false)
    private Character publicYn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Character deleteYn;

    @Column(nullable = false)
    private String fileNameUid;

    @Column(nullable = true)
    private String fileNameHls;
    @Column(nullable = true)
    private String filePathHls;


    private String thumbnailName;

    private String thumbnailPath;
    private String fileType;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime uploadDate;
    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;


    private Integer requiredRole;
    private Long userId;
}