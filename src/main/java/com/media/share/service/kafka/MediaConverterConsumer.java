package com.media.share.service.kafka;

import com.media.share.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MediaConverterConsumer {
    @Autowired
    MediaService mediaService;

    @KafkaListener(topics = "media-convert-topic", groupId = "media-converter")
    public void listen(String message) {
        System.out.println("Received: " + message);
        mediaService.convertFormatAll();
        // Deserialize JSON
        // Convert media to HLS using FFmpeg or whatever tool
    }
}