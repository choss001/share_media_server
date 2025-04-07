package com.media.share.repository;

import com.media.share.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findByFileNameAndFileSizeAndDeleteYn(String fileName, long fileSize, Character deleteYn);
    List<MediaFile> findByFilePathHlsIsNullAndDeleteYn(Character deleteYn);
    Optional<MediaFile> findByFileNameUid(String fileNameUid);
}
