package org.clickenrent.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.notificationservice.dto.expo.ExpoPushMessage;
import org.clickenrent.notificationservice.dto.expo.ExpoPushResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service for communicating with Expo Push Notification API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushService {

    private final WebClient expoWebClient;

    private static final int TIMEOUT_SECONDS = 10;
    private static final int MAX_BATCH_SIZE = 100;

    /**
     * Send a single push notification via Expo API.
     *
     * @param token    Expo Push Token
     * @param title    Notification title
     * @param body     Notification body
     * @param data     Additional data payload
     * @param priority Priority: "default" or "high"
     * @return ExpoPushResponse with receipt ID or error
     */
    public ExpoPushResponse sendNotification(
            String token,
            String title,
            String body,
            Map<String, Object> data,
            String priority
    ) {
        log.debug("Sending notification to token: {} with title: {}", token, title);

        ExpoPushMessage message = ExpoPushMessage.builder()
                .to(token)
                .title(title)
                .body(body)
                .data(data)
                .priority(priority)
                .sound("default")
                .build();

        try {
            // Expo API expects an array of messages
            List<ExpoPushMessage> messages = List.of(message);

            List<ExpoPushResponse> responses = expoWebClient.post()
                    .bodyValue(messages)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<ExpoPushResponse>>() {})
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .block();

            if (responses != null && !responses.isEmpty()) {
                ExpoPushResponse response = responses.get(0);
                log.debug("Expo API response: {}", response);
                return response;
            } else {
                log.error("Empty response from Expo API");
                return ExpoPushResponse.builder()
                        .status("error")
                        .message("Empty response from Expo API")
                        .build();
            }
        } catch (Exception e) {
            log.error("Error sending notification to Expo API", e);
            return ExpoPushResponse.builder()
                    .status("error")
                    .message("Exception: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Send multiple push notifications in a batch.
     * Expo supports up to 100 messages per request.
     *
     * @param messages List of ExpoPushMessage objects
     * @return List of ExpoPushResponse objects
     */
    public List<ExpoPushResponse> sendBatch(List<ExpoPushMessage> messages) {
        if (messages.isEmpty()) {
            log.warn("Attempted to send empty batch of notifications");
            return List.of();
        }

        if (messages.size() > MAX_BATCH_SIZE) {
            log.warn("Batch size {} exceeds maximum {}, splitting into multiple requests",
                    messages.size(), MAX_BATCH_SIZE);
            // In production, you might want to split this into multiple batches
        }

        log.info("Sending batch of {} notifications", messages.size());

        try {
            List<ExpoPushResponse> responses = expoWebClient.post()
                    .bodyValue(messages)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<ExpoPushResponse>>() {})
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .block();

            log.info("Successfully sent batch of {} notifications", messages.size());
            return responses != null ? responses : List.of();
        } catch (Exception e) {
            log.error("Error sending batch notifications to Expo API", e);
            return List.of();
        }
    }

    /**
     * Validate if a token looks like a valid Expo Push Token.
     *
     * @param token Token to validate
     * @return true if token format is valid
     */
    public boolean isValidExpoToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        // Expo tokens start with "ExponentPushToken[" and end with "]"
        return token.startsWith("ExponentPushToken[") && token.endsWith("]");
    }
}

