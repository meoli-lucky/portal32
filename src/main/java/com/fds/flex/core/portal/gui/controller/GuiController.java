package com.fds.flex.core.portal.gui.controller;

import java.io.StringWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.Rendering;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/gui/view")
public class GuiController {

    @Autowired
    private PebbleEngine pebbleEngine;
    @GetMapping(value ="/page", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Mono<String> home() {
        return Mono.fromCallable(() -> {
            FileLoader loader = new FileLoader();
        loader.setPrefix("D:/Work/Projects/java/springboot/portal/portal32/resources");
        loader.setSuffix(".html");

        PebbleEngine engine = new PebbleEngine.Builder()
                .loader(loader)
                .autoEscaping(true)
                .build();

        PebbleTemplate template = pebbleEngine.getTemplate("themes/default/index"); // KHÔNG có .html ở đây

        Map<String, Object> context = new HashMap<>();
        context.put("title", "Pebble từ ổ đĩa ngoài");

        StringWriter writer = new StringWriter();
        template.evaluate(writer, context);
        return writer.toString();
        });
    }
}
