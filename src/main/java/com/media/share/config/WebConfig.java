package com.media.share.config;

import com.media.share.controller.MediaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.path}")
    private String filePathBase;

    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://a31.ddns.net",
                        "http://localhost:3001",
                        "http://192.168.219.100:3001",
                        "http://192.168.219.100"
                ) // Next.js default port
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        logger.info("file base!! {}", "file:///"+filePathBase);
        registry.addResourceHandler("/resource/**")
                .addResourceLocations("file:///"+filePathBase+"/public/") // or specify another location
//                .setCachePeriod(0);
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());

    }
}