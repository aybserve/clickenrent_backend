package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user notification preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferencesRequest {

    /**
     * Enable/disable rental-related notifications (bike unlock, lock, ride start/end)
     */
    private Boolean rentalUpdatesEnabled;

    /**
     * Enable/disable payment-related notifications
     */
    private Boolean paymentUpdatesEnabled;

    /**
     * Enable/disable support message notifications
     */
    private Boolean supportMessagesEnabled;

    /**
     * Enable/disable marketing and promotional notifications
     */
    private Boolean marketingEnabled;
}

