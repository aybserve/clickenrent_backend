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
    @NotBlank(message = "Platform is required")
    private String platform;

    /**
     * Unique device identifier (UUID)
     */
    @NotBlank(message = "Device ID is required")
    private String deviceId;

    /**
     * Mobile app version (e.g., "1.0.0")
     */
    @NotBlank(message = "App version is required")
    private String appVersion;

    /**
     * Human-readable device name (e.g., "iPhone 14 Pro", "Samsung Galaxy S23")
     */
    private String deviceName;

    /**
     * Device model (e.g., "iPhone 15 Pro", "Pixel 8")
     */
    private String deviceModel;

    /**
     * OS version (e.g., "iOS 17.2", "Android 14")
     */
    private String osVersion;
}

