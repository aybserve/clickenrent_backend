package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for UserPreference entity.
 * Contains all user preference settings including navigation, theme, localization, and notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User preferences for personalized UI/UX settings")
public class UserPreferenceDTO {

    @Schema(description = "Unique identifier for preferences", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "123")
    private Long userId;

    @Schema(description = "User external ID for cross-service communication", example = "usr-ext-00001")
    private String userExternalId;

    // ===========================
    // Navigation Preferences
    // ===========================
    
    @Schema(
        description = "Role-specific navigation menu ordering",
        example = "{\"superadmin\": [\"dashboard\", \"companies\", \"locations\"], \"b2b\": [\"bikes\", \"rentals\"]}"
    )
    private Map<String, List<String>> navigationOrder;

    // ===========================
    // Appearance Preferences
    // ===========================
    
    @Schema(description = "UI theme preference", example = "dark", allowableValues = {"system", "light", "dark"})
    private String theme;

    // ===========================
    // Localization Preferences
    // ===========================
    
    @Schema(description = "Preferred language ID", example = "1")
    private Long languageId;
    
    @Schema(description = "Preferred language name", example = "English")
    private String languageName;

    @Schema(description = "Preferred timezone", example = "America/New_York")
    private String timezone;

    @Schema(description = "Preferred date format", example = "MM/DD/YYYY")
    private String dateFormat;

    @Schema(description = "Preferred time format", example = "12h", allowableValues = {"12h", "24h"})
    private String timeFormat;

    @Schema(
        description = "Preferred currency code (ISO 4217) - matches payment-service Currency.code",
        example = "USD",
        allowableValues = {"USD", "EUR", "GBP", "UAH", "PLN"}
    )
    private String currency;

    // ===========================
    // Notification Preferences
    // ===========================
    
    @Schema(description = "Enable email notifications", example = "true")
    private Boolean emailNotifications;

    @Schema(description = "Enable push notifications", example = "true")
    private Boolean pushNotifications;

    @Schema(description = "Enable SMS notifications", example = "false")
    private Boolean smsNotifications;

    @Schema(
        description = "Notification frequency preference",
        example = "immediate",
        allowableValues = {"immediate", "daily", "weekly"}
    )
    private String notificationFrequency;

    // ===========================
    // Display Preferences
    // ===========================
    
    @Schema(description = "Number of items per page in lists", example = "25", minimum = "10", maximum = "100")
    private Integer itemsPerPage;

    @Schema(description = "Dashboard layout configuration (flexible JSON structure)", example = "{}")
    private Map<String, Object> dashboardLayout;

    @Schema(description = "Table column preferences (visible columns, order, widths)", example = "{}")
    private Map<String, Object> tablePreferences;

    @Schema(description = "Default filters for various views", example = "{}")
    private Map<String, Object> defaultFilters;

    // ===========================
    // Audit Fields
    // ===========================
    
    @Schema(description = "Timestamp when preferences were created", example = "2024-01-15T10:30:00")
    private LocalDateTime dateCreated;

    @Schema(description = "Timestamp when preferences were last modified", example = "2024-02-01T14:20:00")
    private LocalDateTime lastDateModified;

    @Schema(description = "User who created the preferences", example = "system")
    private String createdBy;

    @Schema(description = "User who last modified the preferences", example = "user@example.com")
    private String lastModifiedBy;
}
