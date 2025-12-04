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
                // Route for fetching auth-service API docs (SpringDoc will aggregate)
                .route("auth-service-api-docs", r -> r
                        .path("/auth-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/auth-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://auth-service"))
                
                // Actuator endpoints (public for health checks)
                .route("actuator", r -> r
                        .path("/actuator/**")
                        .uri("lb://auth-service"))
                
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

                // Company Management Routes
                .route("companies", r -> r
                        .path("/api/companies/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                // User Management Routes
                .route("users", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                // Global Roles, Company Roles, Company Types, Languages
                .route("admin-resources", r -> r
                        .path("/api/global-roles/**", "/api/company-roles/**", 
                              "/api/company-types/**", "/api/languages/**",
                              "/api/user-companies/**", "/api/user-global-roles/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                // Address Management Routes (Countries, Cities, Addresses, User-Addresses)
                .route("address-resources", r -> r
                        .path("/api/countries/**", "/api/cities/**", 
                              "/api/addresses/**", "/api/user-addresses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                // Fallback route for any other auth-service endpoints
                // NOTE: All endpoints not explicitly listed above will require JWT authentication
                .route("auth-fallback", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))

                .build();
    }
}

