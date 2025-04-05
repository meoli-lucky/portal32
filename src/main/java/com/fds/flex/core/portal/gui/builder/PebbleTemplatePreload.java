package com.fds.flex.core.portal.gui.builder;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.model.DisplayModel;
import com.fds.flex.core.portal.util.PortalConstant;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PebbleTemplatePreload {

    public void render(String template, DisplayModel displayModel, String storeDir, PebbleEngine pebbleEngine)
            throws IOException {
        Map<String, Object> context = new HashMap<>();
        StringWriter writer = new StringWriter();
        
        context.put("display", displayModel);
        context.put("isPreloaded", true);

        PebbleTemplate compiledTemplate = pebbleEngine.getTemplate(template);
        compiledTemplate.evaluate(writer, context);
        
        String htmlContent = writer.toString();

        if (Validator.isNotNull(displayModel.getFragment())) {
            String fragmentTemplate = "<div data-include=\"" + PortalConstant.FRAGMENT_FOLDER + StringPool.SLASH
                    + displayModel.getFragment() + "\"></div>";

            log.info("fragment: {}", fragmentTemplate);

            htmlContent = htmlContent.replace(PortalConstant.FRAGMENT_PATTERN, fragmentTemplate);
        } else {
            htmlContent = htmlContent.replace(PortalConstant.FRAGMENT_PATTERN, StringPool.BLANK);
        }

        htmlContent = htmlContent.replaceAll("<!\\[CDATA\\[", StringPool.BLANK).replaceAll("\\]\\]>", StringPool.BLANK);

        String tmp = storeDir + StringPool.SLASH + displayModel.getPageId() + ".html";

        Files.write(Paths.get(tmp), htmlContent.getBytes(StandardCharsets.UTF_8));
    }
} 
