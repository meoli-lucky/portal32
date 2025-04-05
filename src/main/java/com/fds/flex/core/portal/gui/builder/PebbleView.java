package com.fds.flex.core.portal.gui.builder;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.reactive.result.view.View;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.MediaType;
import java.util.Locale;
import java.io.StringWriter;
import java.util.Map;

public class PebbleView extends AbstractUrlBasedView implements View {
    private final PebbleEngine engine;

    public PebbleView(PebbleEngine engine) {
        this.engine = engine;
    }

    @Override
    protected Mono<Void> renderInternal(Map<String, Object> model, MediaType contentType, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            PebbleTemplate template = engine.getTemplate(getUrl());
            StringWriter writer = new StringWriter();
            template.evaluate(writer, model);
            return writer.toString();
        }).flatMap(html -> {
            exchange.getResponse().getHeaders().setContentType(contentType);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(html.getBytes())));
        });
    }

    @Override
    public boolean checkResourceExists(Locale locale) {
        try {
            engine.getTemplate(getUrl());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 