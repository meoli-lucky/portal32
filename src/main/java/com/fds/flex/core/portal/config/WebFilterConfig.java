package com.fds.flex.core.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import com.fds.flex.core.portal.filter.GlobalWebFilter;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebFilterConfig {

    @Autowired
    private DisplayBuilder displayBuilder;

    @Bean
    public WebFilter globalWebFilter() {
        return new OrderedWebFilter() {
            private final GlobalWebFilter delegate = new GlobalWebFilter();
            
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE + 10;
            }
            
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                String requestUri = exchange.getRequest().getURI().getPath();
                String contextPath = PortalUtil.getContextPathFromRequestUri(requestUri);
                
                // Kiểm tra xem path có thuộc siteMap hoặc gateway context không
                boolean isSitePath = displayBuilder.getSiteMap().values().stream()
                    .anyMatch(site -> site.getSitePaths().stream()
                        .map(PortalUtil::getContextPathFromRequestUri)
                        .anyMatch(path -> path.equals(contextPath)));
                
                boolean isGatewayPath = PortalUtil.matchContextPaths(contextPath, PortalUtil._GATEWAY_CONTEXT_MAP.keySet());
                                
                if (isSitePath || isGatewayPath) {
                    log.debug("Applying GlobalWebFilter for path: {}", requestUri);
                    
                    // Thêm thông tin về loại path vào exchange attributes
                    exchange.getAttributes().put("pathType", isGatewayPath ? "GATEWAY" : "SITE");
                    
                    return delegate.filter(exchange, chain);
                } else {
                    log.debug("Skipping GlobalWebFilter for path: {}", requestUri);
                    return chain.filter(exchange);
                }
            }
        };
    }

    @Bean
    public WebFilter errorHandlingFilter() {
        return new OrderedWebFilter() {
            @Override
            public int getOrder() {
                return Ordered.LOWEST_PRECEDENCE;
            }
            
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                return chain.filter(exchange)
                    .onErrorResume(throwable -> {
                        log.error("Error occurred during request processing", throwable);
                        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
                        return exchange.getResponse().setComplete();
                    });
            }
        };
    }
} 