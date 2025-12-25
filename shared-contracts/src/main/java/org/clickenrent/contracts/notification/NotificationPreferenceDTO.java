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
    private Boolean rentalUpdatesEnabled;
    private Boolean paymentUpdatesEnabled;
    private Boolean supportMessagesEnabled;
    private Boolean marketingEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

