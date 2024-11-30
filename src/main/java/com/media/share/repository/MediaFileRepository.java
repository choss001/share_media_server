package com.media.share.repository;

import com.media.share.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findByFileNameAndFileSize(String fileName, long fileSize);
}
