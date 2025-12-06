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

                // AUTH SERVICE ROUTES

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


                // RENTAL SERVICE ROUTES

                
                // Route for fetching rental-service API docs (SpringDoc will aggregate)
                .route("rental-service-api-docs", r -> r
                        .path("/rental-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/rental-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://rental-service"))

                // Bike Management Routes
                .route("bikes", r -> r
                        .path("/api/bikes/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-types", r -> r
                        .path("/api/bike-types/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-models", r -> r
                        .path("/api/bike-models/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-brands", r -> r
                        .path("/api/bike-brands/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-engines", r -> r
                        .path("/api/bike-engines/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-statuses", r -> r
                        .path("/api/bike-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Rental & Ride Management Routes
                .route("rentals", r -> r
                        .path("/api/rentals/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-rentals", r -> r
                        .path("/api/bike-rentals/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("rides", r -> r
                        .path("/api/rides/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("rental-units", r -> r
                        .path("/api/rental-units/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("rental-plans", r -> r
                        .path("/api/rental-plans/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("rental-statuses", r -> r
                        .path("/api/rental-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("ride-statuses", r -> r
                        .path("/api/ride-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-reservations", r -> r
                        .path("/api/bike-reservations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("bike-model-rental-plans", r -> r
                        .path("/api/bike-model-rental-plans/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Parts & Accessories Management Routes
                .route("parts", r -> r
                        .path("/api/parts/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("part-types", r -> r
                        .path("/api/part-types/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("part-categories", r -> r
                        .path("/api/part-categories/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("part-brands", r -> r
                        .path("/api/part-brands/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("part-models", r -> r
                        .path("/api/part-models/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Charging Station Routes
                .route("charging-stations", r -> r
                        .path("/api/charging-stations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("charging-station-brands", r -> r
                        .path("/api/charging-station-brands/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("charging-station-models", r -> r
                        .path("/api/charging-station-models/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("charging-station-statuses", r -> r
                        .path("/api/charging-station-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("battery-charge-statuses", r -> r
                        .path("/api/battery-charge-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Location & Hub Management Routes
                .route("locations", r -> r
                        .path("/api/locations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("hubs", r -> r
                        .path("/api/hubs/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("user-locations", r -> r
                        .path("/api/user-locations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("location-roles", r -> r
                        .path("/api/location-roles/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("coordinates", r -> r
                        .path("/api/coordinates/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("location-images", r -> r
                        .path("/api/location-images/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("hub-images", r -> r
                        .path("/api/hub-images/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Service Management Routes
                .route("services", r -> r
                        .path("/api/services/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("service-products", r -> r
                        .path("/api/service-products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // B2B Management Routes
                .route("b2b-subscriptions", r -> r
                        .path("/api/b2b-subscriptions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-items", r -> r
                        .path("/api/b2b-subscription-items/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-statuses", r -> r
                        .path("/api/b2b-subscription-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-sales", r -> r
                        .path("/api/b2b-sales/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-sale-products", r -> r
                        .path("/api/b2b-sale-products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-sale-statuses", r -> r
                        .path("/api/b2b-sale-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Stock Management Routes
                .route("stock-movements", r -> r
                        .path("/api/stock-movements/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Lock & Key Management Routes
                .route("locks", r -> r
                        .path("/api/locks/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("keys", r -> r
                        .path("/api/keys/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .build();
    }
}

