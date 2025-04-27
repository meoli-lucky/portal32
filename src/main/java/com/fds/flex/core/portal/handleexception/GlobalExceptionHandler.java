package com.fds.flex.core.portal.handleexception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fds.flex.common.ultility.string.StringPool;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ProblemDetailResponse>> handleResponseStatus(ResponseStatusException ex,
            ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        return buildProblem(
                status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getReason() != null ? ex.getReason() : (status != null ? status.getReasonPhrase() : "Unknown error"),
                exchange,
                URI.create(exchange.getRequest().getURI().toString() + StringPool.SLASH + ex.getStatusCode().value()));
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ProblemDetailResponse>> handleGeneric(Throwable ex, ServerWebExchange exchange) {
        log.error("Unhandled exception: ", ex);
        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                exchange,
                URI.create(exchange.getRequest().getURI().toString() + "/500"));
    }

    private Mono<ResponseEntity<ProblemDetailResponse>> buildProblem(HttpStatus status, String detail,
            ServerWebExchange exchange, URI type) {
        ProblemDetailResponse body = new ProblemDetailResponse(
                type,
                status.getReasonPhrase(),
                status.value(),
                detail,
                exchange.getRequest().getPath().value(),
                Instant.now());
        return Mono.just(ResponseEntity
                .status(status)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(body));
    }

    public record ProblemDetailResponse(
            URI type,
            String title,
            int status,
            String detail,
            String instance,
            Instant timestamp) {
    }
}
