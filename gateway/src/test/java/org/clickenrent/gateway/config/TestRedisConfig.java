package org.clickenrent.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test configuration that provides mock Redis beans so the gateway context
 * starts without a real Redis server. Rate limiters receive a template
 * whose execute() returns "allowed" (1, remaining, capacity, rate).
 */
@Configuration
@Profile("test")
public class TestRedisConfig {

    /**
     * Script returns: allowed_num, new_tokens, capacity, rate.
     * 1L = allowed, 10L = tokens left, 30L = capacity, 20L = rate.
     */
    private static final List<Long> RATE_LIMITER_ALLOWED_RESPONSE = List.of(1L, 10L, 30L, 20L);

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        ReactiveRedisConnectionFactory factory = mock(ReactiveRedisConnectionFactory.class);
        ReactiveRedisConnection connection = mock(ReactiveRedisConnection.class);
        when(factory.getReactiveConnection()).thenReturn(connection);
        when(connection.closeLater()).thenReturn(Mono.empty());
        return factory;
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate() {
        ReactiveStringRedisTemplate template = mock(ReactiveStringRedisTemplate.class);
        when(template.execute(any(RedisScript.class), anyList(), anyList()))
                .thenReturn(Flux.just(RATE_LIMITER_ALLOWED_RESPONSE));
        return template;
    }
}
