package com.fds.flex.app.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.fds.flex.core.portal",
    "com.fds.flex.app.web"
})
@EnableScheduling
@EnableCaching
@ConfigurationPropertiesScan
@EnableR2dbcRepositories(basePackages = "com.fds.flex.core.portal.repository")
public class Application {
    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles("web");
        app.run(args);
    }
}
