package com.media.share.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MediaController {

    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Value("${spring.application.name}")
    private String name;


    @GetMapping("/test")
    @ResponseBody
    public HashMap<String, String> getTest(){
        logger.info(name);
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "Hello from Spring boot!");

        return map;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from Spring Boot!");
    }

}
