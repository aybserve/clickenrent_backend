package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for Spring Cloud Gateway.
 * Allows Swagger UI and other browser-based clients to make requests to backend services.
 * Uses environment variables for flexible configuration across environments.
 */
@Slf4j
@Configuration
public class CorsConfig {
    
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;
    
    @Value("${cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsWebFilter corsWebFilter() {
        log.info("Configuring CORS filter for Gateway...");

        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Parse allowed origins from environment variable
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        corsConfig.setAllowedOrigins(origins);
        
        // Allow origin patterns for wildcard subdomain support
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
                "https://*.aybserve.com",
                "https://*.vercel.app",
                "https://*.readme.io",
                "http://localhost:[*]",
                "http://127.0.0.1:[*]",
                "http://*.aybserve.com"
        ));
        
        log.info("CORS allowed origins: {}", origins);
        log.info("CORS allowed origin patterns: https://*.aybserve.com, http://*.aybserve.com, http://localhost:[*]");
        
        // Parse allowed methods from environment variable
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        corsConfig.setAllowedMethods(methods);
        
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
        
        // Cache preflight response (configurable via environment)
        corsConfig.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Apply CORS configuration to API routes
        source.registerCorsConfiguration("/api/**", corsConfig);
        source.registerCorsConfiguration("/api/v1/**", corsConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/auth-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/rental-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/payment-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/support-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/notification-service/v3/api-docs/**", corsConfig);
        source.registerCorsConfiguration("/swagger-ui/**", corsConfig);
        source.registerCorsConfiguration("/webjars/**", corsConfig);
        
        log.info("CORS filter configured successfully");
        log.info("CORS max age: {} seconds", maxAge);
        log.info("CORS allowed methods: {}", methods);
        
        return new CorsWebFilter(source);
    }
}

