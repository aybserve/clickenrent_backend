package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity representing user preferences for personalized UI/UX settings.
 * Contains navigation customization, theme preferences, localization, and display configurations.
 */
@Entity
@Table(
    name = "user_preferences",
    indexes = {
        @Index(name = "idx_user_preferences_user_id", columnList = "user_id")
    }
)
@SQLDelete(sql = "UPDATE user_preferences SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserPreference extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // ===========================
    // Navigation Preferences
    // ===========================
    
    /**
     * Role-specific navigation menu ordering stored as JSON.
     * Example: {"superadmin": ["dashboard", "companies"], "b2b": ["bikes", "rentals"]}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "navigation_order", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, List<String>> navigationOrder = new HashMap<>();

    // ===========================
    // Appearance Preferences
    // ===========================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "theme", length = 20)
    @Builder.Default
    private Theme theme = Theme.SYSTEM;

    // ===========================
    // Localization Preferences
    // ===========================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Size(max = 100, message = "Timezone must not exceed 100 characters")
    @Column(name = "timezone", length = 100)
    @Builder.Default
    private String timezone = "UTC";

    @Size(max = 50, message = "Date format must not exceed 50 characters")
    @Column(name = "date_format", length = 50)
    @Builder.Default
    private String dateFormat = "YYYY-MM-DD";

    @Enumerated(EnumType.STRING)
    @Column(name = "time_format", length = 20)
    @Builder.Default
    private TimeFormat timeFormat = TimeFormat.TWENTY_FOUR_HOUR;

    /**
     * Currency code (ISO 4217) referencing payment-service Currency entity.
     * Must match Currency.code from payment-service (e.g., "USD", "EUR", "GBP", "UAH", "PLN").
     * Default: "USD"
     */
    @Size(max = 3, message = "Currency code must be exactly 3 characters")
    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "EUR";

    // ===========================
    // Notification Preferences
    // ===========================
    
    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "push_notifications")
    @Builder.Default
    private Boolean pushNotifications = true;

    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_frequency", length = 20)
    @Builder.Default
    private NotificationFrequency notificationFrequency = NotificationFrequency.IMMEDIATE;

    // ===========================
    // Display Preferences
    // ===========================
    
    @Min(value = 10, message = "Items per page must be at least 10")
    @Max(value = 100, message = "Items per page must not exceed 100")
    @Column(name = "items_per_page")
    @Builder.Default
    private Integer itemsPerPage = 20;

    /**
     * Dashboard layout configuration stored as JSON.
     * Allows flexible widget positioning and size configurations.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dashboard_layout", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> dashboardLayout = new HashMap<>();

    /**
     * Table column preferences stored as JSON.
     * Tracks visible columns, column order, and column widths per table.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "table_preferences", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> tablePreferences = new HashMap<>();

    /**
     * Default filters for various views stored as JSON.
     * Allows users to save their preferred filter settings.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "default_filters", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> defaultFilters = new HashMap<>();

    // ===========================
    // Enums
    // ===========================
    
    /**
     * Theme options for UI appearance.
     */
    public enum Theme {
        SYSTEM,  // Follow system theme
        LIGHT,   // Light mode
        DARK     // Dark mode
    }

    /**
     * Time format preferences.
     */
    public enum TimeFormat {
        TWELVE_HOUR,      // 12-hour format (AM/PM)
        TWENTY_FOUR_HOUR  // 24-hour format
    }

    /**
     * Notification frequency options.
     */
    public enum NotificationFrequency {
        IMMEDIATE,  // Send notifications immediately
        DAILY,      // Digest once per day
        WEEKLY      // Digest once per week
    }
}
