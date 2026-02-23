package org.clickenrent.notificationservice.service;

import com.niamedtech.expo.exposerversdk.response.Status;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.contracts.notification.SendNotificationResponse;
import org.clickenrent.notificationservice.entity.NotificationLog;
import org.clickenrent.notificationservice.entity.NotificationPreference;
import org.clickenrent.notificationservice.entity.PushToken;
import org.clickenrent.notificationservice.repository.NotificationLogRepository;
import org.clickenrent.notificationservice.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private ExpoPushService expoPushService;

    @Mock
    private TokenManagementService tokenManagementService;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private NotificationService notificationService;

    private static final String USER_ID = "user-123";
    private static final String TOKEN_STR = "ExponentPushToken[abc]";

    private SendNotificationRequest request;
    private PushToken pushToken;
    private NotificationPreference preferenceEnabled;
    private NotificationPreference preferenceRentalDisabled;

    @BeforeEach
    void setUp() {
        request = SendNotificationRequest.builder()
                .userExternalId(USER_ID)
                .notificationType("BIKE_UNLOCKED")
                .title("Bike unlocked")
                .body("Your bike is ready.")
                .data(Map.of("rideId", "r1"))
                .build();

        pushToken = PushToken.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .expoPushToken(TOKEN_STR)
                .isActive(true)
                .build();

        preferenceEnabled = NotificationPreference.builder()
                .userExternalId(USER_ID)
                .rentalUpdatesEnabled(true)
                .rentalStartEnabled(true)
                .rentalEndRemindersEnabled(true)
                .rentalCompletionEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(false)
                .build();

        preferenceRentalDisabled = NotificationPreference.builder()
                .userExternalId(USER_ID)
                .rentalUpdatesEnabled(false)
                .rentalStartEnabled(true)
                .rentalEndRemindersEnabled(true)
                .rentalCompletionEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(false)
                .build();
    }

    @Test
    void send_whenUserHasDisabledNotificationType_returnsSuccessFalse() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(preferenceRentalDisabled));
        // getActiveTokensForUser is not called when preference disables this notification type

        SendNotificationResponse response = notificationService.send(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("User has disabled this notification type");
        verify(expoPushService, never()).sendNotification(any(), any(), any(), any());
        verify(notificationLogRepository, never()).save(any(NotificationLog.class));
    }

    @Test
    void send_whenNoActiveTokens_returnsSuccessFalseAndLogsFailure() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(preferenceEnabled));
        when(tokenManagementService.getActiveTokensForUser(USER_ID)).thenReturn(List.of());

        SendNotificationResponse response = notificationService.send(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("No active push tokens for user");
        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo("failed");
        assertThat(logCaptor.getValue().getErrorMessage()).isEqualTo("No active push tokens");
    }

    @Test
    void send_whenTokenSendsSuccessfully_returnsSuccessTrueAndLogsSent() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(preferenceEnabled));
        when(tokenManagementService.getActiveTokensForUser(USER_ID)).thenReturn(List.of(pushToken));

        TicketResponse.Ticket okTicket = new TicketResponse.Ticket();
        okTicket.setStatus(Status.OK);
        okTicket.setId("receipt-1");
        when(expoPushService.sendNotification(eq(TOKEN_STR), eq(request.getTitle()), eq(request.getBody()), eq(request.getData())))
                .thenReturn(okTicket);

        SendNotificationResponse response = notificationService.send(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getReceiptId()).isEqualTo("receipt-1");
        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo("sent");
        assertThat(logCaptor.getValue().getExpoReceiptId()).isEqualTo("receipt-1");
    }

    @Test
    void send_whenExpoReturnsDeviceNotRegistered_deactivatesTokenAndLogsFailed() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.of(preferenceEnabled));
        when(tokenManagementService.getActiveTokensForUser(USER_ID)).thenReturn(List.of(pushToken));

        TicketResponse.Ticket errorTicket = new TicketResponse.Ticket();
        errorTicket.setStatus(Status.ERROR);
        errorTicket.setMessage("DeviceNotRegistered: ...");
        when(expoPushService.sendNotification(any(), any(), any(), any())).thenReturn(errorTicket);

        SendNotificationResponse response = notificationService.send(request);

        assertThat(response.isSuccess()).isFalse();
        verify(tokenManagementService).deactivateToken(TOKEN_STR);
        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo("failed");
    }

    @Test
    void send_whenNoPreference_createsDefaultAndSends() {
        when(preferenceRepository.findByUserExternalId(USER_ID)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(inv -> {
            NotificationPreference p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(tokenManagementService.getActiveTokensForUser(USER_ID)).thenReturn(List.of(pushToken));

        TicketResponse.Ticket okTicket = new TicketResponse.Ticket();
        okTicket.setStatus(Status.OK);
        okTicket.setId("r2");
        when(expoPushService.sendNotification(any(), any(), any(), any())).thenReturn(okTicket);

        SendNotificationResponse response = notificationService.send(request);

        assertThat(response.isSuccess()).isTrue();
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }

    @Test
    void markAsRead_whenNotificationNotFound_returnsFalse() {
        when(notificationLogRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = notificationService.markAsRead("999", USER_ID);

        assertThat(result).isFalse();
        verify(notificationLogRepository, never()).save(any());
    }

    @Test
    void markAsRead_whenNotificationBelongsToAnotherUser_returnsFalse() {
        NotificationLog log = NotificationLog.builder()
                .id(1L)
                .userExternalId("other-user")
                .isRead(false)
                .build();
        when(notificationLogRepository.findById(1L)).thenReturn(Optional.of(log));

        boolean result = notificationService.markAsRead("1", USER_ID);

        assertThat(result).isFalse();
        verify(notificationLogRepository, never()).save(any());
    }

    @Test
    void markAsRead_whenOwnNotification_marksReadAndReturnsTrue() {
        NotificationLog log = NotificationLog.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .isRead(false)
                .build();
        when(notificationLogRepository.findById(1L)).thenReturn(Optional.of(log));

        boolean result = notificationService.markAsRead("1", USER_ID);

        assertThat(result).isTrue();
        verify(notificationLogRepository).save(log);
        assertThat(log.getIsRead()).isTrue();
        assertThat(log.getReadAt()).isNotNull();
    }

    @Test
    void markAsRead_whenInvalidIdFormat_returnsFalse() {
        boolean result = notificationService.markAsRead("not-a-number", USER_ID);

        assertThat(result).isFalse();
    }

    @Test
    void markAllAsRead_updatesUnreadNotificationsAndReturnsCount() {
        NotificationLog log1 = NotificationLog.builder().id(1L).userExternalId(USER_ID).isRead(false).build();
        NotificationLog log2 = NotificationLog.builder().id(2L).userExternalId(USER_ID).isRead(false).build();
        when(notificationLogRepository.findByUserExternalIdAndIsReadFalse(USER_ID)).thenReturn(List.of(log1, log2));

        int count = notificationService.markAllAsRead(USER_ID);

        assertThat(count).isEqualTo(2);
        verify(notificationLogRepository).saveAll(List.of(log1, log2));
        assertThat(log1.getIsRead()).isTrue();
        assertThat(log2.getIsRead()).isTrue();
    }

    @Test
    void markAllAsRead_whenNoUnread_returnsZero() {
        when(notificationLogRepository.findByUserExternalIdAndIsReadFalse(USER_ID)).thenReturn(List.of());

        int count = notificationService.markAllAsRead(USER_ID);

        assertThat(count).isEqualTo(0);
        verify(notificationLogRepository, never()).saveAll(any());
    }
}
