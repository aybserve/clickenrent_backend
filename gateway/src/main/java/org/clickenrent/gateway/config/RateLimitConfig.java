package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Configuration for rate limiting using Redis.
 * Implements dual strategy: IP-based (anonymous) and User-based (authenticated).
 * Only enabled when rate-limit.enabled=true
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = false)
public class RateLimitConfig {
    
    @Value("${rate-limit.ip.replenish-rate:20}")
    private int ipReplenishRate;
    
    @Value("${rate-limit.ip.burst-capacity:30}")
    private int ipBurstCapacity;
    
    @Value("${rate-limit.user.replenish-rate:50}")
    private int userReplenishRate;
    
    @Value("${rate-limit.user.burst-capacity:100}")
    private int userBurstCapacity;
    
    /**
     * Key resolver for rate limiting based on client IP address.
     * Used for anonymous/public endpoints.
     * Supports X-Forwarded-For header for proxy/load balancer scenarios.
     */
    @Bean(name = "ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = exchange.getRequest().getRemoteAddress() != null 
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            }
            log.debug("Rate limit key (IP): {}", ip);
            return Mono.just("ip:" + ip);
        };
    }
    
    /**
     * Key resolver for rate limiting based on user ID from JWT token.
     * Used for authenticated endpoints.
     * Falls back to IP if user ID is not available.
     * Marked as @Primary to be used as the default key resolver.
     */
    @Bean(name = "userKeyResolver")
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Try to get user ID from JWT claims (set by JwtAuthenticationFilter)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            
            if (userId != null && !userId.isEmpty()) {
                log.debug("Rate limit key (User): {}", userId);
                return Mono.just("user:" + userId);
            }
            
            // Fallback to IP if user ID not available
            String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = exchange.getRequest().getRemoteAddress() != null 
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            }
            log.debug("Rate limit key (User fallback to IP): {}", ip);
            return Mono.just("ip:" + ip);
        };
    }
    
    /**
     * Redis rate limiter for IP-based rate limiting (public endpoints).
     * Stricter limits to prevent abuse on anonymous endpoints.
     * 
     * Configuration:
     * - replenishRate: tokens per second (steady state rate)
     * - burstCapacity: max tokens in bucket (allows short bursts)
     */
    @Bean(name = "ipRateLimiter")
    public RedisRateLimiter ipRateLimiter() {
        log.info("Configuring IP-based rate limiter: {} req/sec, burst: {}", 
                ipReplenishRate, ipBurstCapacity);
        return new RedisRateLimiter(ipReplenishRate, ipBurstCapacity);
    }
    
    /**
     * Redis rate limiter for user-based rate limiting (authenticated endpoints).
     * More lenient limits for authenticated users.
     * Marked as @Primary to be used as the default rate limiter.
     * 
     * Configuration:
     * - replenishRate: tokens per second (steady state rate)
     * - burstCapacity: max tokens in bucket (allows short bursts)
     */
    @Bean(name = "userRateLimiter")
    @Primary
    public RedisRateLimiter userRateLimiter() {
        log.info("Configuring User-based rate limiter: {} req/sec, burst: {}", 
                userReplenishRate, userBurstCapacity);
        return new RedisRateLimiter(userReplenishRate, userBurstCapacity);
    }
}
