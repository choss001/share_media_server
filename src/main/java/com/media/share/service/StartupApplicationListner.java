package com.media.share.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListner implements ApplicationListener<ApplicationEvent> {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //System.out.println("Database URL: " + databaseUrl);
        //System.out.println("Database username : "+ databaseUsername);
        //System.out.println("Database passwrod : "+ databasePassword);
    }
}
