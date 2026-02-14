package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for push token registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTokenResponse {

    /**
     * Whether the registration was successful
     */
    private Boolean success;

    /**
     * Unique notification/token ID
     */
    private String notificationId;

    /**
     * Device ID that was registered
     */
    private String deviceId;

    /**
     * Timestamp when the token was registered
     */
    private LocalDateTime registeredAt;
}



