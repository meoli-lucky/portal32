package com.fds.flex.app.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@ComponentScan({ "com.fds.flex.app.admin.*" })
@EnableScheduling
@EnableCaching
@ConfigurationPropertiesScan
public class Application {
    public static void main(String[] args) {
		//SpringApplication.run(PortalAdminApplication.class, args);
		SpringApplication app = new SpringApplication(Application.class);
		app.setAdditionalProfiles("admin");
        app.run(args);
	}
}
