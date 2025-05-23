package com.media.share.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media.share.dto.SignInRequest;
import com.media.share.service.UserDetailsImpl;
import com.media.share.service.kafka.MediaUploadProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@Log4j2
public class TestController {

    @Autowired
    private MediaUploadProducer mediaUploadProducer;


    @GetMapping("/all")
    public String allAccess(){
        return "PUublic Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess(){
        String principal = "nothing";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null & authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)){
            principal = authentication.getPrincipal().toString();
        }
        return principal;
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess(){
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(){
        return "Admin Board.";
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> profile(Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/kafka")
    public String test(@RequestParam("param") String param) throws JsonProcessingException {
        Map<String, Object> metadata = Map.of(
                "testKey", param
        );
        String message = new ObjectMapper().writeValueAsString(metadata);
        mediaUploadProducer.sendConversionRequest("media-convert-topic", message);
        return "SUCCESS";

    }
}
