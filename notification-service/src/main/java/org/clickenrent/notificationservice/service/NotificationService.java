package org.clickenrent.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.contracts.notification.SendNotificationResponse;
import org.clickenrent.notificationservice.dto.expo.ExpoPushResponse;
import org.clickenrent.notificationservice.entity.NotificationLog;
import org.clickenrent.notificationservice.entity.NotificationPreference;
import org.clickenrent.notificationservice.entity.PushToken;
import org.clickenrent.notificationservice.repository.NotificationLogRepository;
import org.clickenrent.notificationservice.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for sending notifications with business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ExpoPushService expoPushService;
    private final TokenManagementService tokenManagementService;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationLogRepository notificationLogRepository;

    /**
     * Send a notification to a user.
     * Checks user preferences, retrieves active tokens, sends via Expo, and logs the result.
     *
     * @param request Notification request
     * @return Response with success status and receipt ID
     */
    @Transactional
    public SendNotificationResponse send(SendNotificationRequest request) {
        log.info("Processing notification request for user: {}, type: {}",
                request.getUserExternalId(), request.getNotificationType());

        // 1. Check user preferences
        if (!shouldSendNotification(request.getUserExternalId(), request.getNotificationType())) {
            log.info("User {} has disabled notifications of type: {}",
                    request.getUserExternalId(), request.getNotificationType());
            return SendNotificationResponse.builder()
                    .success(false)
                    .error("User has disabled this notification type")
                    .build();
        }

        // 2. Get active tokens for user
        List<PushToken> activeTokens = tokenManagementService.getActiveTokensForUser(request.getUserExternalId());

        if (activeTokens.isEmpty()) {
            log.warn("No active push tokens found for user: {}", request.getUserExternalId());
            logNotification(request, "failed", null, "No active push tokens");
            return SendNotificationResponse.builder()
                    .success(false)
                    .error("No active push tokens for user")
                    .build();
        }

        // 3. Send notification to all active tokens
        boolean overallSuccess = false;
        String receiptId = null;
        String errorMessage = null;

        for (PushToken token : activeTokens) {
            try {
                ExpoPushResponse response = expoPushService.sendNotification(
                        token.getExpoPushToken(),
                        request.getTitle(),
                        request.getBody(),
                        request.getData(),
                        request.getPriority()
                );

                if ("ok".equals(response.getStatus())) {
                    log.info("Successfully sent notification to token: {}", token.getExpoPushToken());
                    receiptId = response.getId();
                    overallSuccess = true;
                    token.setLastUsedAt(LocalDateTime.now());
                } else {
                    log.error("Failed to send notification to token: {}, error: {}",
                            token.getExpoPushToken(), response.getMessage());
                    errorMessage = response.getMessage();

                    // Handle specific errors
                    if (response.getDetails() != null && "DeviceNotRegistered".equals(response.getDetails().getError())) {
                        log.info("Deactivating invalid token: {}", token.getExpoPushToken());
                        tokenManagementService.deactivateToken(token.getExpoPushToken());
                    }
                }
            } catch (Exception e) {
                log.error("Exception sending notification to token: {}", token.getExpoPushToken(), e);
                errorMessage = e.getMessage();
            }
        }

        // 4. Log the notification
        String status = overallSuccess ? "sent" : "failed";
        logNotification(request, status, receiptId, errorMessage);

        return SendNotificationResponse.builder()
                .success(overallSuccess)
                .receiptId(receiptId)
                .error(errorMessage)
                .build();
    }

    /**
     * Check if a notification should be sent based on user preferences.
     *
     * @param userExternalId   User external ID
     * @param notificationType Notification type
     * @return true if notification should be sent
     */
    private boolean shouldSendNotification(String userExternalId, String notificationType) {
        NotificationPreference preference = preferenceRepository.findByUserExternalId(userExternalId)
                .orElse(createDefaultPreferences(userExternalId));

        // Map notification types to preference settings
        return switch (notificationType) {
            case "BIKE_UNLOCKED", "BIKE_LOCKED", "RIDE_STARTED", "RIDE_ENDED" ->
                    preference.getRentalUpdatesEnabled();
            case "PAYMENT_SUCCESS", "PAYMENT_FAILED", "REFUND_PROCESSED" ->
                    preference.getPaymentUpdatesEnabled();
            case "SUPPORT_MESSAGE", "TICKET_RESOLVED" ->
                    preference.getSupportMessagesEnabled();
            case "MARKETING", "PROMOTION" ->
                    preference.getMarketingEnabled();
            default -> true; // Send by default for unknown types
        };
    }

    /**
     * Create default preferences for a user.
     *
     * @param userExternalId User external ID
     * @return Default notification preferences
     */
    private NotificationPreference createDefaultPreferences(String userExternalId) {
        NotificationPreference preference = NotificationPreference.builder()
                .userExternalId(userExternalId)
                .rentalUpdatesEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(false)
                .isDeleted(false)
                .build();
        return preferenceRepository.save(preference);
    }

    /**
     * Log a notification to the database.
     *
     * @param request      Original request
     * @param status       Status: "sent", "failed", "pending"
     * @param receiptId    Expo receipt ID
     * @param errorMessage Error message if failed
     */
    private void logNotification(SendNotificationRequest request, String status, String receiptId, String errorMessage) {
        NotificationLog log = NotificationLog.builder()
                .userExternalId(request.getUserExternalId())
                .notificationType(request.getNotificationType())
                .title(request.getTitle())
                .body(request.getBody())
                .data(request.getData())
                .status(status)
                .expoReceiptId(receiptId)
                .errorMessage(errorMessage)
                .isDeleted(false)
                .build();
        notificationLogRepository.save(log);
    }
}

