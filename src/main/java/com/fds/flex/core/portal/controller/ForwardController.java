package com.fds.flex.core.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fds.flex.core.portal.service.ReactiveCustomProxyService;
import com.fds.flex.core.portal.util.PortalUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/* @Component
@Slf4j
public class ForwardController {
	
	@Autowired
	ReactiveCustomProxyService customProxyService;

	public Mono<ServerResponse> fwRequest(ServerRequest request) {
		log.info("fowardrequest: {}", System.currentTimeMillis());
		return customProxyService.fwRequest(request);
	}
	
	@Bean
	public RouterFunction<ServerResponse> gatewayRoutes() {
		// Xây dựng router dynamically dựa trên PortalUtil._GATEWAY_CONTEXT_MAP
		return RouterFunctions.route()
			.GET("/**", this::fwRequest)
			.POST("/**", this::fwRequest)
			.PUT("/**", this::fwRequest)
			.DELETE("/**", this::fwRequest)
			.build();
	}
} */
