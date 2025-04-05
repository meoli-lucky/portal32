package com.fds.flex.core.portal.config;

import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated Servlet Filter configuration is not compatible with WebFlux.
 * Use WebFilter components directly instead which are automatically registered.
 */
@Deprecated
@Configuration
@Slf4j
public class FilterConfig {
	// Servlet Filters đã được thay thế bằng WebFilters
	// ContextPathFilter -> ContextPathWebFilter
	// GatewayPathFilter -> GatewayPathWebFilter
}
