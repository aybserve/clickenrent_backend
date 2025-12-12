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
                
                // Public Invitation Routes - No JWT validation required
                .route("invitation-public-validate", r -> r
                        .path("/api/invitations/validate/**")
                        .uri("lb://auth-service"))
                
                .route("invitation-public-complete", r -> r
                        .path("/api/invitations/complete")
                        .uri("lb://auth-service"))

                // Protected Auth Routes - JWT validation required
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
                
                // Invitation Management Routes (Protected - requires JWT)
                // Note: Specific protected invitation routes, not catch-all
                .route("invitations-create", r -> r
                        .path("/api/invitations")
                        .and().method("POST")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))
                
                .route("invitations-list", r -> r
                        .path("/api/invitations")
                        .and().method("GET")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))
                
                .route("invitations-cancel", r -> r
                        .path("/api/invitations/*")
                        .and().method("DELETE")
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

                // Bike Parts Routes
                .route("bike-parts", r -> r
                        .path("/api/bike-parts/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Additional B2B Routes
                .route("b2b-subscription-orders", r -> r
                        .path("/api/b2b-subscription-orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-order-items", r -> r
                        .path("/api/b2b-subscription-order-items/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-subscription-order-statuses", r -> r
                        .path("/api/b2b-subscription-order-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-sale-orders", r -> r
                        .path("/api/b2b-sale-orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-sale-order-statuses", r -> r
                        .path("/api/b2b-sale-order-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                .route("b2b-sale-order-product-models", r -> r
                        .path("/api/b2b-sale-order-product-models/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))

                // Bike Rental Status Routes
                .route("bike-rental-statuses", r -> r
                        .path("/api/bike-rental-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://rental-service"))


                // SUPPORT SERVICE ROUTES

                // Route for fetching support-service API docs (SpringDoc will aggregate)
                .route("support-service-api-docs", r -> r
                        .path("/support-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/support-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://support-service"))

                // Responsible Person Routes
                .route("responsible-persons", r -> r
                        .path("/api/responsible-persons/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Support Request Status Routes
                .route("support-request-statuses", r -> r
                        .path("/api/support-request-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Bike Issue Routes
                .route("bike-issues", r -> r
                        .path("/api/bike-issues/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Error Code Routes
                .route("error-codes", r -> r
                        .path("/api/error-codes/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Support Request Routes
                .route("support-requests", r -> r
                        .path("/api/support-requests/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Feedback Routes
                .route("feedbacks", r -> r
                        .path("/api/feedbacks/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Bike Rental Feedback Routes
                .route("bike-rental-feedbacks", r -> r
                        .path("/api/bike-rental-feedbacks/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Support Request Guide Item Routes
                .route("support-request-guide-items", r -> r
                        .path("/api/support-request-guide-items/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Bike Type Bike Issue Routes
                .route("bike-type-bike-issues", r -> r
                        .path("/api/bike-type-bike-issues/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))

                // Support Request Bike Issue Routes
                .route("support-request-bike-issues", r -> r
                        .path("/api/support-request-bike-issues/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://support-service"))


                // PAYMENT SERVICE ROUTES

                // Route for fetching payment-service API docs (SpringDoc will aggregate)
                .route("payment-service-api-docs", r -> r
                        .path("/payment-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/payment-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://payment-service"))

                // Public Stripe Webhook Route - No JWT validation required
                .route("stripe-webhooks", r -> r
                        .path("/api/webhooks/stripe/**")
                        .uri("lb://payment-service"))

                // Reference Data Routes
                .route("currencies", r -> r
                        .path("/api/currencies/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                .route("payment-statuses", r -> r
                        .path("/api/payment-statuses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                .route("payment-methods", r -> r
                        .path("/api/payment-methods/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                .route("service-providers", r -> r
                        .path("/api/service-providers/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // Financial Transaction Routes
                .route("financial-transactions", r -> r
                        .path("/api/financial-transactions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // User Payment Profile Routes
                .route("user-payment-profiles", r -> r
                        .path("/api/user-payment-profiles/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // User Payment Method Routes
                .route("user-payment-methods", r -> r
                        .path("/api/user-payment-methods/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // Junction Table Routes - Rental Financial Transactions
                .route("rental-fin-transactions", r -> r
                        .path("/api/rental-fin-transactions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // Junction Table Routes - B2B Sale Financial Transactions
                .route("b2b-sale-fin-transactions", r -> r
                        .path("/api/b2b-sale-fin-transactions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // Junction Table Routes - B2B Subscription Financial Transactions
                .route("b2b-subscription-fin-transactions", r -> r
                        .path("/api/b2b-subscription-fin-transactions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // B2B Revenue Share Payout Routes
                .route("b2b-revenue-share-payouts", r -> r
                        .path("/api/b2b-revenue-share-payouts/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // B2B Revenue Share Payout Item Routes
                .route("b2b-revenue-share-payout-items", r -> r
                        .path("/api/b2b-revenue-share-payout-items/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                // Junction Table Routes - Payout Financial Transactions
                .route("payout-fin-transactions", r -> r
                        .path("/api/payout-fin-transactions/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-service"))

                .build();
    }
}


