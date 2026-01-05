package org.clickenrent.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.clickenrent.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Get Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", request.getPath());
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Extract token
            String token = jwtUtil.extractTokenFromHeader(authHeader);

            if (token == null) {
                log.warn("Token extraction failed");
                return onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED);
            }

            // Validate token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token validation failed");
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Extract claims
            Long userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            List<String> roles = jwtUtil.extractRoles(token);
            String userExternalId = jwtUtil.extractUserExternalId(token);
            List<String> companyExternalIds = jwtUtil.extractCompanyExternalIds(token);

            // Add user information to request headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Email", email)
                    .header("X-User-Roles", roles != null ? String.join(",", roles) : "")
                    .header("X-User-External-Id", userExternalId != null ? userExternalId : "")
                    .header("X-Company-External-Ids", companyExternalIds != null ? String.join(",", companyExternalIds) : "")
                    .build();

            log.debug("JWT authenticated for user: {} ({}), companies: {}", email, userId, companyExternalIds);

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("JWT authentication error: {}", e.getMessage());
            return onError(exchange, "Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("Authentication error: {}", error);
        return response.setComplete();
    }
}

