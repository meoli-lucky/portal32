package com.fds.flex.core.portal.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.gui.builder.DisplayBuilder;
import com.fds.flex.core.portal.gui.model.SiteModel;
import com.fds.flex.core.portal.util.PortalUtil;
import com.fds.flex.core.portal.model.GatewayModel;
import com.fds.flex.core.portal.model.Site;
import com.fds.flex.core.portal.service.SiteService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Order(2)
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(name = "flexcore.portal.web.context-path-filter.enabled", havingValue = "true", matchIfMissing = true)
public class GlobalWebFilter implements WebFilter {

    @Autowired
    DisplayBuilder displayBuilder;

    @Autowired
    private SiteService siteService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestUri = exchange.getRequest().getURI().getPath();

        log.info("GlobalWebFilter::run ::url::" + requestUri);

        // Lấy thông tin về loại path từ attributes
        String pathType = (String) exchange.getAttributes().get("pathType");

        if ("GATEWAY".equals(pathType)) {
            String requestContextPath = PortalUtil.getContextPathFromRequestUri(requestUri);
            String servicePath = StringPool.BLANK;
            GatewayModel gatewayModel = null;

            for (String context : PortalUtil._GATEWAY_CONTEXT_MAP.keySet()) {
                if (PortalUtil.matchContextPaths(requestContextPath, context)) {
                    String tmp = requestContextPath.replace(context, StringPool.BLANK);
                    if (PortalUtil.matchContextPaths(tmp,
                            PortalUtil._GATEWAY_CONTEXT_MAP.get(context).getResources())) {
                        servicePath = tmp;
                    }
                    gatewayModel = PortalUtil._GATEWAY_CONTEXT_MAP.get(context);
                    break;
                }
            }
            if (gatewayModel == null) {
                return forwardError(exchange, chain, requestContextPath);
            }
            return forwardToGateway(exchange, chain, gatewayModel, servicePath);

        } else if ("SITE".equals(pathType)) {
            SiteModel siteModel = PortalUtil._SITE_CONTEXT_MAP.get(requestUri);
            Mono<Site> monoSite = siteService.findByContextPath(requestUri);

            return monoSite.flatMap(site -> {
                if (Validator.isNull(site)) {
                    return forwardError(exchange, chain, requestUri);
                }
                if (site.isPrivateSite()) {
                    return checkPermission(exchange, chain, requestUri, site);
                }
                return forward(exchange, chain, requestUri, site);
            });

            // Kiểm tra xem path có cần bảo mật không
           /*  if (siteModel.getSecureSitePaths().contains(requestUri)) {
                // Kiểm tra quyền truy cập
                return checkPermission(exchange, chain, requestUri, siteModel);
            } else {
                // Chuyển hướng đến ViewRenderController
                return forward(exchange, chain, requestUri, siteModel);
            } */
        }

        return chain.filter(exchange)
                .onErrorResume(throwable -> {
                    log.error("Error occurred during request processing", throwable);
                    return forwardError(exchange, chain, requestUri);
                });
    }

    private Mono<Void> forwardToGateway(ServerWebExchange exchange, WebFilterChain chain, GatewayModel gatewayModel,
            String servicePath) {
        // Tạo path mới cho gateway endpoint
        String newPath = gatewayModel.getEndpoint() + servicePath
                + (exchange.getRequest().getURI().getQuery() != null ? "?" + exchange.getRequest().getURI().getQuery()
                        : "");

        return chain.filter(
                exchange.mutate()
                        .request(exchange.getRequest().mutate().path(newPath).build())
                        .build());
    }

    private Mono<Void> forward(ServerWebExchange exchange, WebFilterChain chain,
            String contextPath, Site site) {
        // Thêm thuộc tính cần thiết vào exchange
        exchange.getAttributes().put("originContextPath", contextPath);
        exchange.getAttributes().put("site", site);

        // Tạo path mới cho controller ViewRender
        return chain.filter(
                exchange.mutate()
                        .request(exchange.getRequest().mutate().path("/viewRender").build())
                        .build());
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
                        .build());
    }

    private Mono<Void> forwardError(ServerWebExchange exchange, WebFilterChain chain,
            String contextPath) {
        return chain.filter(
                exchange.mutate()
                        .request(exchange.getRequest().mutate().path("/error").build())
                        .build());
    }

    private Mono<Void> checkPermission(ServerWebExchange exchange, WebFilterChain chain,
            String contextPath, Site site) {
     
        return forward(exchange, chain, contextPath, site);
        // Chuyển sang kiểm tra quyền theo cách reactive
        /*
         * return reactiveCustomAuthentication.isAuthenticated(exchange)
         * .flatMap(isAuthenticated -> {
         * if (!isAuthenticated) {
         * log.info("Authentication failed for path: {}", contextPath);
         * exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
         * return exchange.getResponse().setComplete();
         * }
         * 
         * // Đã authenticated, kiểm tra quyền
         * boolean hasPermission =
         * reactiveCustomAuthentication.hasAnyRole(siteModel.getRoleMap().get(
         * contextPath));
         * 
         * if (!hasPermission) {
         * log.info("AccessDeniedException::unauthorized! {}", contextPath);
         * exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
         * return exchange.getResponse().setComplete();
         * }
         * 
         * return forward(exchange, chain, contextPath, siteModel);
         * });
         */
    }

    private Mono<Void> checkPermission(ServerWebExchange exchange, WebFilterChain chain,
            String contextPath, SiteModel siteModel) {
        if (Validator.isNull(siteModel.getRoleMap().get(contextPath))) {
            return forward(exchange, chain, contextPath, siteModel);
        }
        return forward(exchange, chain, contextPath, siteModel);
        // Chuyển sang kiểm tra quyền theo cách reactive
        /*
         * return reactiveCustomAuthentication.isAuthenticated(exchange)
         * .flatMap(isAuthenticated -> {
         * if (!isAuthenticated) {
         * log.info("Authentication failed for path: {}", contextPath);
         * exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
         * return exchange.getResponse().setComplete();
         * }
         * 
         * // Đã authenticated, kiểm tra quyền
         * boolean hasPermission =
         * reactiveCustomAuthentication.hasAnyRole(siteModel.getRoleMap().get(
         * contextPath));
         * 
         * if (!hasPermission) {
         * log.info("AccessDeniedException::unauthorized! {}", contextPath);
         * exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
         * return exchange.getResponse().setComplete();
         * }
         * 
         * return forward(exchange, chain, contextPath, siteModel);
         * });
         */
    }
}
