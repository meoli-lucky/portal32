package com.fds.flex.core.portal.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.gui.builder.PebbleTemplatePreload;
import com.fds.flex.core.portal.gui.model.DisplayModel;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalConstant;
import io.pebbletemplates.pebble.PebbleEngine;

@Configuration
public class PebbleTemplatePreloadConfig {

    @Autowired
    PebbleEngine pebbleEngine;

    @Autowired
    DisplayBuilder displayBuilder;

    @Bean
    public boolean load() {
        // preloaded
        if (GetterUtil.getBoolean(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_THEME_PRELOAD))) {
            displayBuilder.getSiteMap().forEach((n, site) -> {
                String runtimeDir = GetterUtil
                        .getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_DIR))
                        + StringPool.SLASH + PortalConstant.WORK_FOLDER + StringPool.SLASH
                        + PortalConstant.RUNTIME_FOLDER + StringPool.SLASH + site.getThemeId();

                File tmp = new File(runtimeDir);

                if (!tmp.exists()) {
                    tmp.mkdirs();
                }
                PebbleTemplatePreload templatePreload = new PebbleTemplatePreload();

                Map<String, DisplayModel> map = site.getDisplayModelMap();

                map.forEach((k, v) -> {
                    try {
                        templatePreload.render(v.getTemplatePath(), v, runtimeDir, pebbleEngine);
                    } catch (IOException e) {
                        return;
                    }
                });
            });
        }
        return true;
    }
} 
