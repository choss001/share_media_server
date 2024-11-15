package com.media.share.simple;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class VideoThumbnail {

    public static void main(String[] args) throws IOException, JCodecException {
        int frameNumber = 0;
        DirectoryStream.Filter<Path> filter = file -> {
            return file.toString().endsWith(".mp4") || file.toString().endsWith(".MP4")
                    || file.toString().endsWith(".mov") || file.toString().endsWith(".MOV");
        };
        Path dirName = Paths.get("C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirName, filter)) {
            stream.forEach(path -> {
                try {
                    Picture picture = FrameGrab.getFrameFromFile(
                            new File(path.toString()), frameNumber);
                    BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
                    ImageIO.write(bufferedImage, "png", new File(
                            "C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp\\thumbnail\\thumbnailvideo-frame-" + UUID.randomUUID().toString() + ".png"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}