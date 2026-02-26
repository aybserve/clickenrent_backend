package org.clickenrent.notificationservice.service;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.Status;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpoPushServiceTest {

    @Mock
    private ExpoPushNotificationClient expoPushClient;

    @InjectMocks
    private ExpoPushService expoPushService;

    private static final String VALID_TOKEN = "ExponentPushToken[abc123]";

    @Test
    void isValidExpoToken_whenNull_returnsFalse() {
        assertThat(expoPushService.isValidExpoToken(null)).isFalse();
    }

    @Test
    void isValidExpoToken_whenEmpty_returnsFalse() {
        assertThat(expoPushService.isValidExpoToken("")).isFalse();
    }

    @Test
    void isValidExpoToken_whenValidFormat_returnsTrue() {
        assertThat(expoPushService.isValidExpoToken(VALID_TOKEN)).isTrue();
        assertThat(expoPushService.isValidExpoToken("ExponentPushToken[xyz]")).isTrue();
    }

    @Test
    void isValidExpoToken_whenInvalidFormat_returnsFalse() {
        assertThat(expoPushService.isValidExpoToken("invalid")).isFalse();
        assertThat(expoPushService.isValidExpoToken("ExponentPushToken[no-bracket")).isFalse();
        assertThat(expoPushService.isValidExpoToken("ExponentPushToken]")).isFalse();
    }

    @Test
    void sendNotification_whenTokenInvalid_returnsErrorTicket() throws Exception {
        TicketResponse.Ticket result = expoPushService.sendNotification(
                "invalid", "Title", "Body", null);

        assertThat(result.getStatus()).isEqualTo(Status.ERROR);
        assertThat(result.getMessage()).isEqualTo("Invalid Expo push token format");
        verify(expoPushClient, never()).sendPushNotifications(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendNotification_whenClientReturnsSuccess_returnsOkTicket() throws IOException {
        TicketResponse.Ticket okTicket = new TicketResponse.Ticket();
        okTicket.setStatus(Status.OK);
        okTicket.setId("receipt-id");
        when(expoPushClient.sendPushNotifications(anyList())).thenReturn(List.of(okTicket));

        TicketResponse.Ticket result = expoPushService.sendNotification(
                VALID_TOKEN, "Hi", "Message", Map.of("key", "value"));

        assertThat(result.getStatus()).isEqualTo(Status.OK);
        assertThat(result.getId()).isEqualTo("receipt-id");

        ArgumentCaptor<List<PushNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(expoPushClient).sendPushNotifications(captor.capture());
        List<PushNotification> sent = captor.getValue();
        assertThat(sent).hasSize(1);
        assertThat(sent.get(0).getTo()).containsExactly(VALID_TOKEN);
        assertThat(sent.get(0).getTitle()).isEqualTo("Hi");
        assertThat(sent.get(0).getBody()).isEqualTo("Message");
        assertThat(sent.get(0).getData()).isEqualTo(Map.of("key", "value"));
    }

    @Test
    void sendNotification_whenClientThrowsIOException_returnsErrorTicket() throws IOException {
        when(expoPushClient.sendPushNotifications(anyList())).thenThrow(new IOException("Network error"));

        TicketResponse.Ticket result = expoPushService.sendNotification(
                VALID_TOKEN, "Title", "Body", null);

        assertThat(result.getStatus()).isEqualTo(Status.ERROR);
        assertThat(result.getMessage()).contains("Exception:");
        assertThat(result.getMessage()).contains("Network error");
    }

    @Test
    void sendNotification_whenClientReturnsEmptyList_returnsErrorTicket() throws IOException {
        when(expoPushClient.sendPushNotifications(anyList())).thenReturn(Collections.emptyList());

        TicketResponse.Ticket result = expoPushService.sendNotification(
                VALID_TOKEN, "Title", "Body", null);

        assertThat(result.getStatus()).isEqualTo(Status.ERROR);
        assertThat(result.getMessage()).isEqualTo("Empty response from Expo API");
    }

    @Test
    void buildMessage_createsPushNotificationWithCorrectFields() {
        Map<String, Object> data = Map.of("id", 1);

        PushNotification msg = expoPushService.buildMessage(VALID_TOKEN, "T", "B", data);

        assertThat(msg.getTo()).containsExactly(VALID_TOKEN);
        assertThat(msg.getTitle()).isEqualTo("T");
        assertThat(msg.getBody()).isEqualTo("B");
        assertThat(msg.getData()).isEqualTo(data);
        assertThat(msg.getSound()).isEqualTo("default");
    }

    @Test
    void sendBatch_whenEmpty_returnsEmptyList() throws IOException {
        List<TicketResponse.Ticket> result = expoPushService.sendBatch(List.of());

        assertThat(result).isEmpty();
        verify(expoPushClient, never()).sendPushNotifications(anyList());
    }

    @Test
    void sendBatch_whenClientReturnsTickets_returnsTickets() throws IOException {
        PushNotification msg = expoPushService.buildMessage(VALID_TOKEN, "T", "B", null);
        TicketResponse.Ticket ticket = new TicketResponse.Ticket();
        ticket.setStatus(Status.OK);
        ticket.setId("batch-1");
        when(expoPushClient.sendPushNotifications(anyList())).thenReturn(List.of(ticket));

        List<TicketResponse.Ticket> result = expoPushService.sendBatch(List.of(msg));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.OK);
        assertThat(result.get(0).getId()).isEqualTo("batch-1");
        verify(expoPushClient).sendPushNotifications(anyList());
    }

    @Test
    void sendBatch_whenClientThrowsIOException_returnsEmptyList() throws IOException {
        PushNotification msg = expoPushService.buildMessage(VALID_TOKEN, "T", "B", null);
        when(expoPushClient.sendPushNotifications(anyList())).thenThrow(new IOException("Network error"));

        List<TicketResponse.Ticket> result = expoPushService.sendBatch(List.of(msg));

        assertThat(result).isEmpty();
    }
}
