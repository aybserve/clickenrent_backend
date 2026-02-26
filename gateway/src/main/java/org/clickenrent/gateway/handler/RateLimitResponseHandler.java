package org.clickenrent.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom handler for rate limit exceeded responses.
 * Provides detailed error messages and rate limit headers.
 */
@Slf4j
@Component
public class RateLimitResponseHandler {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Handle rate limit exceeded response.
     * 
     * @param exchange The server web exchange
     * @param retryAfterSeconds Seconds to wait before retrying
     * @return Mono<Void> representing the response
     */
    public Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange, long retryAfterSeconds) {
        ServerHttpResponse response = exchange.getResponse();
        
        // Extract client information for logging
        String ip = getClientIp(exchange);
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String path = exchange.getRequest().getPath().value();
        
        // Log rate limit violation
        log.warn("Rate limit exceeded - IP: {}, User: {}, Path: {}, Retry after: {}s", 
                ip, userId != null ? userId : "anonymous", path, retryAfterSeconds);
        
        // Set response status
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // Add rate limit headers
        response.getHeaders().add("X-RateLimit-Retry-After-Seconds", String.valueOf(retryAfterSeconds));
        response.getHeaders().add("Retry-After", String.valueOf(retryAfterSeconds));
        response.getHeaders().add("X-RateLimit-Reset", String.valueOf(Instant.now().getEpochSecond() + retryAfterSeconds));
        
        // Create error response body
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Too Many Requests");
        errorBody.put("message", "Rate limit exceeded. Please try again later.");
        errorBody.put("retryAfterSeconds", retryAfterSeconds);
        errorBody.put("timestamp", Instant.now().toString());
        errorBody.put("path", path);
        
        try {
            String jsonResponse = objectMapper.writeValueAsString(errorBody);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error creating rate limit response JSON", e);
            return response.setComplete();
        }
    }
    
    /**
     * Add rate limit headers to successful responses.
     * 
     * @param exchange The server web exchange
     * @param remaining Remaining requests allowed
     * @param limit Total requests allowed
     */
    public void addRateLimitHeaders(ServerWebExchange exchange, long remaining, long limit) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("X-RateLimit-Limit", String.valueOf(limit));
        response.getHeaders().add("X-RateLimit-Remaining", String.valueOf(remaining));
    }
    
    /**
     * Extract client IP address from request.
     * Checks X-Forwarded-For header first (for proxied requests).
     * 
     * @param exchange The server web exchange
     * @return Client IP address
     */
    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return ip.split(",")[0].trim();
        }
        
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        
        return "unknown";
    }
}
