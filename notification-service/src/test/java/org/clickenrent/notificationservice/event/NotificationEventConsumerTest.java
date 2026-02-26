package org.clickenrent.notificationservice.event;

import org.clickenrent.contracts.notification.NotificationEvent;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventConsumer consumer;

    @Test
    void consumeNotificationEvent_buildsSendRequestAndCallsService() {
        NotificationEvent event = NotificationEvent.builder()
                .eventId("evt-1")
                .eventType("BIKE_UNLOCKED")
                .userExternalId("user-123")
                .notificationType("BIKE_UNLOCKED")
                .title("Bike unlocked")
                .body("Your bike is ready.")
                .data(Map.of("rideId", "r1"))
                .priority("high")
                .build();

        consumer.consumeNotificationEvent(event, 0, 1L);

        ArgumentCaptor<SendNotificationRequest> captor = ArgumentCaptor.forClass(SendNotificationRequest.class);
        verify(notificationService).send(captor.capture());
        SendNotificationRequest request = captor.getValue();
        assertThat(request.getUserExternalId()).isEqualTo("user-123");
        assertThat(request.getNotificationType()).isEqualTo("BIKE_UNLOCKED");
        assertThat(request.getTitle()).isEqualTo("Bike unlocked");
        assertThat(request.getBody()).isEqualTo("Your bike is ready.");
        assertThat(request.getData()).isEqualTo(Map.of("rideId", "r1"));
        assertThat(request.getPriority()).isEqualTo("high");
    }
}
