package org.clickenrent.authservice.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration for rate limiting OAuth endpoints.
 * Uses Bucket4j for token bucket algorithm implementation.
 */
@Configuration
@Getter
public class RateLimitConfig {
    
    @Value("${oauth2.rate-limit.enabled:true}")
    private boolean enabled;
    
    @Value("${oauth2.rate-limit.capacity:10}")
    private long capacity;
    
    @Value("${oauth2.rate-limit.refill-tokens:10}")
    private long refillTokens;
    
    @Value("${oauth2.rate-limit.refill-duration-minutes:1}")
    private long refillDurationMinutes;
    
    // In-memory cache of buckets per IP address
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    /**
     * Resolve a bucket for the given IP address.
     * Creates a new bucket if one doesn't exist for this IP.
     * 
     * @param ip Client IP address
     * @return Bucket for rate limiting
     */
    public Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> createNewBucket());
    }
    
    /**
     * Create a new bucket with configured limits.
     * Uses token bucket algorithm:
     * - Initial capacity: number of requests allowed in burst
     * - Refill: tokens added over time
     * 
     * @return New Bucket instance
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(refillTokens, Duration.ofMinutes(refillDurationMinutes))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    /**
     * Clear the bucket cache (useful for testing or maintenance).
     */
    public void clearCache() {
        cache.clear();
    }
}

