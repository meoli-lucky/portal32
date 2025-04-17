package com.fds.flex.core.portal.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.common.ultility.string.StringUtil;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class ResourceConfig implements WebFluxConfigurer {

	@Value("${spring.web.resources.cache.period}")
	Integer period;

	@Value("${spring.web.resources.cache.cachecontrol.max-age}")
	Integer maxAge;

	@Value("${spring.pebble.cache:true}")
	boolean pebbleCache;

	@Autowired
	DisplayBuilder displayBuilder;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String[] locations = StringUtil.split(PortalUtil
				.remakeStaticResourceDir(GetterUtil
						.getString(PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_WEB_STATIC_RESOURCE_LOCATION)))
				.concat(StringPool.COMMA)
				.concat(StringUtil
						.merge(PortalUtil.remakeStaticResourceDir(Arrays.asList(StringUtil.split(GetterUtil.getString(
								PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_OTHER_STATIC_RESOURCE_LOCATIONS))))))));
		String[] patterns = StringUtil.split(displayBuilder.getResources().getResourcePatterns());

		for (String pattern : patterns) {
			registry.addResourceHandler(pattern)
					.addResourceLocations(locations)
					.setCacheControl(CacheControl.maxAge(java.time.Duration.ofSeconds(maxAge)));
		}
	}
}
