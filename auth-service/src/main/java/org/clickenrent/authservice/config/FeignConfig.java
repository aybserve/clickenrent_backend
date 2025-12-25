package org.clickenrent.authservice.config;

import feign.Logger;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuration for Feign clients.
 */
@Configuration
@Slf4j
public class FeignConfig {

    /**
     * Enable full Feign logging for debugging.
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Interceptor to propagate JWT token to other services.
     * Extracts the Authorization header from the incoming request and forwards it.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    log.debug("Propagating Authorization header to Feign client");
                    requestTemplate.header("Authorization", authHeader);
                } else {
                    log.warn("No valid Authorization header found in request");
                }
            } else {
                log.warn("No request attributes available for Feign interceptor");
            }
        };
    }
}

