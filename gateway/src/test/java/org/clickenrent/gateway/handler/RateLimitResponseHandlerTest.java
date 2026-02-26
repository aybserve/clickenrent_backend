package org.clickenrent.gateway.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RateLimitResponseHandler.
 */
class RateLimitResponseHandlerTest {

    private RateLimitResponseHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RateLimitResponseHandler();
    }

    @Test
    void handleRateLimitExceeded_sets429AndRateLimitHeaders() {
        long retryAfterSeconds = 60L;
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/bikes").build());

        handler.handleRateLimitExceeded(exchange, retryAfterSeconds).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(exchange.getResponse().getHeaders().getContentType())
                .isNotNull();
        assertThat(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Retry-After-Seconds"))
                .isEqualTo("60");
        assertThat(exchange.getResponse().getHeaders().getFirst("Retry-After"))
                .isEqualTo("60");
        assertThat(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Reset"))
                .isNotNull();
    }

    @Test
    void handleRateLimitExceeded_writesJsonBodyWithErrorAndRetryAfter() {
        long retryAfterSeconds = 30L;
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/auth/login").build());

        handler.handleRateLimitExceeded(exchange, retryAfterSeconds).block();

        Flux<DataBuffer> body = ((MockServerHttpResponse) exchange.getResponse()).getBody();
        String bodyString = body != null ? body.map(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }).reduce("", (a, b) -> a + b).block() : "";

        assertThat(bodyString).contains("\"error\":\"Too Many Requests\"");
        assertThat(bodyString).contains("\"message\":\"Rate limit exceeded. Please try again later.\"");
        assertThat(bodyString).contains("\"retryAfterSeconds\":30");
        assertThat(bodyString).contains("\"path\":\"/api/v1/auth/login\"");
    }

    @Test
    void addRateLimitHeaders_addsLimitAndRemainingHeaders() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/bikes").build());

        handler.addRateLimitHeaders(exchange, 5L, 10L);

        assertThat(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Limit"))
                .isEqualTo("10");
        assertThat(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Remaining"))
                .isEqualTo("5");
    }
}
