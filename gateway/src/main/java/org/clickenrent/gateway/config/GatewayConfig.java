package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.clickenrent.gateway.filter.JwtAuthenticationFilter;
import org.clickenrent.gateway.ratelimit.CustomRedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Slf4j
@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    @Qualifier("ipRateLimiter")
    private CustomRedisRateLimiter ipRateLimiter;
    
    @Autowired
    @Qualifier("userRateLimiter")
    private CustomRedisRateLimiter userRateLimiter;
    
    @Autowired
    @Qualifier("ipKeyResolver")
    private KeyResolver ipKeyResolver;
    
    @Autowired
    @Qualifier("userKeyResolver")
    private KeyResolver userKeyResolver;
    
    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring Gateway routes with rate limiting enabled: {}", rateLimitEnabled);

        return builder.routes()

                // AUTH SERVICE ROUTES

                // Route for fetching auth-service API docs (SpringDoc will aggregate)
                .route("auth-service-api-docs", r -> r
                        .path("/auth-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/auth-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://auth-service"))
                
                // Public Auth Routes (v1 API) - No JWT validation required, but rate limited with IP-based limiter
                .route("auth-v1-public-register", r -> r
                        .path("/api/v1/auth/register")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(ipRateLimiter)
                                .setKeyResolver(ipKeyResolver)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("auth-v1-public-login", r -> r
                        .path("/api/v1/auth/login")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(ipRateLimiter)
                                .setKeyResolver(ipKeyResolver)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("auth-v1-public-refresh", r -> r
                        .path("/api/v1/auth/refresh")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(ipRateLimiter)
                                .setKeyResolver(ipKeyResolver)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                // Email verification routes (public, v1 API)
                .route("auth-v1-verify-email", r -> r
                        .path("/api/v1/auth/verify-email")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(ipRateLimiter)
                                .setKeyResolver(ipKeyResolver)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("auth-v1-send-verification-code", r -> r
                        .path("/api/v1/auth/send-verification-code")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(ipRateLimiter)
                                .setKeyResolver(ipKeyResolver)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                // Google OAuth routes (public, v1 API)
                .route("auth-v1-google-login", r -> r
                        .path("/api/v1/auth/google/**")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(ipRateLimiter)
                                .setKeyResolver(ipKeyResolver)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                // Protected Auth Routes (v1 API) - JWT validation required, user-based rate limiting
                .route("auth-v1-protected-me", r -> r
                        .path("/api/v1/auth/me")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("auth-v1-protected-logout", r -> r
                        .path("/api/v1/auth/logout")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("auth-v1-protected-register-admin", r -> r
                        .path("/api/v1/auth/register-admin")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                // Public Invitation Routes - No JWT validation required
                .route("invitation-public-validate", r -> r
                        .path("/api/v1/invitations/validate/**")
                        .uri("lb://auth-service"))
                
                .route("invitation-public-complete", r -> r
                        .path("/api/v1/invitations/complete")
                        .uri("lb://auth-service"))

                // Company Management Routes
                .route("companies", r -> r
                        .path("/api/v1/companies/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))

                // User Management Routes
                .route("users", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))

                // Global Roles, Company Roles, Company Types, Languages
                .route("admin-resources", r -> r
                        .path("/api/v1/global-roles/**", "/api/v1/company-roles/**", 
                              "/api/v1/company-types/**", "/api/v1/languages/**",
                              "/api/v1/user-companies/**", "/api/v1/user-global-roles/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))

                // Address Management Routes (Countries, Addresses, User-Addresses)
                .route("address-resources", r -> r
                        .path("/api/v1/countries/**", "/api/v1/addresses/**", "/api/v1/user-addresses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                // Invitation Management Routes (Protected - requires JWT)
                // Note: Specific protected invitation routes, not catch-all
                .route("invitations-create", r -> r
                        .path("/api/v1/invitations")
                        .and().method("POST")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("invitations-list", r -> r
                        .path("/api/v1/invitations")
                        .and().method("GET")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))
                
                .route("invitations-cancel", r -> r
                        .path("/api/v1/invitations/*")
                        .and().method("DELETE")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://auth-service"))


                // RENTAL SERVICE ROUTES

                
                // Route for fetching rental-service API docs (SpringDoc will aggregate)
                .route("rental-service-api-docs", r -> r
                        .path("/rental-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/rental-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://rental-service"))

                // Bike Management Routes
                .route("bikes", r -> r
                        .path("/api/v1/bikes/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-types", r -> r
                        .path("/api/v1/bike-types/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-models", r -> r
                        .path("/api/v1/bike-models/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-brands", r -> r
                        .path("/api/v1/bike-brands/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-engines", r -> r
                        .path("/api/v1/bike-engines/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-statuses", r -> r
                        .path("/api/v1/bike-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Rental & Ride Management Routes
                .route("rentals", r -> r
                        .path("/api/v1/rentals/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-rentals", r -> r
                        .path("/api/v1/bike-rentals/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("rides", r -> r
                        .path("/api/v1/rides/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("rental-units", r -> r
                        .path("/api/v1/rental-units/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("rental-plans", r -> r
                        .path("/api/v1/rental-plans/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("rental-statuses", r -> r
                        .path("/api/v1/rental-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("ride-statuses", r -> r
                        .path("/api/v1/ride-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-reservations", r -> r
                        .path("/api/v1/bike-reservations/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-model-rental-plans", r -> r
                        .path("/api/v1/bike-model-rental-plans/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("bike-model-parts", r -> r
                        .path("/api/v1/bike-model-parts/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Parts & Accessories Management Routes
                .route("parts", r -> r
                        .path("/api/v1/parts/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("part-categories", r -> r
                        .path("/api/v1/part-categories/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("part-brands", r -> r
                        .path("/api/v1/part-brands/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Charging Station Routes
                .route("charging-stations", r -> r
                        .path("/api/v1/charging-stations/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("charging-station-brands", r -> r
                        .path("/api/v1/charging-station-brands/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("charging-station-models", r -> r
                        .path("/api/v1/charging-station-models/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("charging-station-statuses", r -> r
                        .path("/api/v1/charging-station-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Location & Hub Management Routes
                .route("location-service", r -> r
                        .path("/api/v1/location/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))
                
                .route("locations", r -> r
                        .path("/api/v1/locations/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("hubs", r -> r
                        .path("/api/v1/hubs/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("user-locations", r -> r
                        .path("/api/v1/user-locations/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("location-roles", r -> r
                        .path("/api/v1/location-roles/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("coordinates", r -> r
                        .path("/api/v1/coordinates/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("location-images", r -> r
                        .path("/api/v1/location-images/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("hub-images", r -> r
                        .path("/api/v1/hub-images/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Service Management Routes
                .route("services", r -> r
                        .path("/api/v1/services/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("service-products", r -> r
                        .path("/api/v1/service-products/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // B2B Management Routes
                .route("b2b-subscriptions", r -> r
                        .path("/api/v1/b2b-subscriptions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-items", r -> r
                        .path("/api/v1/b2b-subscription-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-statuses", r -> r
                        .path("/api/v1/b2b-subscription-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-sales", r -> r
                        .path("/api/v1/b2b-sales/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-sale-items", r -> r
                        .path("/api/v1/b2b-sale-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-sale-statuses", r -> r
                        .path("/api/v1/b2b-sale-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Stock Management Routes
                .route("stock-movements", r -> r
                        .path("/api/v1/stock-movements/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Lock & Key Management Routes
                .route("locks", r -> r
                        .path("/api/v1/locks/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("lock-providers", r -> r
                        .path("/api/v1/lock-providers/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("lock-statuses", r -> r
                        .path("/api/v1/lock-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("keys", r -> r
                        .path("/api/v1/keys/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Additional B2B Routes
                .route("b2b-subscription-orders", r -> r
                        .path("/api/v1/b2b-subscription-orders/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-order-items", r -> r
                        .path("/api/v1/b2b-subscription-order-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-order-statuses", r -> r
                        .path("/api/v1/b2b-subscription-order-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-sale-orders", r -> r
                        .path("/api/v1/b2b-sale-orders/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-sale-order-statuses", r -> r
                        .path("/api/v1/b2b-sale-order-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                .route("b2b-sale-order-items", r -> r
                        .path("/api/v1/b2b-sale-order-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))

                // Bike Rental Status Routes
                .route("bike-rental-statuses", r -> r
                        .path("/api/v1/bike-rental-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://rental-service"))


                // SUPPORT SERVICE ROUTES

                // Route for fetching support-service API docs (SpringDoc will aggregate)
                .route("support-service-api-docs", r -> r
                        .path("/support-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/support-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://support-service"))

                // Responsible Person Routes
                .route("responsible-persons", r -> r
                        .path("/api/v1/responsible-persons/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Support Request Status Routes
                .route("support-request-statuses", r -> r
                        .path("/api/v1/support-request-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Issue Routes
                .route("bike-issues", r -> r
                        .path("/api/v1/bike-issues/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Error Code Routes
                .route("error-codes", r -> r
                        .path("/api/v1/error-codes/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Support Request Routes
                .route("support-requests", r -> r
                        .path("/api/v1/support-requests/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Feedback Routes
                .route("feedbacks", r -> r
                        .path("/api/v1/feedbacks/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Rental Feedback Routes
                .route("bike-rental-feedbacks", r -> r
                        .path("/api/v1/bike-rental-feedbacks/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Support Request Guide Item Routes
                .route("support-request-guide-items", r -> r
                        .path("/api/v1/support-request-guide-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Type Bike Issue Routes
                .route("bike-type-bike-issues", r -> r
                        .path("/api/v1/bike-type-bike-issues/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Support Request Bike Issue Routes
                .route("support-request-bike-issues", r -> r
                        .path("/api/v1/support-request-bike-issues/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Status Routes
                .route("bike-inspection-statuses", r -> r
                        .path("/api/v1/bike-inspection-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Item Status Routes
                .route("bike-inspection-item-statuses", r -> r
                        .path("/api/v1/bike-inspection-item-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Routes
                .route("bike-inspections", r -> r
                        .path("/api/v1/bike-inspections/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Item Routes
                .route("bike-inspection-items", r -> r
                        .path("/api/v1/bike-inspection-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Item Photo Routes
                .route("bike-inspection-item-photos", r -> r
                        .path("/api/v1/bike-inspection-item-photos/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Item Bike Issue Routes
                .route("bike-inspection-item-bike-issues", r -> r
                        .path("/api/v1/bike-inspection-item-bike-issues/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Unit Routes
                .route("bike-units", r -> r
                        .path("/api/v1/bike-units/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))

                // Bike Inspection Item Bike Unit Routes
                .route("bike-inspection-item-bike-units", r -> r
                        .path("/api/v1/bike-inspection-item-bike-units/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://support-service"))


                // NOTIFICATION SERVICE ROUTES

                // Route for fetching notification-service API docs (SpringDoc will aggregate)
                .route("notification-service-api-docs", r -> r
                        .path("/notification-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/notification-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://notification-service"))

                // Notification Management Routes
                // More specific routes first
                .route("notification-preferences", r -> r
                        .path("/api/v1/notifications/preferences/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://notification-service"))

                .route("notifications", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://notification-service"))


                // PAYMENT SERVICE ROUTES

                // Route for fetching payment-service API docs (SpringDoc will aggregate)
                .route("payment-service-api-docs", r -> r
                        .path("/payment-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/payment-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://payment-service"))

                // Public Stripe Webhook Route - No JWT validation required
                .route("stripe-webhooks", r -> r
                        .path("/api/v1/webhooks/stripe/**")
                        .uri("lb://payment-service"))

                // Public MultiSafePay Webhook Route - No JWT validation required
                .route("multisafepay-webhooks", r -> r
                        .path("/api/v1/webhooks/multisafepay/**")
                        .uri("lb://payment-service"))

                // Public MultiSafePay Test Routes - For development/testing (should be disabled in production)
                .route("multisafepay-test", r -> r
                        .path("/api/v1/multisafepay/test/**")
                        .uri("lb://payment-service"))

                // Public Mobile Payment Test Routes - For development/testing (should be disabled in production)
                .route("mobile-payments-test", r -> r
                        .path("/api/v1/payments/mobile/test/**")
                        .uri("lb://payment-service"))

                // Protected MultiSafePay Production Routes - Requires JWT authentication
                .route("multisafepay-production", r -> r
                        .path("/api/v1/multisafepay/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Mobile Payment Routes - Protected, requires JWT authentication
                .route("mobile-payments", r -> r
                        .path("/api/v1/payments/mobile/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Reference Data Routes
                .route("currencies", r -> r
                        .path("/api/v1/currencies/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                .route("payment-statuses", r -> r
                        .path("/api/v1/payment-statuses/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                .route("payment-methods", r -> r
                        .path("/api/v1/payment-methods/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                .route("service-providers", r -> r
                        .path("/api/v1/service-providers/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Financial Transaction Routes
                .route("financial-transactions", r -> r
                        .path("/api/v1/financial-transactions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // User Payment Profile Routes
                .route("user-payment-profiles", r -> r
                        .path("/api/v1/user-payment-profiles/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // User Payment Method Routes
                .route("user-payment-methods", r -> r
                        .path("/api/v1/user-payment-methods/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Junction Table Routes - Rental Financial Transactions
                .route("rental-fin-transactions", r -> r
                        .path("/api/v1/rental-fin-transactions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Junction Table Routes - B2B Sale Financial Transactions
                .route("b2b-sale-fin-transactions", r -> r
                        .path("/api/v1/b2b-sale-fin-transactions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Junction Table Routes - B2B Subscription Financial Transactions
                .route("b2b-subscription-fin-transactions", r -> r
                        .path("/api/v1/b2b-subscription-fin-transactions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // B2B Revenue Share Payout Routes
                .route("b2b-revenue-share-payouts", r -> r
                        .path("/api/v1/b2b-revenue-share-payouts/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // B2B Revenue Share Payout Item Routes
                .route("b2b-revenue-share-payout-items", r -> r
                        .path("/api/v1/b2b-revenue-share-payout-items/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                // Junction Table Routes - Payout Financial Transactions
                .route("payout-fin-transactions", r -> r
                        .path("/api/v1/payout-fin-transactions/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(userRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                        .uri("lb://payment-service"))

                .build();
    }
}


