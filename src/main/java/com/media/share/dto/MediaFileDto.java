package com.media.share.dto;

import com.media.share.model.MediaFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileDto {

    private Long id;
    private String fileName;


    private Long fileSize;

    private String filePath;

    private String thumbnail_name;

    private String thumbnail_path;
    private String fileType;

    private LocalDateTime uploadDate;
    private Character deleteYn;
    private Character publicYn;
    private String title;
    private String content;
    private Integer requiredRole;
    private Long userId;
    private String fileNameUid;
    private String fileNameHls;
    private String filePathHls;

}
