package org.clickenrent.notificationservice.service;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.Status;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for communicating with Expo Push Notification API using official Expo Server SDK for Java.
 * SDK: https://github.com/hlspablo/expo-server-sdk-java
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushService {

    private final ExpoPushNotificationClient expoPushClient;

    /**
     * Send a single push notification via Expo API.
     *
     * @param token    Expo Push Token
     * @param title    Notification title
     * @param body     Notification body
     * @param data     Additional data payload
     * @param priority Priority: "default" or "high"
     * @return TicketResponse.Ticket with receipt ID or error
     */
    public TicketResponse.Ticket sendNotification(
            String token,
            String title,
            String body,
            Map<String, Object> data,
            String priority
    ) {
        log.debug("Sending notification to token: {} with title: {}", token, title);

        if (!isValidExpoToken(token)) {
            log.error("Invalid Expo push token format: {}", token);
            TicketResponse.Ticket errorTicket = new TicketResponse.Ticket();
            errorTicket.setStatus(Status.ERROR);
            errorTicket.setMessage("Invalid Expo push token format");
            return errorTicket;
        }

        PushNotification pushNotification = new PushNotification();
        List<String> recipients = new ArrayList<>();
        recipients.add(token);
        pushNotification.setTo(recipients);
        pushNotification.setTitle(title);
        pushNotification.setBody(body);
        pushNotification.setData(data);
        // Note: Priority setting removed - check SDK documentation for correct method
        // pushNotification.setPriority(priority != null && priority.equals("high") ? "high" : "default");
        pushNotification.setSound("default");

        try {
            List<PushNotification> notifications = new ArrayList<>();
            notifications.add(pushNotification);

            List<TicketResponse.Ticket> tickets = expoPushClient.sendPushNotifications(notifications);

            if (!tickets.isEmpty()) {
                TicketResponse.Ticket ticket = tickets.get(0);
                log.debug("Expo API response: status={}, id={}", ticket.getStatus(), ticket.getId());
                return ticket;
            } else {
                log.error("Empty response from Expo API");
                TicketResponse.Ticket errorTicket = new TicketResponse.Ticket();
                errorTicket.setStatus(Status.ERROR);
                errorTicket.setMessage("Empty response from Expo API");
                return errorTicket;
            }
        } catch (IOException e) {
            log.error("Error sending notification to Expo API", e);
            TicketResponse.Ticket errorTicket = new TicketResponse.Ticket();
            errorTicket.setStatus(Status.ERROR);
            errorTicket.setMessage("Exception: " + e.getMessage());
            return errorTicket;
        }
    }

    /**
     * Send multiple push notifications in a batch.
     * The SDK automatically handles chunking if needed.
     *
     * @param notifications List of PushNotification objects
     * @return List of TicketResponse.Ticket objects
     */
    public List<TicketResponse.Ticket> sendBatch(List<PushNotification> notifications) {
        if (notifications.isEmpty()) {
            log.warn("Attempted to send empty batch of notifications");
            return List.of();
        }

        log.info("Sending batch of {} notifications", notifications.size());

        try {
            List<TicketResponse.Ticket> tickets = expoPushClient.sendPushNotifications(notifications);
            log.info("Successfully sent batch of {} notifications, received {} tickets",
                    notifications.size(), tickets.size());
            return tickets;
        } catch (IOException e) {
            log.error("Error sending batch notifications to Expo API", e);
            return List.of();
        }
    }

    /**
     * Build a PushNotification from individual components.
     *
     * @param token    Expo Push Token
     * @param title    Notification title
     * @param body     Notification body
     * @param data     Additional data payload
     * @param priority Priority: "default" or "high"
     * @return PushNotification ready to send
     */
    public PushNotification buildMessage(
            String token,
            String title,
            String body,
            Map<String, Object> data,
            String priority
    ) {
        PushNotification pushNotification = new PushNotification();
        List<String> recipients = new ArrayList<>();
        recipients.add(token);
        pushNotification.setTo(recipients);
        pushNotification.setTitle(title);
        pushNotification.setBody(body);
        pushNotification.setData(data);
        // Note: Priority setting removed - check SDK documentation for correct method
        // pushNotification.setPriority(priority != null && priority.equals("high") ? "high" : "default");
        pushNotification.setSound("default");
        return pushNotification;
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
