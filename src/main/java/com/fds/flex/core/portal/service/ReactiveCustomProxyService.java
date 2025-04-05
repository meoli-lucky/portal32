package com.fds.flex.core.portal.service;

import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.util.ContentTypes;
import com.fds.flex.core.portal.util.PortalUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.net.ssl.SSLContext;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReactiveCustomProxyService {

    @Autowired
    SSLContext sslContext;

    @Value("${flexcore.portal.gateway.ssl.enable}")
    private boolean isEnableGatewaySSL;

    private final WebClient webClient;

    public ReactiveCustomProxyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<ServerResponse> fwRequest(ServerRequest req) {
        String path = req.path();
        String servicePath = StringPool.BLANK;
        String endpoint = StringPool.BLANK;

        if (Validator.isNull(path)) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(PortalUtil.createErrorResponseMessage("Not Found", HttpStatus.NOT_FOUND.value()));
        }

        for (String context : PortalUtil._GATEWAY_CONTEXT_MAP.keySet()) {
            if (PortalUtil.matchContextPaths(path, context)) {
                String tmp = path.replace(context, StringPool.BLANK);
                if (PortalUtil.matchContextPaths(tmp, PortalUtil._GATEWAY_CONTEXT_MAP.get(context).getResources())) {
                    servicePath = tmp;
                }
                endpoint = PortalUtil._GATEWAY_CONTEXT_MAP.get(context).getEndpoint();
                break;
            }
        }

        if (Validator.isNull(endpoint)) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(PortalUtil.createErrorResponseMessage("Not Found", HttpStatus.NOT_FOUND.value()));
        }

        if (Validator.isNull(servicePath)) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(PortalUtil.createErrorResponseMessage("Not Found", HttpStatus.NOT_FOUND.value()));
        }

        // Lấy query string từ request
        String queryString = req.uri().getQuery();
        
        log.info("GET ServicePath: {}", servicePath);
        log.info("Params: {}", queryString);
        log.info("Endpoint + servicePath: {}", endpoint + servicePath);

        // Tạo URL đầy đủ để forward request
        String targetUrl = endpoint + servicePath + 
                (Validator.isNotNull(queryString) ? ("?" + queryString) : StringPool.BLANK);

        // Sử dụng WebClient để forward request
        return webClient
                .method(req.method())
                .uri(targetUrl)
                .headers(headers -> {
                    req.headers().asHttpHeaders().forEach((name, values) -> {
                        headers.addAll(name, values);
                    });
                })
                .retrieve()
                .toEntity(String.class)
                .flatMap(response -> ServerResponse
                        .status(response.getStatusCode())
                        .headers(headers -> headers.addAll(response.getHeaders()))
                        .bodyValue(response.getBody()))
                .onErrorResume(e -> {
                    log.error("Error forwarding request", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(PortalUtil.createErrorResponseMessage(
                                    "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
                });
    }
} 
