package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Kafka event for notification triggers.
 * Published by: rental-service, auth-service, payment-service, support-service
 * Consumed by: notification-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventId;
    private String eventType;
    private ZonedDateTime timestamp;
    
    private String userExternalId;
    private String notificationType;
    private String title;
    private String body;
    private Map<String, Object> data;
    private String priority;
}
