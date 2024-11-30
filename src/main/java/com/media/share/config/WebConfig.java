package com.media.share.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3001",
                        "http://183.102.80.161:3000"
                ) // Next.js default port
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}