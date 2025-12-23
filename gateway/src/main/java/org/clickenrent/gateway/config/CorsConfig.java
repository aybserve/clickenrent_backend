package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS Configuration for Spring Cloud Gateway.
 * Allows Swagger UI and other browser-based clients to make requests to backend services.
 */
@Slf4j
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        log.info("Configuring CORS filter for Gateway...");

        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Allow development origins
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "http://localhost:3000",
                "http://127.0.0.1:8080",
                "http://127.0.0.1:3000"
        ));
        
        // Allow all HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", 
                "POST", 
                "PUT", 
                "DELETE", 
                "PATCH", 
                "OPTIONS"
        ));
        
        // Allow common headers
        corsConfig.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        
        // Expose headers that might be needed by the client
        corsConfig.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        
        // Allow credentials (cookies, authorization headers, etc.)
        corsConfig.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Apply CORS configuration to API routes
        source.registerCorsConfiguration("/api/**", corsConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/auth-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/rental-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/payment-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/support-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/swagger-ui/**", corsConfig);
        source.registerCorsConfiguration("/webjars/**", corsConfig);
        
        log.info("CORS filter configured successfully for development origins");
        
        return new CorsWebFilter(source);
    }
}

