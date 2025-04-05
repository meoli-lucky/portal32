package com.fds.flex.core.portal.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fds.flex.core.portal.model.GatewayModel;
import com.fds.flex.core.portal.security.ReactiveCustomAuthentication;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/* @Component
@Order(3)
@Slf4j
public class GatewayPathWebFilter implements WebFilter {

    @Autowired
    ReactiveCustomAuthentication reactiveCustomAuthentication;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().value();
        log.debug("GatewayPathWebFilter::run url::{}", requestPath);
        
        // Kiểm tra xem URL có khớp với bất kỳ gateway context nào không
        String matchedContext = PortalUtil.findMatchingGatewayContext(requestPath);
        
        if (matchedContext != null && !PortalUtil.matchContextPaths(requestPath, PortalUtil._GATEWAY_EXCLUDE_PATHS)) {
            GatewayModel gatewayModel = PortalUtil._GATEWAY_CONTEXT_MAP.get(matchedContext);
            log.debug("Matched gateway context: {} for path: {}", matchedContext, requestPath);
            
            // Kiểm tra xác thực
            return reactiveCustomAuthentication.isAuthenticated(exchange)
                    .flatMap(isAuthenticated -> {
                        if (!isAuthenticated) {
                            log.info("Gateway authentication failed for path: {}", requestPath);
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                        
                        log.debug("Successfully authenticated for gateway path: {}", requestPath);
                        return chain.filter(exchange);
                    });
        }
        
        // Không phải path gateway hoặc là exclude path, tiếp tục chain
        return chain.filter(exchange);
    }
} 
 */