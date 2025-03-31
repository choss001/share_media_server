package com.media.share.service;

import com.media.share.dto.MediaFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IMediaService {

    public String upload(MediaFileDto mediaFileDto, MultipartFile file);
}
