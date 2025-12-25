package org.clickenrent.notificationservice.dto.expo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO representing an Expo Push Notification message.
 * This is the format expected by the Expo Push API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpoPushMessage {

    /**
     * The Expo Push Token of the recipient
     */
    private String to;

    /**
     * The notification title
     */
    private String title;

    /**
     * The notification body text
     */
    private String body;

    /**
     * Additional data to send with the notification
     */
    private Map<String, Object> data;

    /**
     * Priority: "default" or "high"
     */
    @Builder.Default
    private String priority = "default";

    /**
     * Sound to play: "default" or null for no sound
     */
    private String sound;

    /**
     * Badge count to display on app icon
     */
    private Integer badge;

    /**
     * Channel ID for Android
     */
    private String channelId;

    /**
     * Category ID for iOS
     */
    private String categoryId;

    /**
     * Time to live in seconds
     */
    private Integer ttl;

    /**
     * Expiration timestamp
     */
    private Long expiration;
}

