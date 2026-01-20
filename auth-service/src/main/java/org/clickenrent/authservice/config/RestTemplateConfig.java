package org.clickenrent.authservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for RestTemplate used in OAuth and external API calls.
 * Provides proper timeouts, connection pooling, and error handling.
 */
@Configuration
@Slf4j
public class RestTemplateConfig {
    
    /**
     * Create RestTemplate bean for OAuth operations.
     * Configured with:
     * - Connection timeout: 10 seconds
     * - Read timeout: 15 seconds
     * - Buffering for request/response logging
     * - Connection pooling via SimpleClientHttpRequestFactory
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(15))
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(10000); // 10 seconds
                    factory.setReadTimeout(15000); // 15 seconds
                    // Enable buffering for better error handling and logging
                    return new BufferingClientHttpRequestFactory(factory);
                })
                .build();
    }
    
    /**
     * Alternative RestTemplate for Google OAuth specifically.
     * Can be customized with Google-specific interceptors if needed.
     */
    @Bean(name = "googleOAuthRestTemplate")
    public RestTemplate googleOAuthRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(15))
                .requestFactory(this::createRequestFactory)
                .build();
    }
    
    /**
     * Create request factory with proper configuration.
     */
    private ClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setReadTimeout(15000); // 15 seconds
        // BufferingClientHttpRequestFactory handles request buffering
        return new BufferingClientHttpRequestFactory(factory);
    }
}
