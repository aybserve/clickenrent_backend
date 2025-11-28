package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.clickenrent.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring Gateway routes...");

        return builder.routes()
                // Public Auth Routes - No JWT validation required
                .route("auth-public-register", r -> r
                        .path("/api/auth/register")
                        .uri("lb://auth-service"))
                
                .route("auth-public-login", r -> r
                        .path("/api/auth/login")
                        .uri("lb://auth-service"))
                
                .route("auth-public-refresh", r -> r
                        .path("/api/auth/refresh")
                        .uri("lb://auth-service"))

                // Protected Auth Routes - JWT validation required
                .route("auth-protected-users", r -> r
                        .path("/api/auth/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                .route("auth-protected-companies", r -> r
                        .path("/api/auth/companies/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                .route("auth-protected-profile", r -> r
                        .path("/api/auth/profile/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                // Fallback route for any other auth-service endpoints
                .route("auth-fallback", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                .build();
    }
}

