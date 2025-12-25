package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO after sending a notification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationResponse {

    /**
     * Whether the notification was successfully sent
     */
    private boolean success;

    /**
     * Expo receipt ID for tracking the notification delivery
     */
    private String receiptId;

    /**
     * Error message if the notification failed
     */
    private String error;
}

