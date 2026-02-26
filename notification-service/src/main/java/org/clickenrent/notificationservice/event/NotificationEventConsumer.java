package org.clickenrent.notificationservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.NotificationEvent;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.notification-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotificationEvent(
            @Payload NotificationEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Consumed notification event: type={}, user={}, partition={}, offset={}",
                event.getEventType(), event.getUserExternalId(), partition, offset);

        try {
            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userExternalId(event.getUserExternalId())
                    .notificationType(event.getNotificationType())
                    .title(event.getTitle())
                    .body(event.getBody())
                    .data(event.getData())
                    .priority(event.getPriority())
                    .build();

            notificationService.send(request);
            log.info("Successfully processed notification event: eventId={}, type={}",
                    event.getEventId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to process notification event: eventId={}, type={}, user={}",
                    event.getEventId(), event.getEventType(), event.getUserExternalId(), e);
            throw e;
        }
    }
}
