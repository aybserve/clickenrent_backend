package org.clickenrent.gateway.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized logging for rate limiting events.
 * Provides structured logging for monitoring and analysis.
 */
@Slf4j
@Component
public class RateLimitLogger {
    
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    
    /**
     * Log a rate limit violation.
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     * @param path Request path
     * @param method HTTP method
     * @param retryAfter Seconds to wait before retrying
     */
    public void logViolation(String ip, String userId, String path, String method, long retryAfter) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "RATE_LIMIT_VIOLATION");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("ip", ip);
        logData.put("userId", userId != null ? userId : "anonymous");
        logData.put("path", path);
        logData.put("method", method);
        logData.put("retryAfter", retryAfter);
        
        log.warn("Rate limit violation: {}", formatLogData(logData));
    }
    
    /**
     * Log a burst usage pattern.
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     * @param requestCount Number of requests in burst
     * @param timeWindowSeconds Time window of the burst
     */
    public void logBurstPattern(String ip, String userId, int requestCount, int timeWindowSeconds) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "BURST_PATTERN_DETECTED");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("ip", ip);
        logData.put("userId", userId != null ? userId : "anonymous");
        logData.put("requestCount", requestCount);
        logData.put("timeWindowSeconds", timeWindowSeconds);
        logData.put("requestsPerSecond", (double) requestCount / timeWindowSeconds);
        
        log.info("Burst pattern detected: {}", formatLogData(logData));
    }
    
    /**
     * Log a repeated offender (IP with multiple violations).
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     * @param violationCount Total number of violations
     */
    public void logRepeatedOffender(String ip, String userId, int violationCount) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "REPEATED_OFFENDER");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("ip", ip);
        logData.put("userId", userId != null ? userId : "anonymous");
        logData.put("violationCount", violationCount);
        
        log.warn("Repeated offender detected: {}", formatLogData(logData));
    }
    
    /**
     * Log an IP block event.
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     * @param blockDurationMinutes Duration of the block
     * @param reason Reason for blocking
     */
    public void logIpBlocked(String ip, String userId, int blockDurationMinutes, String reason) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "IP_BLOCKED");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("ip", ip);
        logData.put("userId", userId != null ? userId : "anonymous");
        logData.put("blockDurationMinutes", blockDurationMinutes);
        logData.put("reason", reason);
        
        log.error("IP blocked: {}", formatLogData(logData));
    }
    
    /**
     * Log a Redis connection issue.
     * 
     * @param error Error message
     * @param operation Operation that failed
     */
    public void logRedisError(String error, String operation) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "REDIS_ERROR");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("error", error);
        logData.put("operation", operation);
        
        log.error("Redis connection error: {}", formatLogData(logData));
    }
    
    /**
     * Log successful rate limit check.
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     * @param path Request path
     * @param remaining Remaining requests allowed
     */
    public void logSuccess(String ip, String userId, String path, long remaining) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "RATE_LIMIT_OK");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("ip", ip);
        logData.put("userId", userId != null ? userId : "anonymous");
        logData.put("path", path);
        logData.put("remaining", remaining);
        
        log.debug("Rate limit check passed: {}", formatLogData(logData));
    }
    
    /**
     * Log rate limiter configuration.
     * 
     * @param limiterType Type of rate limiter (IP or User)
     * @param replenishRate Replenish rate
     * @param burstCapacity Burst capacity
     */
    public void logConfiguration(String limiterType, int replenishRate, int burstCapacity) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "RATE_LIMITER_CONFIGURED");
        logData.put("timestamp", FORMATTER.format(Instant.now()));
        logData.put("limiterType", limiterType);
        logData.put("replenishRate", replenishRate);
        logData.put("burstCapacity", burstCapacity);
        
        log.info("Rate limiter configured: {}", formatLogData(logData));
    }
    
    /**
     * Format log data as a readable string.
     * 
     * @param logData Map of log data
     * @return Formatted string
     */
    private String formatLogData(Map<String, Object> logData) {
        StringBuilder sb = new StringBuilder();
        logData.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(key).append("=").append(value);
        });
        return sb.toString();
    }
}
