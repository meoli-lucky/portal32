package com.fds.flex.core.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.loader.Loader;

import lombok.extern.slf4j.Slf4j;

/* @Configuration
@Slf4j
public class PebbleConfig {

    @Value("${flexcore.portal.web.static-resource.location}")
    private String templatePath;

    @Value("${spring.pebble.cache:true}")
    private boolean pebbleCache;

    @Bean
    public Loader<?> loader() {
        FileLoader loader = new FileLoader();
        loader.setPrefix(templatePath);
        loader.setSuffix(".html");
        return loader;
    }

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder()
                .loader(loader())
                .cacheActive(pebbleCache)
                .build();
    }
}  */
