package com.fds.flex.core.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.fds.flex.core.portal.model.SiteDisplay;
import com.fds.flex.core.portal.service.SiteService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Slf4j
public class DisplayInitializer {

    @Autowired
    private SiteService siteService;

    @PostConstruct
    public void init() {
        log.info("DisplayInitializer::init");
        loadComponent();
    }

    private void loadComponent() {
        log.info("DisplayInitializer::loadComponent");
        Flux<SiteDisplay> siteDisplays = siteService.findAll()
            .flatMap(site -> Mono.just(SiteDisplay.build(site)));

        siteDisplays.subscribe(this::cacheSiteDisplay);
    }

    @Cacheable(value = "siteDisplayCache", key = "#siteDisplay.id")
    public SiteDisplay cacheSiteDisplay(SiteDisplay siteDisplay) {
        return siteDisplay; // Return the object to be cached
    }
}
