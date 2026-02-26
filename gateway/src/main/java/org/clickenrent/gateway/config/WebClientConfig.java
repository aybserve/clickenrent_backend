package org.clickenrent.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for WebClient used in Gateway.
 */
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
