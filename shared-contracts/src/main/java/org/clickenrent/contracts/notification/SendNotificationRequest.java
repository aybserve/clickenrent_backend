package org.clickenrent.contracts.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for sending a notification to a user.
 * Used by other microservices to trigger push notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @NotBlank(message = "User external ID is required")
    private String userExternalId;

    @NotBlank(message = "Notification type is required")
    private String notificationType;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    /**
     * Additional data payload to send with the notification.
     * This data will be available in the mobile app when the notification is received.
     */
    private Map<String, Object> data;

    /**
     * Priority of the notification: "default" or "high"
     * High priority notifications are delivered immediately even if device is in low-power mode.
     */
    @Builder.Default
    private String priority = "default";
}

