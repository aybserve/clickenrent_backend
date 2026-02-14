package org.clickenrent.gateway.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Custom Redis Rate Limiter with configurable TTL.
 * 
 * Based on Spring Cloud Gateway's RedisRateLimiter but with enhanced TTL control.
 * Uses the token bucket algorithm with Redis for distributed rate limiting.
 */
@Slf4j
public class CustomRedisRateLimiter extends AbstractRateLimiter<CustomRedisRateLimiter.Config> {

    public static final String CONFIGURATION_PROPERTY_NAME = "custom-redis-rate-limiter";
    public static final String REDIS_SCRIPT_NAME = "redisRequestRateLimiterScript";
    public static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";
    public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";
    public static final String REQUESTED_TOKENS_HEADER = "X-RateLimit-Requested-Tokens";

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisScript<List<Long>> script;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Config defaultConfig;

    /**
     * Lua script for token bucket rate limiting with custom TTL.
     * 
     * This script:
     * 1. Gets current tokens and timestamp from Redis
     * 2. Calculates tokens to add based on time elapsed
     * 3. Checks if request can be allowed
     * 4. Updates Redis with new values
     * 5. Sets custom TTL on keys (configurable, not based on rate)
     */
    private static final String REDIS_LUA_SCRIPT = """
            local tokens_key = KEYS[1]
            local timestamp_key = KEYS[2]
            
            local rate = tonumber(ARGV[1])
            local capacity = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local requested = tonumber(ARGV[4])
            local ttl_seconds = tonumber(ARGV[5])
            
            local fill_time = capacity / rate
            local ttl = math.floor(ttl_seconds)
            
            local last_tokens = tonumber(redis.call("get", tokens_key))
            if last_tokens == nil then
                last_tokens = capacity
            end
            
            local last_refreshed = tonumber(redis.call("get", timestamp_key))
            if last_refreshed == nil then
                last_refreshed = 0
            end
            
            local delta = math.max(0, now - last_refreshed)
            local filled_tokens = math.min(capacity, last_tokens + (delta * rate))
            local allowed = filled_tokens >= requested
            local new_tokens = filled_tokens
            local allowed_num = 0
            
            if allowed then
                new_tokens = filled_tokens - requested
                allowed_num = 1
            end
            
            redis.call("setex", tokens_key, ttl, new_tokens)
            redis.call("setex", timestamp_key, ttl, now)
            
            return {allowed_num, new_tokens, capacity, rate}
            """;

    public CustomRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                   RedisScript<List<Long>> script,
                                   Config defaultConfig) {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
        this.redisTemplate = redisTemplate;
        this.script = script;
        this.defaultConfig = defaultConfig;
    }

    @SuppressWarnings("unchecked")
    public CustomRedisRateLimiter(int defaultReplenishRate, 
                                   int defaultBurstCapacity,
                                   int defaultTtlSeconds,
                                   ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
        this.redisTemplate = redisTemplate;
        this.script = (RedisScript<List<Long>>) (Object) RedisScript.of(REDIS_LUA_SCRIPT, List.class);
        this.defaultConfig = new Config()
                .setReplenishRate(defaultReplenishRate)
                .setBurstCapacity(defaultBurstCapacity)
                .setRequestedTokens(1)
                .setTtlSeconds(defaultTtlSeconds);
    }

    static List<String> getKeys(String id) {
        String prefix = "request_rate_limiter.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Response> isAllowed(String routeId, String id) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("RedisRateLimiter is not initialized");
        }

        Config routeConfig = loadConfiguration(routeId);

        int replenishRate = routeConfig.getReplenishRate();
        int burstCapacity = routeConfig.getBurstCapacity();
        int requestedTokens = routeConfig.getRequestedTokens();
        int ttlSeconds = routeConfig.getTtlSeconds();

        List<String> keys = getKeys(id);
        List<String> scriptArgs = Arrays.asList(
                String.valueOf(replenishRate),
                String.valueOf(burstCapacity),
                String.valueOf(Instant.now().getEpochSecond()),
                String.valueOf(requestedTokens),
                String.valueOf(ttlSeconds)
        );

        Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);

        return flux.onErrorResume(throwable -> {
            if (log.isDebugEnabled()) {
                log.debug("Error calling rate limiter lua script", throwable);
            }
            return Flux.just(Arrays.asList(1L, -1L, (long) burstCapacity, (long) replenishRate));
        }).reduce(new ArrayList<Long>(), (longs, l) -> {
            longs.addAll(l);
            return longs;
        }).map(results -> {
            boolean allowed = results.get(0) == 1L;
            Long tokensLeft = results.get(1);
            Long capacity = results.get(2);
            Long rate = results.get(3);

            Response response = new Response(allowed, getHeaders(capacity, rate, tokensLeft, requestedTokens));

            if (log.isDebugEnabled()) {
                log.debug("Response: {}, tokens left: {}, capacity: {}, rate: {}, ttl: {}s",
                        response, tokensLeft, capacity, rate, ttlSeconds);
            }

            return response;
        });
    }

    private Config loadConfiguration(String routeId) {
        Config routeConfig = getConfig().getOrDefault(routeId, defaultConfig);
        if (routeConfig == null) {
            routeConfig = defaultConfig;
        }
        return routeConfig;
    }

    private java.util.Map<String, String> getHeaders(Long capacity, Long rate, Long tokensLeft, int requestedTokens) {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put(REMAINING_HEADER, tokensLeft.toString());
        headers.put(REPLENISH_RATE_HEADER, rate.toString());
        headers.put(BURST_CAPACITY_HEADER, capacity.toString());
        headers.put(REQUESTED_TOKENS_HEADER, String.valueOf(requestedTokens));
        return headers;
    }

    public void setInitialized(boolean initialized) {
        this.initialized.set(initialized);
    }

    /**
     * Configuration for the rate limiter.
     */
    public static class Config {
        private int replenishRate;
        private int burstCapacity;
        private int requestedTokens = 1;
        private int ttlSeconds = 60; // Default TTL: 60 seconds

        public int getReplenishRate() {
            return replenishRate;
        }

        public Config setReplenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
            return this;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public Config setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
            return this;
        }

        public int getRequestedTokens() {
            return requestedTokens;
        }

        public Config setRequestedTokens(int requestedTokens) {
            this.requestedTokens = requestedTokens;
            return this;
        }

        public int getTtlSeconds() {
            return ttlSeconds;
        }

        public Config setTtlSeconds(int ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
            return this;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "replenishRate=" + replenishRate +
                    ", burstCapacity=" + burstCapacity +
                    ", requestedTokens=" + requestedTokens +
                    ", ttlSeconds=" + ttlSeconds +
                    '}';
        }
    }
}
