package com.fds.flex.core.portal.gui.builder;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.loader.Loader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;
import org.springframework.web.reactive.result.view.ViewResolver;

@Configuration
@Slf4j
public class PebbleEngineBuilder {

    @Value("${flexcore.portal.web.static-resource.dir}")
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
                .autoEscaping(true)
                .cacheActive(pebbleCache)
                .build();
    }

    @Bean
    public ViewResolver viewResolver(PebbleEngine pebbleEngine) {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver() {
            @Override
            protected org.springframework.web.reactive.result.view.AbstractUrlBasedView createView(String viewName) {
                return new PebbleView(pebbleEngine);
            }
        };
        resolver.setViewClass(PebbleView.class);
        resolver.setPrefix("");
        resolver.setSuffix(".html");
        return resolver;
    }
} 