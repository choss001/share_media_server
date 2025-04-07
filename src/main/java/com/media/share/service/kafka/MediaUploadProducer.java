package com.media.share.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaUploadProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendConversionRequest(String topic, String mediaInfoJson){
        kafkaTemplate.send(topic, mediaInfoJson);

    }
}
