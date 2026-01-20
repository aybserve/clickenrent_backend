package org.clickenrent.authservice.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

/**
 * Configuration for Resilience4j retry mechanism.
 * Provides retry logic for Google OAuth API calls with exponential backoff.
 */
@Configuration
@Slf4j
public class Resilience4jConfig {
    
    /**
     * Create retry configuration for Google OAuth API calls.
     * Configured with:
     * - Max attempts: 3
     * - Initial wait: 1 second
     * - Exponential backoff multiplier: 2
     * - Max wait: 10 seconds
     * - Retry on: Network errors, 5xx server errors, rate limits (429)
     * - Don't retry on: 4xx client errors (except 429)
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                        Duration.ofSeconds(1),  // Initial interval
                        2.0                      // Multiplier
                ))
                .retryOnException(throwable -> {
                    // Retry on network errors
                    if (throwable instanceof ResourceAccessException) {
                        log.warn("Retrying due to network error: {}", throwable.getMessage());
                        return true;
                    }
                    
                    // Retry on 5xx server errors
                    if (throwable instanceof HttpServerErrorException) {
                        log.warn("Retrying due to server error: {}", throwable.getMessage());
                        return true;
                    }
                    
                    // Retry on rate limiting (429)
                    if (throwable instanceof HttpClientErrorException.TooManyRequests) {
                        log.warn("Retrying due to rate limiting: {}", throwable.getMessage());
                        return true;
                    }
                    
                    // Don't retry on other client errors (4xx)
                    if (throwable instanceof HttpClientErrorException) {
                        log.debug("Not retrying client error: {}", throwable.getMessage());
                        return false;
                    }
                    
                    // Default: don't retry
                    return false;
                })
                .failAfterMaxAttempts(true)
                .build();
        
        return RetryRegistry.of(config);
    }
    
    /**
     * Create specific retry instance for Google OAuth token exchange.
     */
    @Bean(name = "googleTokenExchangeRetry")
    public Retry googleTokenExchangeRetry(RetryRegistry retryRegistry) {
        Retry retry = retryRegistry.retry("googleTokenExchange");
        
        // Add event listeners for monitoring
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt {} for token exchange: {}", 
                        event.getNumberOfRetryAttempts(), 
                        event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "unknown"))
                .onError(event -> log.error("All retry attempts exhausted for token exchange: {}", 
                        event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "unknown"));
        
        return retry;
    }
    
    /**
     * Create specific retry instance for Google user info fetch.
     */
    @Bean(name = "googleUserInfoRetry")
    public Retry googleUserInfoRetry(RetryRegistry retryRegistry) {
        Retry retry = retryRegistry.retry("googleUserInfo");
        
        // Add event listeners for monitoring
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt {} for user info fetch: {}", 
                        event.getNumberOfRetryAttempts(), 
                        event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "unknown"))
                .onError(event -> log.error("All retry attempts exhausted for user info fetch: {}", 
                        event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "unknown"));
        
        return retry;
    }
}
