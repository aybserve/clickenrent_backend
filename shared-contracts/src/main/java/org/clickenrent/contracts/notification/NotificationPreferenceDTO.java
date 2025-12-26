package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user notification preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDTO {

    private Long id;
    private String userExternalId;
    
    // Master rental toggle
    private Boolean rentalUpdatesEnabled;
    
    // Granular rental preferences
    private Boolean rentalStartEnabled;
    private Boolean rentalEndRemindersEnabled;
    private Boolean rentalCompletionEnabled;
    
    // Other preferences
    private Boolean paymentUpdatesEnabled;
    private Boolean supportMessagesEnabled;
    private Boolean marketingEnabled;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

