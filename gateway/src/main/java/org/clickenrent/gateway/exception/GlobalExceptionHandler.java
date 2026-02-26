package org.clickenrent.gateway.exception;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-1)
@Configuration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    
    /**
     * Capture exception to Sentry with request context.
     */
    private void captureExceptionToSentry(Throwable ex, ServerWebExchange exchange, HttpStatus status) {
        try {
            Sentry.withScope(scope -> {
                scope.setTag("http_status", String.valueOf(status.value()));
                scope.setTag("request_path", exchange.getRequest().getPath().toString());
                scope.setTag("request_method", exchange.getRequest().getMethod().toString());
                scope.setLevel(status.is5xxServerError() ? SentryLevel.ERROR : SentryLevel.WARNING);
                Sentry.captureException(ex);
            });
        } catch (Exception sentryEx) {
            log.warn("Failed to capture exception to Sentry: {}", sentryEx.getMessage());
        }
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        String message;

        if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = "Service not found or unavailable";
            log.error("Service not found: {}", ex.getMessage());
            captureExceptionToSentry(ex, exchange, status);
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : "Request failed";
            log.error("Response status exception: {} - {}", status, message);
            captureExceptionToSentry(ex, exchange, status);
        } else if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Service temporarily unavailable";
            log.error("Connection refused to service: {}", ex.getMessage());
            captureExceptionToSentry(ex, exchange, status);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal server error";
            log.error("Unexpected error: ", ex);
            captureExceptionToSentry(ex, exchange, status);
        }

        response.setStatusCode(status);

        String errorJson = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                java.time.Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value()
        );

        byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}










