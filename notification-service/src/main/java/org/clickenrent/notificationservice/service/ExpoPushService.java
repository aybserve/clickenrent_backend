package org.clickenrent.notificationservice.service;

import io.github.jav.exposerversdk.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for communicating with Expo Push Notification API using official Expo Server SDK.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushService {

    private final PushClient pushClient;

    private static final int MAX_BATCH_SIZE = 100;

    /**
     * Send a single push notification via Expo API.
     *
     * @param token    Expo Push Token
     * @param title    Notification title
     * @param body     Notification body
     * @param data     Additional data payload
     * @param priority Priority: "default" or "high"
     * @return ExpoPushTicket with receipt ID or error
     */
    public ExpoPushTicket sendNotification(
            String token,
            String title,
            String body,
            Map<String, Object> data,
            String priority
    ) {
        log.debug("Sending notification to token: {} with title: {}", token, title);

        if (!PushClient.isExponentPushToken(token)) {
            log.error("Invalid Expo push token format: {}", token);
            ExpoPushTicket errorTicket = new ExpoPushTicket();
            errorTicket.setStatus(Status.ERROR);
            errorTicket.setMessage("Invalid Expo push token format");
            return errorTicket;
        }

        ExpoPushMessage message = new ExpoPushMessage();
        message.setTo(token);
        message.setTitle(title);
        message.setBody(body);
        message.setData(data);
        message.setPriority(priority != null && priority.equals("high") ? Priority.HIGH : Priority.DEFAULT);
        message.setSound("default");

        try {
            List<ExpoPushMessage> messages = List.of(message);
            List<List<ExpoPushMessage>> chunks = pushClient.chunkPushNotifications(messages);

            List<CompletableFuture<List<ExpoPushTicket>>> messageRepliesFutures = new ArrayList<>();

            for (List<ExpoPushMessage> chunk : chunks) {
                messageRepliesFutures.add(pushClient.sendPushNotificationsAsync(chunk));
            }

            // Wait for all responses
            List<ExpoPushTicket> allTickets = new ArrayList<>();
            for (CompletableFuture<List<ExpoPushTicket>> future : messageRepliesFutures) {
                allTickets.addAll(future.get());
            }

            if (!allTickets.isEmpty()) {
                ExpoPushTicket ticket = allTickets.get(0);
                log.debug("Expo API response: status={}, id={}", ticket.getStatus(), ticket.getId());
                return ticket;
            } else {
                log.error("Empty response from Expo API");
                ExpoPushTicket errorTicket = new ExpoPushTicket();
                errorTicket.setStatus(Status.ERROR);
                errorTicket.setMessage("Empty response from Expo API");
                return errorTicket;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error sending notification to Expo API", e);
            ExpoPushTicket errorTicket = new ExpoPushTicket();
            errorTicket.setStatus(Status.ERROR);
            errorTicket.setMessage("Exception: " + e.getMessage());
            return errorTicket;
        }
    }

    /**
     * Send multiple push notifications in a batch.
     * Expo supports up to 100 messages per request.
     * The SDK automatically chunks messages into appropriate batch sizes.
     *
     * @param messages List of ExpoPushMessage objects
     * @return List of ExpoPushTicket objects
     */
    public List<ExpoPushTicket> sendBatch(List<ExpoPushMessage> messages) {
        if (messages.isEmpty()) {
            log.warn("Attempted to send empty batch of notifications");
            return List.of();
        }

        log.info("Sending batch of {} notifications", messages.size());

        try {
            // Chunk messages into batches of 100 (handled by SDK)
            List<List<ExpoPushMessage>> chunks = pushClient.chunkPushNotifications(messages);
            log.debug("Split {} messages into {} chunks", messages.size(), chunks.size());

            List<CompletableFuture<List<ExpoPushTicket>>> messageRepliesFutures = new ArrayList<>();

            for (List<ExpoPushMessage> chunk : chunks) {
                messageRepliesFutures.add(pushClient.sendPushNotificationsAsync(chunk));
            }

            // Wait for all responses
            List<ExpoPushTicket> allTickets = new ArrayList<>();
            for (CompletableFuture<List<ExpoPushTicket>> future : messageRepliesFutures) {
                allTickets.addAll(future.get());
            }

            log.info("Successfully sent batch of {} notifications, received {} tickets",
                    messages.size(), allTickets.size());
            return allTickets;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error sending batch notifications to Expo API", e);
            return List.of();
        }
    }

    /**
     * Build an ExpoPushMessage from individual components.
     *
     * @param token    Expo Push Token
     * @param title    Notification title
     * @param body     Notification body
     * @param data     Additional data payload
     * @param priority Priority: "default" or "high"
     * @return ExpoPushMessage ready to send
     */
    public ExpoPushMessage buildMessage(
            String token,
            String title,
            String body,
            Map<String, Object> data,
            String priority
    ) {
        ExpoPushMessage message = new ExpoPushMessage();
        message.setTo(token);
        message.setTitle(title);
        message.setBody(body);
        message.setData(data);
        message.setPriority(priority != null && priority.equals("high") ? Priority.HIGH : Priority.DEFAULT);
        message.setSound("default");
        return message;
    }

    /**
     * Check delivery receipts for sent notifications.
     *
     * @param receiptIds List of receipt IDs from ExpoPushTicket
     * @return List of ExpoPushReceipt objects with delivery status
     */
    public List<ExpoPushReceipt> getReceipts(List<String> receiptIds) {
        if (receiptIds.isEmpty()) {
            return List.of();
        }

        try {
            log.debug("Checking receipts for {} notifications", receiptIds.size());
            List<CompletableFuture<List<ExpoPushReceipt>>> receiptFutures = new ArrayList<>();

            // Chunk receipt IDs (SDK handles this)
            List<List<String>> chunks = pushClient.chunkPushNotificationReceiptIds(receiptIds);

            for (List<String> chunk : chunks) {
                receiptFutures.add(pushClient.getPushNotificationReceiptsAsync(chunk));
            }

            // Wait for all receipts
            List<ExpoPushReceipt> allReceipts = new ArrayList<>();
            for (CompletableFuture<List<ExpoPushReceipt>> future : receiptFutures) {
                allReceipts.addAll(future.get());
            }

            log.debug("Retrieved {} receipts", allReceipts.size());
            return allReceipts;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error retrieving receipts from Expo API", e);
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
        return PushClient.isExponentPushToken(token);
    }
}
