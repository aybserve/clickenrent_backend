package org.clickenrent.searchservice.event;

import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.contracts.search.IndexEventRequest.IndexOperation;
import org.clickenrent.searchservice.service.IndexingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Unit tests for IndexEventConsumer.
 *
 * @author Vitaliy Shvetsov
 */
@ExtendWith(MockitoExtension.class)
class IndexEventConsumerTest {

    @Mock
    private IndexingService indexingService;

    @InjectMocks
    private IndexEventConsumer indexEventConsumer;

    @Test
    void consumeIndexEvent_callsProcessIndexEvent() {
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("user")
                .entityId("user-123")
                .operation(IndexOperation.CREATE)
                .build();

        indexEventConsumer.consumeIndexEvent(event, 0, 1L);

        verify(indexingService).processIndexEvent(event);
    }

    @Test
    void consumeIndexEvent_whenServiceThrows_propagatesException() {
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("bike")
                .entityId("bike-456")
                .operation(IndexOperation.UPDATE)
                .build();
        doThrow(new RuntimeException("Indexing failed")).when(indexingService).processIndexEvent(event);

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                indexEventConsumer.consumeIndexEvent(event, 1, 2L));

        verify(indexingService).processIndexEvent(event);
    }
}
