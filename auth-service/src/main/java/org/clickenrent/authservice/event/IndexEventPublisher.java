package org.clickenrent.authservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.search-index-events}")
    private String searchIndexEventsTopic;

    public void publishIndexEvent(
            String entityType,
            String entityId,
            IndexEventRequest.IndexOperation operation
    ) {
        try {
            IndexEventRequest event = IndexEventRequest.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .operation(operation)
                    .build();

            kafkaTemplate.send(searchIndexEventsTopic, entityId, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Published index event: operation={}, type={}, id={}, partition={}, offset={}",
                                    operation, entityType, entityId,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to publish index event: operation={}, type={}, id={}",
                                    operation, entityType, entityId, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing index event: operation={}, type={}, id={}",
                    operation, entityType, entityId, e);
        }
    }
}
