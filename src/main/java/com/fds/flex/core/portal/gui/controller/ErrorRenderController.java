package com.fds.flex.core.portal.gui.controller;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class ErrorRenderController {

	@RequestMapping("/error")
	public Mono<String> handleError(ServerWebExchange exchange) {
		HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
		log.debug("Error handling with status: {}", statusCode);
		
		if (statusCode == null) {
			return Mono.just("commons/error");
		}
		
		int status = statusCode.value();
		
		switch (status) {
			case 401: // UNAUTHORIZED
				return Mono.just("commons/401");
			case 404: // NOT_FOUND
				return Mono.just("commons/404");
			case 403: // FORBIDDEN
				return Mono.just("commons/403");
			case 502: // BAD_GATEWAY
				return Mono.just("commons/502");
			case 503: // SERVICE_UNAVAILABLE
				return Mono.just("commons/503");
			case 500: // INTERNAL_SERVER_ERROR
				return Mono.just("commons/500");
			default:
				return Mono.just("commons/error");
		}
	}
}
