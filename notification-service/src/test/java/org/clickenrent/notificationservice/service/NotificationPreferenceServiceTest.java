package org.clickenrent.notificationservice.service;

import org.clickenrent.contracts.notification.NotificationPreferenceDTO;
import org.clickenrent.contracts.notification.UpdatePreferencesRequest;
import org.clickenrent.notificationservice.entity.NotificationPreference;
import org.clickenrent.notificationservice.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceServiceTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationPreferenceService preferenceService;

    private static final String USER_ID = "user-456";

    private NotificationPreference existingPreference;

    @BeforeEach
    void setUp() {
        existingPreference = NotificationPreference.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .rentalUpdatesEnabled(true)
                .rentalStartEnabled(true)
                .rentalEndRemindersEnabled(true)
                .rentalCompletionEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getPreferences_whenNoPreference_createsDefaultAndReturnsDto() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(inv -> {
            NotificationPreference p = inv.getArgument(0);
            p.setId(1L);
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            return p;
        });

        NotificationPreferenceDTO dto = preferenceService.getPreferences(USER_ID);

        assertThat(dto).isNotNull();
        assertThat(dto.getUserExternalId()).isEqualTo(USER_ID);
        assertThat(dto.getRentalUpdatesEnabled()).isTrue();
        assertThat(dto.getRentalStartEnabled()).isTrue();
        assertThat(dto.getMarketingEnabled()).isFalse();

        ArgumentCaptor<NotificationPreference> captor = ArgumentCaptor.forClass(NotificationPreference.class);
        verify(preferenceRepository).save(captor.capture());
        assertThat(captor.getValue().getUserExternalId()).isEqualTo(USER_ID);
    }

    @Test
    void getPreferences_whenPreferenceExists_returnsDtoWithoutSaving() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(existingPreference));

        NotificationPreferenceDTO dto = preferenceService.getPreferences(USER_ID);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserExternalId()).isEqualTo(USER_ID);
        assertThat(dto.getRentalUpdatesEnabled()).isTrue();
        assertThat(dto.getMarketingEnabled()).isFalse();
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    void updatePreferences_partialUpdate_onlyUpdatesProvidedFields() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                .marketingEnabled(true)
                .rentalEndRemindersEnabled(false)
                .build();

        NotificationPreferenceDTO dto = preferenceService.updatePreferences(USER_ID, request);

        assertThat(dto.getMarketingEnabled()).isTrue();
        assertThat(dto.getRentalEndRemindersEnabled()).isFalse();
        verify(preferenceRepository).save(existingPreference);
        assertThat(existingPreference.getMarketingEnabled()).isTrue();
        assertThat(existingPreference.getRentalEndRemindersEnabled()).isFalse();
    }

    @Test
    void updatePreferences_whenNoPreference_createsDefaultThenUpdates() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(inv -> {
            NotificationPreference p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                .paymentUpdatesEnabled(false)
                .build();

        NotificationPreferenceDTO dto = preferenceService.updatePreferences(USER_ID, request);

        assertThat(dto.getPaymentUpdatesEnabled()).isFalse();
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }

    @Test
    void shouldSendRentalStart_whenPreferenceEnabled_returnsTrue() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(existingPreference));

        boolean result = preferenceService.shouldSendRentalStart(USER_ID);

        assertThat(result).isTrue();
    }

    @Test
    void shouldSendRentalStart_whenRentalUpdatesDisabled_returnsFalse() {
        existingPreference.setRentalUpdatesEnabled(false);
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(existingPreference));

        boolean result = preferenceService.shouldSendRentalStart(USER_ID);

        assertThat(result).isFalse();
    }

    @Test
    void shouldSendRentalStart_whenNoPreference_returnsTrue() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.empty());

        boolean result = preferenceService.shouldSendRentalStart(USER_ID);

        assertThat(result).isTrue();
    }

    @Test
    void shouldSendRentalEndReminder_whenEnabled_returnsTrue() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(existingPreference));

        boolean result = preferenceService.shouldSendRentalEndReminder(USER_ID);

        assertThat(result).isTrue();
    }

    @Test
    void shouldSendRentalCompletion_whenEnabled_returnsTrue() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(existingPreference));

        boolean result = preferenceService.shouldSendRentalCompletion(USER_ID);

        assertThat(result).isTrue();
    }
}
