package com.media.share.service;

import com.media.share.controller.MediaController;
import com.media.share.dto.MediaFileDto;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@Service
public class MakeThumbNail {
    Logger logger = LoggerFactory.getLogger(MediaController.class);


    @Value("${file.path}")
    private String filePathBase;

    public MediaFileDto doMake(MultipartFile file, Path toFilePath, String type) throws IOException {

        int frameNumber = 0;
        String thumbnailName = "thumbnailvideo-frame-" +
                UUID.randomUUID().toString() +
                file.getOriginalFilename()+".png";
        Path thumbnailPath = Paths.get(filePathBase, type, "thumbnail", thumbnailName);
        String thumbnailPathStr = thumbnailPath.toString();
        MediaFileDto mediaFileDto = new MediaFileDto();
        File outputFile = new File(thumbnailPathStr);
        Files.createDirectories(thumbnailPath.getParent()); // Ensure directory exists
        try {
            Picture picture = FrameGrab.getFrameFromFile(
                    new File(toFilePath.toString()), frameNumber);
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            if (bufferedImage.getHeight() < bufferedImage.getWidth()){
                bufferedImage = rotateImage(bufferedImage);
            }

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            if (!writers.hasNext()) {
                throw new IllegalStateException("No writers found for format: png");
            }
            ImageWriter imageWriter = writers.next();

            try (ImageOutputStream out = ImageIO.createImageOutputStream(outputFile)) {
                imageWriter.setOutput(out);
                ImageWriteParam iwp = imageWriter.getDefaultWriteParam();

                imageWriter.write(null, new IIOImage(bufferedImage, null, null), iwp);
                mediaFileDto.setThumbnail_path(thumbnailPathStr);
                mediaFileDto.setThumbnail_name(thumbnailName);
            } catch (IOException e) {
                logger.error("thumbnail error",e);
            } finally {
                imageWriter.dispose(); // Ensure resources are freed
            }




        } catch (Exception e1) {
            logger.error("thumbnail error",e1);
            return mediaFileDto;
        }
        logger.info("Readable: {}", outputFile.canRead());
        logger.info("Writable: {}", outputFile.canWrite());

        return mediaFileDto;
    }
    private BufferedImage rotateImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotated = new BufferedImage(height, width, image.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.translate((height - width) / 2, (height - width) / 2);
        g2d.rotate(Math.toRadians(90), height / 2.0, width / 2.0);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rotated;
    }
}
