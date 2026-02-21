package org.clickenrent.searchservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.searchservice.service.IndexingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexEventConsumer {

    private final IndexingService indexingService;

    @KafkaListener(
            topics = "${kafka.topics.search-index-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeIndexEvent(
            @Payload IndexEventRequest event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Consumed index event: operation={}, type={}, id={}, partition={}, offset={}",
                event.getOperation(), event.getEntityType(), event.getEntityId(), partition, offset);

        try {
            indexingService.processIndexEvent(event);
            log.info("Successfully processed index event: operation={}, type={}, id={}",
                    event.getOperation(), event.getEntityType(), event.getEntityId());
        } catch (Exception e) {
            log.error("Failed to process index event: operation={}, type={}, id={}",
                    event.getOperation(), event.getEntityType(), event.getEntityId(), e);
            throw e;
        }
    }
}
