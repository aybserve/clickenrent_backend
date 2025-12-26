package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for notification data returned to mobile app.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    /**
     * Notification ID
     */
    private String id;

    /**
     * Notification type (e.g., BIKE_UNLOCKED, RENTAL_ENDING)
     */
    private String type;

    /**
     * Notification title
     */
    private String title;

    /**
     * Notification body/message
     */
    private String body;

    /**
     * Additional data payload
     */
    private Map<String, Object> data;

    /**
     * When the notification was sent
     */
    private LocalDateTime sentAt;

    /**
     * When the notification was read (null if unread)
     */
    private LocalDateTime readAt;

    /**
     * Whether the notification has been read
     */
    private Boolean isRead;

    /**
     * Delivery status (sent, delivered, failed)
     */
    private String deliveryStatus;
}

