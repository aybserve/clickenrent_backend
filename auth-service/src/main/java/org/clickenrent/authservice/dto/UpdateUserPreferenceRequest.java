package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for updating user preferences.
 * All fields are optional - only provided fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating user preferences (all fields optional)")
public class UpdateUserPreferenceRequest {

    // ===========================
    // Navigation Preferences
    // ===========================
    
    @Schema(
        description = "Role-specific navigation menu ordering (optional)",
        example = "{\"b2b\": [\"analytics\", \"bikes\", \"bikeRentals\"]}"
    )
    private Map<String, List<String>> navigationOrder;

    // ===========================
    // Appearance Preferences
    // ===========================
    
    @Pattern(
        regexp = "^(system|light|dark)$",
        message = "Theme must be 'system', 'light', or 'dark'"
    )
    @Schema(
        description = "UI theme preference (optional)",
        example = "dark",
        allowableValues = {"system", "light", "dark"}
    )
    private String theme;

    // ===========================
    // Localization Preferences
    // ===========================
    
    @Schema(description = "Preferred language ID (optional)", example = "4")
    private Long languageId;

    @Size(max = 100, message = "Timezone must not exceed 100 characters")
    @Schema(description = "Preferred timezone (optional)", example = "Europe/Madrid")
    private String timezone;

    @Size(max = 50, message = "Date format must not exceed 50 characters")
    @Schema(description = "Preferred date format (optional)", example = "DD/MM/YYYY")
    private String dateFormat;

    @Pattern(
        regexp = "^(12h|24h)$",
        message = "Time format must be '12h' or '24h'"
    )
    @Schema(
        description = "Preferred time format (optional)",
        example = "12h",
        allowableValues = {"12h", "24h"}
    )
    private String timeFormat;

    @Size(max = 100, message = "Currency external ID must not exceed 100 characters")
    @Schema(
        description = "Preferred currency external ID - must reference payment-service Currency.externalId (optional)",
        example = "550e8400-e29b-41d4-a716-446655440022"
    )
    private String currencyExternalId;

    // ===========================
    // Notification Preferences
    // ===========================
    
    @Schema(description = "Enable email notifications (optional)", example = "true")
    private Boolean emailNotifications;

    @Schema(description = "Enable push notifications (optional)", example = "false")
    private Boolean pushNotifications;

    @Schema(description = "Enable SMS notifications (optional)", example = "false")
    private Boolean smsNotifications;

    @Pattern(
        regexp = "^(immediate|daily|weekly)$",
        message = "Notification frequency must be 'immediate', 'daily', or 'weekly'"
    )
    @Schema(
        description = "Notification frequency preference (optional)",
        example = "daily",
        allowableValues = {"immediate", "daily", "weekly"}
    )
    private String notificationFrequency;

    // ===========================
    // Display Preferences
    // ===========================
    
    @Min(value = 10, message = "Items per page must be at least 10")
    @Max(value = 100, message = "Items per page must not exceed 100")
    @Schema(
        description = "Number of items per page in lists (optional)",
        example = "50",
        minimum = "10",
        maximum = "100"
    )
    private Integer itemsPerPage;

    @Schema(description = "Dashboard layout configuration (optional)", example = "{}")
    private Map<String, Object> dashboardLayout;

    @Schema(description = "Table column preferences (optional)", example = "{}")
    private Map<String, Object> tablePreferences;

    @Schema(description = "Default filters for various views (optional)", example = "{}")
    private Map<String, Object> defaultFilters;
}
