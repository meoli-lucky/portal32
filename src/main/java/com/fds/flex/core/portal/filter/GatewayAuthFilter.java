package com.fds.flex.core.portal.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.security.ReactiveCustomAuthentication;
import com.fds.flex.core.portal.util.PortalConstant;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GatewayAuthFilter implements GlobalFilter {

    @Autowired
    private ReactiveCustomAuthentication customAuthentication;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Kiểm tra nếu path là API gateway và không phải exclude path
        if (PortalUtil.matchContextPaths(path, PortalUtil._GATEWAY_CONTEXT_MAP.keySet())
                && !PortalUtil.matchContextPaths(path, PortalUtil._GATEWAY_EXCLUDE_PATHS)) {

            // Kiểm tra xác thực
            return customAuthentication.isAuthenticated(exchange)
                    .flatMap(isAuthenticated -> {
                        if (!isAuthenticated) {
                            log.info("Gateway access unauthorized: {}", path);
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        // Trích xuất token và thêm vào header
                        return exchange.getSession()
                                .flatMap(session -> {
                                    String sessionId = session.getId();
                                    Object tokenObj = session
                                            .getAttribute(sessionId + StringPool.DASH + PortalConstant.ACCESS_TOKEN);
                                    String token = tokenObj != null ? tokenObj.toString() : StringPool.BLANK;

                                    if (Validator.isNotNull(token)) {
                                        // Thêm token vào request
                                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                                .header(HttpHeaders.AUTHORIZATION,
                                                        PortalConstant.BEARER + StringPool.SPACE + token)
                                                .build();

                                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                                    }

                                    // Nếu không có token, cho phép request tiếp tục nếu đã được xác thực
                                    return chain.filter(exchange);
                                });
                    });
        }

        // Không phải path gateway, cho qua
        return chain.filter(exchange);
    }
}
