package org.clickenrent.contracts.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for registering an Expo Push Token from a mobile device.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTokenRequest {

    @NotBlank(message = "Expo push token is required")
    private String expoPushToken;

    /**
     * Device platform: "ios" or "android"
     */
    private String deviceType;

    /**
     * Human-readable device name (e.g., "iPhone 14 Pro", "Samsung Galaxy S23")
     */
    private String deviceName;
}

