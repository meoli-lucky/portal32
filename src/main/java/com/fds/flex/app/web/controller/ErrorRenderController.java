package com.fds.flex.app.web.controller;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

@Controller
@Slf4j
public class ErrorRenderController {

	@Autowired
	private PebbleEngine templateEngine;

	@RequestMapping("/error")
	@ResponseBody
	public Mono<String> handleError(ServerWebExchange exchange) {
		HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
		log.debug("Error handling with status: {}", statusCode);

		if (statusCode == null) {
			return Mono.just(renderError());
		}

		int status = statusCode.value();

		switch (status) {
			case 401: // UNAUTHORIZED
				return Mono.just(render401());
			case 404: // NOT_FOUND
				return Mono.just(render404());
			case 403: // FORBIDDEN
				return Mono.just(render403());
			case 502: // BAD_GATEWAY
				return Mono.just(render502());
			case 503: // SERVICE_UNAVAILABLE
				return Mono.just(render503());
			case 500: // INTERNAL_SERVER_ERROR
				return Mono.just(render500());
			default:
				return Mono.just(renderError());
		}
	}

	private String render401() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/401");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 401 page", e);
			return "Unauthorized";
		}
	}

	private String render404() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/404");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 404 page", e);
			return "Page not found";
		}
	}

	private String render403() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/403");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 403 page", e);
			return "Forbidden";
		}
	}

	private String render502() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/502");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 502 page", e);
			return "Bad Gateway";
		}
	}

	private String render503() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/503");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 503 page", e);
			return "Service Unavailable";
		}
	}

	private String render500() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/500");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering 500 page", e);
			return "Internal Server Error";
		}
	}

	private String renderError() {
		try {
			PebbleTemplate template = templateEngine.getTemplate("commons/error");
			StringWriter writer = new StringWriter();
			template.evaluate(writer, new HashMap<>());
			return writer.toString();
		} catch (IOException e) {
			log.error("Error rendering error page", e);
			return "An error occurred";
		}
	}
}
