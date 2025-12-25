package org.clickenrent.notificationservice.dto.expo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the response from Expo Push API after sending a notification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpoPushResponse {

    /**
     * Status of the push notification: "ok" or "error"
     */
    private String status;

    /**
     * Receipt ID for tracking the notification delivery
     */
    private String id;

    /**
     * Error message if status is "error"
     */
    private String message;

    /**
     * Detailed error information
     */
    private ExpoPushErrorDetails details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpoPushErrorDetails {
        /**
         * Error code from Expo
         * Common codes:
         * - DeviceNotRegistered: The token is invalid or expired
         * - MessageTooBig: The notification payload is too large
         * - MessageRateExceeded: Too many notifications sent
         * - InvalidCredentials: Invalid Expo access token
         */
        private String error;

        /**
         * The Expo Push Token that caused the error
         */
        @JsonProperty("expoPushToken")
        private String expoPushToken;
    }
}

