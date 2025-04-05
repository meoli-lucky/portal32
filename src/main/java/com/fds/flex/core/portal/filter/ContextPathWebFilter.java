package com.fds.flex.core.portal.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fds.flex.common.ultility.Validator;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.security.ReactiveCustomAuthentication;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Order(2)
@Slf4j
public class ContextPathWebFilter implements WebFilter {

    @Autowired
    ReactiveCustomAuthentication reactiveCustomAuthentication;
    
    @Autowired
    DisplayBuilder displayBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestUri = exchange.getRequest().getURI().getPath();
        String contextPath = PortalUtil.getContextPathFromRequestUri(requestUri);

        log.info("ContextPathWebFilter::run ::url::" + requestUri);

        if (PortalUtil._SITE_CONTEXT_MAP.containsKey(contextPath)) {
            SiteModel siteModel = PortalUtil._SITE_CONTEXT_MAP.get(contextPath);

            // Kiểm tra xem path có cần bảo mật không
            if (siteModel.getSecureSitePaths().contains(contextPath)) {
                // Kiểm tra quyền truy cập
                return checkPermission(exchange, chain, contextPath, siteModel);
            } else {
                // Chuyển hướng đến ViewRenderController
                return forward(exchange, chain, contextPath, siteModel);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> forward(ServerWebExchange exchange, WebFilterChain chain, 
                              String contextPath, SiteModel siteModel) {
        // Thêm thuộc tính cần thiết vào exchange
        exchange.getAttributes().put("originContextPath", contextPath);
        exchange.getAttributes().put("site", siteModel);
        
        // Tạo path mới cho controller ViewRender
        return chain.filter(
            exchange.mutate()
                .request(exchange.getRequest().mutate().path("/viewRender").build())
                .build()
        );
    }

    private Mono<Void> checkPermission(ServerWebExchange exchange, WebFilterChain chain, 
                                      String contextPath, SiteModel siteModel) {
        if (Validator.isNull(siteModel.getRoleMap().get(contextPath))) {
            return forward(exchange, chain, contextPath, siteModel);
        }

        // Chuyển sang kiểm tra quyền theo cách reactive
        return reactiveCustomAuthentication.isAuthenticated(exchange)
            .flatMap(isAuthenticated -> {
                if (!isAuthenticated) {
                    log.info("Authentication failed for path: {}", contextPath);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                
                // Đã authenticated, kiểm tra quyền
                boolean hasPermission = reactiveCustomAuthentication.hasAnyRole(siteModel.getRoleMap().get(contextPath));
                
                if (!hasPermission) {
                    log.info("AccessDeniedException::unauthorized! {}", contextPath);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
                
                return forward(exchange, chain, contextPath, siteModel);
            });
    }
} 
