package org.clickenrent.notificationservice.service;

import org.clickenrent.contracts.notification.RegisterTokenRequest;
import org.clickenrent.contracts.notification.RegisterTokenResponse;
import org.clickenrent.notificationservice.entity.PushToken;
import org.clickenrent.notificationservice.repository.PushTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenManagementServiceTest {

    @Mock
    private PushTokenRepository pushTokenRepository;

    @Mock
    private ExpoPushService expoPushService;

    @InjectMocks
    private TokenManagementService tokenManagementService;

    private static final String USER_ID = "user-789";
    private static final String VALID_TOKEN = "ExponentPushToken[xyz123]";

    private RegisterTokenRequest request;

    @BeforeEach
    void setUp() {
        request = RegisterTokenRequest.builder()
                .expoPushToken(VALID_TOKEN)
                .platform("ios")
                .deviceId("device-uuid-1")
                .appVersion("1.0.0")
                .deviceName("iPhone 14")
                .deviceModel("iPhone14,2")
                .osVersion("iOS 17.0")
                .build();
    }

    @Test
    void registerToken_whenTokenInvalid_throwsIllegalArgumentException() {
        when(expoPushService.isValidExpoToken(VALID_TOKEN)).thenReturn(false);

        assertThatThrownBy(() -> tokenManagementService.registerToken(USER_ID, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Expo Push Token format");
        verify(pushTokenRepository, never()).save(any());
    }

    @Test
    void registerToken_whenNewDevice_createsNewToken() {
        when(expoPushService.isValidExpoToken(VALID_TOKEN)).thenReturn(true);
        when(pushTokenRepository.findByUserExternalIdAndDeviceId(USER_ID, request.getDeviceId())).thenReturn(Optional.empty());
        when(pushTokenRepository.findByExpoPushToken(VALID_TOKEN)).thenReturn(Optional.empty());
        when(pushTokenRepository.save(any(PushToken.class))).thenAnswer(inv -> {
            PushToken t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        RegisterTokenResponse response = tokenManagementService.registerToken(USER_ID, request);

        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getDeviceId()).isEqualTo(request.getDeviceId());

        ArgumentCaptor<PushToken> captor = ArgumentCaptor.forClass(PushToken.class);
        verify(pushTokenRepository).save(captor.capture());
        PushToken saved = captor.getValue();
        assertThat(saved.getUserExternalId()).isEqualTo(USER_ID);
        assertThat(saved.getExpoPushToken()).isEqualTo(VALID_TOKEN);
        assertThat(saved.getDeviceId()).isEqualTo(request.getDeviceId());
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    void registerToken_whenSameDeviceId_updatesExistingToken() {
        PushToken existing = PushToken.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .expoPushToken("ExponentPushToken[old]")
                .deviceId(request.getDeviceId())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(expoPushService.isValidExpoToken(VALID_TOKEN)).thenReturn(true);
        when(pushTokenRepository.findByUserExternalIdAndDeviceId(USER_ID, request.getDeviceId())).thenReturn(Optional.of(existing));
        when(pushTokenRepository.save(any(PushToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterTokenResponse response = tokenManagementService.registerToken(USER_ID, request);

        assertThat(response.getSuccess()).isTrue();
        assertThat(existing.getExpoPushToken()).isEqualTo(VALID_TOKEN);
        assertThat(existing.getPlatform()).isEqualTo("ios");
        verify(pushTokenRepository).save(existing);
    }

    @Test
    void registerToken_whenSameExpoTokenDifferentUser_updatesTokenWithNewUser() {
        PushToken existing = PushToken.builder()
                .id(1L)
                .userExternalId("old-user")
                .expoPushToken(VALID_TOKEN)
                .deviceId("old-device")
                .isActive(true)
                .build();

        when(expoPushService.isValidExpoToken(VALID_TOKEN)).thenReturn(true);
        when(pushTokenRepository.findByUserExternalIdAndDeviceId(USER_ID, request.getDeviceId())).thenReturn(Optional.empty());
        when(pushTokenRepository.findByExpoPushToken(VALID_TOKEN)).thenReturn(Optional.of(existing));
        when(pushTokenRepository.save(any(PushToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterTokenResponse response = tokenManagementService.registerToken(USER_ID, request);

        assertThat(response.getSuccess()).isTrue();
        assertThat(existing.getUserExternalId()).isEqualTo(USER_ID);
        assertThat(existing.getDeviceId()).isEqualTo(request.getDeviceId());
        verify(pushTokenRepository).save(existing);
    }

    @Test
    void getActiveTokensForUser_returnsFromRepository() {
        PushToken token = PushToken.builder().id(1L).userExternalId(USER_ID).expoPushToken(VALID_TOKEN).isActive(true).build();
        when(pushTokenRepository.findByUserExternalIdAndIsActiveTrue(USER_ID)).thenReturn(List.of(token));

        List<PushToken> result = tokenManagementService.getActiveTokensForUser(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExpoPushToken()).isEqualTo(VALID_TOKEN);
    }

    @Test
    void deactivateToken_whenTokenExists_setsInactive() {
        PushToken token = PushToken.builder().id(1L).expoPushToken(VALID_TOKEN).isActive(true).build();
        when(pushTokenRepository.findByExpoPushToken(VALID_TOKEN)).thenReturn(Optional.of(token));
        when(pushTokenRepository.save(any(PushToken.class))).thenAnswer(inv -> inv.getArgument(0));

        tokenManagementService.deactivateToken(VALID_TOKEN);

        assertThat(token.getIsActive()).isFalse();
        verify(pushTokenRepository).save(token);
    }

    @Test
    void deactivateToken_whenTokenNotExists_doesNothing() {
        when(pushTokenRepository.findByExpoPushToken(VALID_TOKEN)).thenReturn(Optional.empty());

        tokenManagementService.deactivateToken(VALID_TOKEN);

        verify(pushTokenRepository, never()).save(any());
    }

    @Test
    void deleteToken_whenTokenBelongsToUser_deletesToken() {
        PushToken token = PushToken.builder().id(1L).userExternalId(USER_ID).expoPushToken(VALID_TOKEN).build();
        when(pushTokenRepository.findByExpoPushToken(VALID_TOKEN)).thenReturn(Optional.of(token));

        tokenManagementService.deleteToken(USER_ID, VALID_TOKEN);

        verify(pushTokenRepository).delete(token);
    }

    @Test
    void deleteToken_whenTokenBelongsToOtherUser_throwsIllegalArgumentException() {
        PushToken token = PushToken.builder().id(1L).userExternalId("other-user").expoPushToken(VALID_TOKEN).build();
        when(pushTokenRepository.findByExpoPushToken(VALID_TOKEN)).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> tokenManagementService.deleteToken(USER_ID, VALID_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token does not belong to this user");
        verify(pushTokenRepository, never()).delete(any());
    }
}
