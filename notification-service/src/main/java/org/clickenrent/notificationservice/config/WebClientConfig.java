package org.clickenrent.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for WebClient used to call Expo Push API.
 */
@Configuration
public class WebClientConfig {

    @Value("${expo.api.url}")
    private String expoApiUrl;

    @Bean
    public WebClient expoWebClient() {
        return WebClient.builder()
                .baseUrl(expoApiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}


