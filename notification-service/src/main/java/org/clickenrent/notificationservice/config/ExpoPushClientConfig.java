package org.clickenrent.notificationservice.config;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Expo Push Notification Client.
 * Creates a singleton ExpoPushNotificationClient bean for sending push notifications via Expo.
 * Uses the official Java SDK from https://github.com/hlspablo/expo-server-sdk-java
 */
@Configuration
@Slf4j
public class ExpoPushClientConfig {

    /**
     * Create HTTP client for Expo API communication.
     *
     * @return configured CloseableHttpClient instance
     */
    @Bean
    public CloseableHttpClient httpClient() {
        log.info("Creating HTTP client for Expo API...");
        return HttpClients.createDefault();
    }

    /**
     * Create and configure the Expo Push Notification Client bean.
     * The client handles communication with Expo's push notification service.
     *
     * @param httpClient HTTP client for API communication
     * @return configured ExpoPushNotificationClient instance
     */
    @Bean
    public ExpoPushNotificationClient expoPushClient(CloseableHttpClient httpClient) {
        log.info("Initializing Expo Push Notification Client...");
        
        ExpoPushNotificationClient client = ExpoPushNotificationClient
                .builder()
                .setHttpClient(httpClient)
                // Optional: Set access token if using Expo's push notification service with authentication
                // .setAccessToken("YOUR_ACCESS_TOKEN")
                .build();
        
        log.info("Expo Push Notification Client initialized successfully");
        return client;
    }
}

