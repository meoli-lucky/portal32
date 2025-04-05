package com.fds.flex.core.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.common.ultility.string.StringUtil;
import com.fds.flex.core.portal.model.GatewayModel;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class GatewayConfig {

    @Value("${flexcore.portal.gateway.config}")
    String gatewayConfig;

    @Autowired
    SSLContext sslContext;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        if (Validator.isNotNull(gatewayConfig)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode nodes;
            try {
                nodes = mapper.readTree(gatewayConfig);

                nodes.forEach(node -> {
                    String gatewayContextOriginal = node.get("gatewayContext").textValue();
                    String endpoint = node.get("endpoint").textValue();
                    List<String> resources = Arrays.asList(StringUtil.split(node.get("resources").textValue()));

                    // Xử lý gatewayContext và tạo bản sao cho sử dụng trong lambda
                    final String gatewayContext = gatewayContextOriginal.replace("/**", StringPool.BLANK);

                    GatewayModel gatewayModel = GatewayModel.builder()
                            .endpoint(endpoint)
                            .gatewayContext(gatewayContext)
                            .resources(resources)
                            .build();

                    PortalUtil._GATEWAY_CONTEXT_MAP.put(gatewayContext, gatewayModel);

                    // Chỉ thêm route nếu endpoint không trống
                    if (Validator.isNotNull(endpoint)) {
                        // Đảm bảo endpoint có scheme
                        if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                            endpoint = "http://" + endpoint;
                        }

                        final String finalEndpoint = endpoint;

                        routes.route(gatewayContext, r -> r
                                .path(gatewayContext + "/**")
                                .filters(f -> f
                                        .addRequestHeader(HttpHeaders.AUTHORIZATION,
                                                "Bearer " + "{{token}}") // Đây là placeholder, sẽ được thay thế động
                                        .rewritePath(gatewayContext + "/(?<segment>.*)", "/${segment}"))
                                .uri(finalEndpoint));
                    } else {
                        log.warn("Skipping route configuration for '{}' because endpoint is empty", gatewayContext);
                    }
                });
            } catch (Exception e) {
                log.error("Error configuring gateway routes", e);
            }
        }

        return routes.build();
    }
}
