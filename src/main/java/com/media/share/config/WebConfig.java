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
                        "http://a31.ddns.net:80",
                        "http://a31.ddns.net:3000",
                        "http://localhost:3001"
                ) // Next.js default port
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}