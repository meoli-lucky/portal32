package com.fds.flex.core.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebFlux
@Slf4j
public class WebFluxConfig implements WebFluxConfigurer {
    
    @Autowired(required = false)
    private ViewResolver viewResolver;
    
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        if (viewResolver != null) {
            registry.viewResolver(viewResolver);
            log.info("Registered custom ViewResolver: {}", viewResolver.getClass().getName());
        }
    }
} 