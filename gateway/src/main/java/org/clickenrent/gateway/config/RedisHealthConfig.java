package org.clickenrent.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

import java.time.Duration;

/**
 * Redis health check configuration.
 * Monitors Redis connectivity and provides health status.
 */
@Slf4j
@Configuration
public class RedisHealthConfig {
    
    @Autowired
    private ReactiveRedisConnectionFactory redisConnectionFactory;
    
    /**
     * Custom health indicator for Redis connection.
     * Provides detailed health information about Redis connectivity.
     * 
     * Note: Spring Boot Actuator provides a default Redis health indicator,
     * but this custom one provides additional details.
     */
    @Bean
    public HealthIndicator customRedisHealthIndicator() {
        return () -> {
            try {
                // Test Redis connection by attempting to get a connection
                redisConnectionFactory.getReactiveConnection()
                        .closeLater()
                        .timeout(Duration.ofSeconds(2))
                        .block();
                
                log.debug("Redis health check: OK");
                return Health.up()
                        .withDetail("redis", "Available")
                        .withDetail("connectionFactory", redisConnectionFactory.getClass().getSimpleName())
                        .build();
            } catch (Exception e) {
                log.error("Redis health check failed: {}", e.getMessage());
                return Health.down()
                        .withDetail("redis", "Unavailable")
                        .withDetail("error", e.getMessage())
                        .withDetail("errorType", e.getClass().getSimpleName())
                        .build();
            }
        };
    }
}
