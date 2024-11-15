package com.media.share.service;

import com.media.share.dto.MediaFileDto;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MakeThumbNail {

    public MediaFileDto doMake(MultipartFile file, Path toFilePath){

        int frameNumber = 0;
        String thumbnailName = "thumbnailvideo-frame-" + UUID.randomUUID().toString() + file.getOriginalFilename()+".png";
        String toFilePathThumbnail = "C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp\\thumbnail\\"+thumbnailName;
        MediaFileDto mediaFileDto = new MediaFileDto();
        try {
            Picture picture = FrameGrab.getFrameFromFile(
                    new File(toFilePath.toString()), frameNumber);
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            ImageIO.write(bufferedImage, "png", new File(
                    toFilePathThumbnail));
        } catch (Exception e1) {
            e1.printStackTrace();
            return mediaFileDto;
        }
        mediaFileDto.setThumbnail_name(thumbnailName);
        mediaFileDto.setThumbnail_path(toFilePathThumbnail);

        return mediaFileDto;
    }
}
