package org.clickenrent.notificationservice.config;

import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Expo Push Notification Client.
 */
@Configuration
@Slf4j
public class ExpoPushClientConfig {

    /**
     * Create and configure the Expo PushClient bean.
     * The PushClient handles communication with Expo's push notification service.
     *
     * @return configured PushClient instance
     */
    @Bean
    public PushClient expoPushClient() {
        try {
            PushClient client = new PushClient();
            log.info("Expo PushClient initialized successfully");
            return client;
        } catch (PushClientException e) {
            log.error("Failed to initialize Expo PushClient", e);
            throw new RuntimeException("Failed to initialize Expo PushClient", e);
        }
    }
}

