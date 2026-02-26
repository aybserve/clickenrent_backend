package org.clickenrent.gateway.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handle_notFoundException_returns404WithExpectedMessage() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/bikes").build());
        Throwable ex = new org.springframework.cloud.gateway.support.NotFoundException("route not found");

        handler.handle(exchange, ex).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String body = getResponseBodyAsString(exchange);
        assertThat(body).contains("\"status\":404");
        assertThat(body).contains("\"error\":\"Not Found\"");
        assertThat(body).contains("\"message\":\"Service not found or unavailable\"");
        assertThat(body).contains("\"path\":\"/api/v1/bikes\"");
    }

    @Test
    void handle_responseStatusException_returnsMatchingStatusAndReason() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/auth/login").build());
        Throwable ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");

        handler.handle(exchange, ex).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        String body = getResponseBodyAsString(exchange);
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"message\":\"Invalid credentials\"");
    }

    @Test
    void handle_responseStatusExceptionWithNullReason_returnsRequestFailedMessage() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/unknown").build());
        Throwable ex = new ResponseStatusException(HttpStatus.FORBIDDEN);

        handler.handle(exchange, ex).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        String body = getResponseBodyAsString(exchange);
        assertThat(body).contains("\"status\":403");
        assertThat(body).contains("\"message\":\"Request failed\"");
    }

    @Test
    void handle_connectionRefusedException_returns503() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/rentals").build());
        Throwable ex = new RuntimeException("Connection refused: localhost/127.0.0.1:8082");

        handler.handle(exchange, ex).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        String body = getResponseBodyAsString(exchange);
        assertThat(body).contains("\"status\":503");
        assertThat(body).contains("\"message\":\"Service temporarily unavailable\"");
    }

    @Test
    void handle_genericException_returns500() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/users").build());
        Throwable ex = new RuntimeException("Unexpected failure");

        handler.handle(exchange, ex).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        String body = getResponseBodyAsString(exchange);
        assertThat(body).contains("\"status\":500");
        assertThat(body).contains("\"error\":\"Internal Server Error\"");
        assertThat(body).contains("\"message\":\"Internal server error\"");
    }

    private String getResponseBodyAsString(ServerWebExchange exchange) {
        Flux<DataBuffer> body = ((MockServerHttpResponse) exchange.getResponse()).getBody();
        if (body == null) {
            return "";
        }
        String result = body.map(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }).reduce("", (a, b) -> a + b).block();
        return result != null ? result : "";
    }
}
