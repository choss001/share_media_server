package com.media.share.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media.share.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MediaConverterConsumer {
    @Autowired
    MediaService mediaService;

    @KafkaListener(topics = "media-convert-topic", groupId = "media-converter")
    public void listen(String message) {
        try{
            System.out.println("Received: " + message);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {});
            mediaService.convertFormat(map.get("fileNameUid"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}