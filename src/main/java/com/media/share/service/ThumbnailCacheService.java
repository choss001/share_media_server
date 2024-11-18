package com.media.share.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.media.share.model.MediaFile;
import org.jcodec.common.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Service
public class ThumbnailCacheService {
    private final Cache<Long, byte[]> thumbnailCache = Caffeine.newBuilder()
            .maximumSize(100) // Limit cache size
            .expireAfterWrite(Duration.ofMinutes(10)) // Cache expiry
            .build();

    public byte[] getThumbnail(MediaFile mediaFile) {
        return thumbnailCache.get(mediaFile.getId(), id -> {
            try {
                if (mediaFile.getThumbnailPath() == null) return new byte[0];
                InputStream in = new FileInputStream(mediaFile.getThumbnailPath());
                return IOUtils.toByteArray(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}