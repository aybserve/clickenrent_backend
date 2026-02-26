package org.clickenrent.rentalservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.notification-events}")
    private String notificationEventsTopic;

    public void publishNotificationEvent(
            String userExternalId,
            String notificationType,
            String title,
            String body,
            Map<String, Object> data,
            String priority
    ) {
        try {
            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(notificationType)
                    .timestamp(ZonedDateTime.now())
                    .userExternalId(userExternalId)
                    .notificationType(notificationType)
                    .title(title)
                    .body(body)
                    .data(data)
                    .priority(priority != null ? priority : "default")
                    .build();

            kafkaTemplate.send(notificationEventsTopic, userExternalId, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Published notification event: type={}, user={}, partition={}, offset={}",
                                    notificationType, userExternalId,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to publish notification event: type={}, user={}",
                                    notificationType, userExternalId, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing notification event: type={}, user={}",
                    notificationType, userExternalId, e);
        }
    }
}
