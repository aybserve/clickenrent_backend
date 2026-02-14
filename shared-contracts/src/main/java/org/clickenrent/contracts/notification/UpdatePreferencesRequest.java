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
     * Enable/disable all rental-related notifications (master toggle)
     */
    private Boolean rentalUpdatesEnabled;

    /**
     * Enable/disable rental start notifications (bike unlock, ride start)
     */
    private Boolean rentalStartEnabled;

    /**
     * Enable/disable rental end reminders (rental ending soon)
     */
    private Boolean rentalEndRemindersEnabled;

    /**
     * Enable/disable rental completion notifications (bike lock, ride end)
     */
    private Boolean rentalCompletionEnabled;

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

