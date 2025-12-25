package org.clickenrent.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.NotificationPreferenceDTO;
import org.clickenrent.contracts.notification.UpdatePreferencesRequest;
import org.clickenrent.notificationservice.entity.NotificationPreference;
import org.clickenrent.notificationservice.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing notification preferences.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    /**
     * Get notification preferences for a user.
     * Creates default preferences if none exist.
     *
     * @param userExternalId User external ID
     * @return Notification preferences
     */
    @Transactional
    public NotificationPreferenceDTO getPreferences(String userExternalId) {
        NotificationPreference preference = preferenceRepository.findByUserExternalId(userExternalId)
                .orElseGet(() -> createDefaultPreferences(userExternalId));

        return toDTO(preference);
    }

    /**
     * Update notification preferences for a user.
     *
     * @param userExternalId  User external ID
     * @param request Update request
     * @return Updated preferences
     */
    @Transactional
    public NotificationPreferenceDTO updatePreferences(String userExternalId, UpdatePreferencesRequest request) {
        log.info("Updating notification preferences for user: {}", userExternalId);

        NotificationPreference preference = preferenceRepository.findByUserExternalId(userExternalId)
                .orElseGet(() -> createDefaultPreferences(userExternalId));

        // Update only non-null fields
        if (request.getRentalUpdatesEnabled() != null) {
            preference.setRentalUpdatesEnabled(request.getRentalUpdatesEnabled());
        }
        if (request.getPaymentUpdatesEnabled() != null) {
            preference.setPaymentUpdatesEnabled(request.getPaymentUpdatesEnabled());
        }
        if (request.getSupportMessagesEnabled() != null) {
            preference.setSupportMessagesEnabled(request.getSupportMessagesEnabled());
        }
        if (request.getMarketingEnabled() != null) {
            preference.setMarketingEnabled(request.getMarketingEnabled());
        }

        preference = preferenceRepository.save(preference);
        log.info("Updated notification preferences for user: {}", userExternalId);

        return toDTO(preference);
    }

    /**
     * Create default preferences for a user.
     *
     * @param userExternalId User external ID
     * @return Default preferences
     */
    private NotificationPreference createDefaultPreferences(String userExternalId) {
        log.info("Creating default notification preferences for user: {}", userExternalId);
        NotificationPreference preference = NotificationPreference.builder()
                .userExternalId(userExternalId)
                .rentalUpdatesEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(false)
                .isDeleted(false)
                .build();
        return preferenceRepository.save(preference);
    }

    /**
     * Convert entity to DTO.
     */
    private NotificationPreferenceDTO toDTO(NotificationPreference preference) {
        return NotificationPreferenceDTO.builder()
                .id(preference.getId())
                .userExternalId(preference.getUserExternalId())
                .rentalUpdatesEnabled(preference.getRentalUpdatesEnabled())
                .paymentUpdatesEnabled(preference.getPaymentUpdatesEnabled())
                .supportMessagesEnabled(preference.getSupportMessagesEnabled())
                .marketingEnabled(preference.getMarketingEnabled())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}

