package com.media.share.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MediaConverterConsumer {

    @KafkaListener(topics = "media-convert-topic", groupId = "media-converter")
    public void listen(String message) {
        System.out.println("Received: " + message);
        // Deserialize JSON
        // Convert media to HLS using FFmpeg or whatever tool
    }
}