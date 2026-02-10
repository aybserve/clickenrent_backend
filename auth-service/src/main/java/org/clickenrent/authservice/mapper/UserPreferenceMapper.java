package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.UpdateUserPreferenceRequest;
import org.clickenrent.authservice.dto.UserPreferenceDTO;
import org.clickenrent.authservice.entity.UserPreference;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserPreference entity and DTOs.
 * Handles enum conversions and provides update logic for partial updates.
 */
@Component
public class UserPreferenceMapper {
    
    /**
     * Convert UserPreference entity to DTO.
     *
     * @param entity the UserPreference entity
     * @return UserPreferenceDTO or null if entity is null
     */
    public UserPreferenceDTO toDto(UserPreference entity) {
        if (entity == null) {
            return null;
        }
        
        return UserPreferenceDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userExternalId(entity.getUser() != null ? entity.getUser().getExternalId() : null)
                .navigationOrder(entity.getNavigationOrder())
                .theme(entity.getTheme() != null ? entity.getTheme().name().toLowerCase() : null)
                .languageId(entity.getLanguage() != null ? entity.getLanguage().getId() : null)
                .languageName(entity.getLanguage() != null ? entity.getLanguage().getName() : null)
                .timezone(entity.getTimezone())
                .dateFormat(entity.getDateFormat())
                .timeFormat(mapTimeFormatToString(entity.getTimeFormat()))
                .currencyExternalId(entity.getCurrencyExternalId())
                .emailNotifications(entity.getEmailNotifications())
                .pushNotifications(entity.getPushNotifications())
                .smsNotifications(entity.getSmsNotifications())
                .notificationFrequency(entity.getNotificationFrequency() != null 
                        ? entity.getNotificationFrequency().name().toLowerCase() : null)
                .itemsPerPage(entity.getItemsPerPage())
                .dashboardLayout(entity.getDashboardLayout())
                .tablePreferences(entity.getTablePreferences())
                .defaultFilters(entity.getDefaultFilters())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }
    
    /**
     * Update entity from UpdateUserPreferenceRequest.
     * Only updates fields that are not null in the request.
     *
     * @param request the update request
     * @param entity the entity to update
     */
    public void updateEntityFromRequest(UpdateUserPreferenceRequest request, UserPreference entity) {
        if (request == null || entity == null) {
            return;
        }
        
        if (request.getNavigationOrder() != null) {
            entity.setNavigationOrder(request.getNavigationOrder());
        }
        if (request.getTheme() != null) {
            entity.setTheme(mapStringToTheme(request.getTheme()));
        }
        // Note: languageId is handled in the service layer for proper validation
        if (request.getTimezone() != null) {
            entity.setTimezone(request.getTimezone());
        }
        if (request.getDateFormat() != null) {
            entity.setDateFormat(request.getDateFormat());
        }
        if (request.getTimeFormat() != null) {
            entity.setTimeFormat(mapStringToTimeFormat(request.getTimeFormat()));
        }
        if (request.getCurrencyExternalId() != null) {
            entity.setCurrencyExternalId(request.getCurrencyExternalId());
        }
        if (request.getEmailNotifications() != null) {
            entity.setEmailNotifications(request.getEmailNotifications());
        }
        if (request.getPushNotifications() != null) {
            entity.setPushNotifications(request.getPushNotifications());
        }
        if (request.getSmsNotifications() != null) {
            entity.setSmsNotifications(request.getSmsNotifications());
        }
        if (request.getNotificationFrequency() != null) {
            entity.setNotificationFrequency(mapStringToNotificationFrequency(request.getNotificationFrequency()));
        }
        if (request.getItemsPerPage() != null) {
            entity.setItemsPerPage(request.getItemsPerPage());
        }
        if (request.getDashboardLayout() != null) {
            entity.setDashboardLayout(request.getDashboardLayout());
        }
        if (request.getTablePreferences() != null) {
            entity.setTablePreferences(request.getTablePreferences());
        }
        if (request.getDefaultFilters() != null) {
            entity.setDefaultFilters(request.getDefaultFilters());
        }
    }
    
    // ===========================
    // Enum Mapping Helpers
    // ===========================
    
    /**
     * Map Theme enum to string representation.
     */
    private UserPreference.Theme mapStringToTheme(String theme) {
        if (theme == null) {
            return UserPreference.Theme.SYSTEM;
        }
        return switch (theme.toLowerCase()) {
            case "light" -> UserPreference.Theme.LIGHT;
            case "dark" -> UserPreference.Theme.DARK;
            default -> UserPreference.Theme.SYSTEM;
        };
    }
    
    /**
     * Map TimeFormat enum to string representation.
     */
    private String mapTimeFormatToString(UserPreference.TimeFormat timeFormat) {
        if (timeFormat == null) {
            return "24h";
        }
        return switch (timeFormat) {
            case TWELVE_HOUR -> "12h";
            case TWENTY_FOUR_HOUR -> "24h";
        };
    }
    
    /**
     * Map string to TimeFormat enum.
     */
    private UserPreference.TimeFormat mapStringToTimeFormat(String timeFormat) {
        if (timeFormat == null) {
            return UserPreference.TimeFormat.TWENTY_FOUR_HOUR;
        }
        return switch (timeFormat.toLowerCase()) {
            case "12h" -> UserPreference.TimeFormat.TWELVE_HOUR;
            default -> UserPreference.TimeFormat.TWENTY_FOUR_HOUR;
        };
    }
    
    /**
     * Map string to NotificationFrequency enum.
     */
    private UserPreference.NotificationFrequency mapStringToNotificationFrequency(String frequency) {
        if (frequency == null) {
            return UserPreference.NotificationFrequency.IMMEDIATE;
        }
        return switch (frequency.toLowerCase()) {
            case "daily" -> UserPreference.NotificationFrequency.DAILY;
            case "weekly" -> UserPreference.NotificationFrequency.WEEKLY;
            default -> UserPreference.NotificationFrequency.IMMEDIATE;
        };
    }
}
