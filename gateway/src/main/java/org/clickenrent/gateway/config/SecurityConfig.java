package org.clickenrent.gateway.config;

import lombok.RequiredArgsConstructor;
import org.clickenrent.gateway.security.SwaggerAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for Gateway.
 * Protects Swagger UI with HTTP Basic Authentication while leaving API endpoints open.
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final SwaggerAuthenticationManager swaggerAuthenticationManager;
    
    /**
     * Single security filter chain with path-based authorization.
     * Swagger paths require HTTP Basic Authentication, all others are permitted.
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        // Swagger endpoints require authentication
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").authenticated()
                        // All other endpoints are permitted (security handled by microservices)
                        .anyExchange().permitAll()
                )
                .httpBasic(basic -> basic
                        .authenticationManager(swaggerAuthenticationManager)
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
