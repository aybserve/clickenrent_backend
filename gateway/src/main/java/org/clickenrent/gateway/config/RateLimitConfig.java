package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.clickenrent.gateway.ratelimit.CustomRedisRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

/**
 * Configuration for rate limiting using Redis.
 * Implements dual strategy: IP-based (anonymous) and User-based (authenticated).
 * Enabled by default, can be disabled by setting rate-limit.enabled=false
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitConfig {
    
    @Value("${rate-limit.ip.replenish-rate:20}")
    private int ipReplenishRate;
    
    @Value("${rate-limit.ip.burst-capacity:30}")
    private int ipBurstCapacity;
    
    @Value("${rate-limit.user.replenish-rate:50}")
    private int userReplenishRate;
    
    @Value("${rate-limit.user.burst-capacity:100}")
    private int userBurstCapacity;
    
    @Value("${rate-limit.ip.ttl-seconds:30}")
    private int ipTtlSeconds;
    
    @Value("${rate-limit.user.ttl-seconds:60}")
    private int userTtlSeconds;
    
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
     * - ttlSeconds: how long keys persist in Redis (custom TTL)
     */
    @Bean(name = "ipRateLimiter")
    public CustomRedisRateLimiter ipRateLimiter(ReactiveStringRedisTemplate redisTemplate) {
        log.info("Configuring IP-based rate limiter: {} req/sec, burst: {}, TTL: {}s", 
                ipReplenishRate, ipBurstCapacity, ipTtlSeconds);
        
        CustomRedisRateLimiter rateLimiter = new CustomRedisRateLimiter(
                ipReplenishRate, 
                ipBurstCapacity, 
                ipTtlSeconds,
                redisTemplate
        );
        rateLimiter.setInitialized(true);
        return rateLimiter;
    }
    
    /**
     * Redis rate limiter for user-based rate limiting (authenticated endpoints).
     * More lenient limits for authenticated users.
     * Marked as @Primary to be used as the default rate limiter.
     * 
     * Configuration:
     * - replenishRate: tokens per second (steady state rate)
     * - burstCapacity: max tokens in bucket (allows short bursts)
     * - ttlSeconds: how long keys persist in Redis (custom TTL)
     */
    @Bean(name = "userRateLimiter")
    @Primary
    public CustomRedisRateLimiter userRateLimiter(ReactiveStringRedisTemplate redisTemplate) {
        log.info("Configuring User-based rate limiter: {} req/sec, burst: {}, TTL: {}s", 
                userReplenishRate, userBurstCapacity, userTtlSeconds);
        
        CustomRedisRateLimiter rateLimiter = new CustomRedisRateLimiter(
                userReplenishRate, 
                userBurstCapacity, 
                userTtlSeconds,
                redisTemplate
        );
        rateLimiter.setInitialized(true);
        return rateLimiter;
    }
}
