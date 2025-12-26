package org.clickenrent.notificationservice.service;

import com.niamedtech.expo.exposerversdk.response.Status;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.contracts.notification.SendNotificationResponse;
import org.clickenrent.notificationservice.entity.NotificationLog;
import org.clickenrent.notificationservice.entity.NotificationPreference;
import org.clickenrent.notificationservice.entity.PushToken;
import org.clickenrent.notificationservice.repository.NotificationLogRepository;
import org.clickenrent.notificationservice.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                TicketResponse.Ticket ticket = expoPushService.sendNotification(
                        token.getExpoPushToken(),
                        request.getTitle(),
                        request.getBody(),
                        request.getData(),
                        request.getPriority()
                );

                if (ticket.getStatus() == Status.OK) {
                    log.info("Successfully sent notification to token: {}", token.getExpoPushToken());
                    receiptId = ticket.getId();
                    overallSuccess = true;
                    token.setLastUsedAt(LocalDateTime.now());
                } else {
                    log.error("Failed to send notification to token: {}, error: {}",
                            token.getExpoPushToken(), ticket.getMessage());
                    errorMessage = ticket.getMessage();

                    // Handle specific errors - check if details contain DeviceNotRegistered
                    if (ticket.getMessage() != null && ticket.getMessage().contains("DeviceNotRegistered")) {
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
     * Uses granular preference checking for rental notifications.
     *
     * @param userExternalId   User external ID
     * @param notificationType Notification type
     * @return true if notification should be sent
     */
    private boolean shouldSendNotification(String userExternalId, String notificationType) {
        NotificationPreference preference = preferenceRepository.findByUserExternalId(userExternalId)
                .orElse(createDefaultPreferences(userExternalId));

        // Map notification types to preference settings with granular rental controls
        return switch (notificationType) {
            // Rental start notifications
            case "BIKE_UNLOCKED", "RIDE_STARTED" ->
                    preference.getRentalUpdatesEnabled() && preference.getRentalStartEnabled();
            
            // Rental end reminders
            case "RENTAL_ENDING_SOON", "RENTAL_ENDING_10MIN", "RENTAL_ENDING_30MIN" ->
                    preference.getRentalUpdatesEnabled() && preference.getRentalEndRemindersEnabled();
            
            // Rental completion notifications
            case "BIKE_LOCKED", "RIDE_ENDED" ->
                    preference.getRentalUpdatesEnabled() && preference.getRentalCompletionEnabled();
            
            // Payment notifications
            case "PAYMENT_SUCCESS", "PAYMENT_FAILED", "REFUND_PROCESSED" ->
                    preference.getPaymentUpdatesEnabled();
            
            // Support notifications
            case "SUPPORT_MESSAGE", "TICKET_RESOLVED" ->
                    preference.getSupportMessagesEnabled();
            
            // Marketing notifications
            case "MARKETING", "PROMOTION" ->
                    preference.getMarketingEnabled();
            
            default -> true; // Send by default for unknown types
        };
    }

    /**
     * Mark a notification as read.
     *
     * @param notificationId Notification ID
     * @param userExternalId User external ID (for security check)
     * @return true if successfully marked as read
     */
    @Transactional
    public boolean markAsRead(String notificationId, String userExternalId) {
        try {
            Long id = Long.parseLong(notificationId);
            Optional<NotificationLog> logOptional = notificationLogRepository.findById(id);
            
            if (logOptional.isEmpty()) {
                log.warn("Notification not found: {}", notificationId);
                return false;
            }
            
            NotificationLog notificationLog = logOptional.get();
            
            // Security check: ensure notification belongs to user
            if (!notificationLog.getUserExternalId().equals(userExternalId)) {
                log.warn("User {} attempted to mark notification {} as read, but it belongs to user {}",
                        userExternalId, notificationId, notificationLog.getUserExternalId());
                return false;
            }
            
            // Mark as read
            if (!notificationLog.getIsRead()) {
                notificationLog.setIsRead(true);
                notificationLog.setReadAt(LocalDateTime.now());
                notificationLogRepository.save(notificationLog);
                log.info("Marked notification {} as read for user {}", notificationId, userExternalId);
            }
            
            return true;
        } catch (NumberFormatException e) {
            log.error("Invalid notification ID format: {}", notificationId);
            return false;
        }
    }

    /**
     * Mark all notifications as read for a user.
     *
     * @param userExternalId User external ID
     * @return Number of notifications marked as read
     */
    @Transactional
    public int markAllAsRead(String userExternalId) {
        List<NotificationLog> unreadNotifications = notificationLogRepository
                .findByUserExternalIdAndIsReadFalse(userExternalId);
        
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        
        for (NotificationLog notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(now);
            count++;
        }
        
        if (count > 0) {
            notificationLogRepository.saveAll(unreadNotifications);
            log.info("Marked {} notifications as read for user {}", count, userExternalId);
        }
        
        return count;
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
                .rentalStartEnabled(true)
                .rentalEndRemindersEnabled(true)
                .rentalCompletionEnabled(true)
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

