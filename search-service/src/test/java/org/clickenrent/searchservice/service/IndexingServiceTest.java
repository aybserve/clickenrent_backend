package org.clickenrent.searchservice.service;

import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.contracts.search.IndexEventRequest.IndexOperation;
import org.clickenrent.searchservice.dto.BulkSyncRequest;
import org.clickenrent.searchservice.dto.BulkSyncResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IndexingService.
 *
 * @author Vitaliy Shvetsov
 */
@ExtendWith(MockitoExtension.class)
class IndexingServiceTest {

    @Mock
    private UserIndexService userIndexService;

    @Mock
    private BikeIndexService bikeIndexService;

    @Mock
    private LocationIndexService locationIndexService;

    @Mock
    private HubIndexService hubIndexService;

    @InjectMocks
    private IndexingService indexingService;

    @BeforeEach
    void setUp() {
        // no-op
    }

    @Test
    void bulkSync_withUsersAndBikes_callsCorrectServices() {
        // Given
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of("users", "bikes"))
                .companyExternalId("company-1")
                .build();
        when(userIndexService.bulkIndexUsers("company-1")).thenReturn(10);
        when(bikeIndexService.bulkIndexBikes("company-1")).thenReturn(5);

        // When
        BulkSyncResponse response = indexingService.bulkSync(request);

        // Then
        verify(userIndexService).bulkIndexUsers("company-1");
        verify(bikeIndexService).bulkIndexBikes("company-1");
        verifyNoInteractions(locationIndexService, hubIndexService);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(Map.of("users", 10, "bikes", 5), response.getIndexedCounts());
        assertTrue(response.getErrors().isEmpty());
        assertNotNull(response.getDurationMs());
    }

    @Test
    void bulkSync_withUnknownType_addsErrorAndPartialSuccess() {
        // Given
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of("users", "unknown"))
                .companyExternalId(null)
                .build();
        when(userIndexService.bulkIndexUsers(null)).thenReturn(3);

        // When
        BulkSyncResponse response = indexingService.bulkSync(request);

        // Then
        verify(userIndexService).bulkIndexUsers(null);
        assertEquals("PARTIAL_SUCCESS", response.getStatus());
        assertEquals(Map.of("users", 3, "unknown", 0), response.getIndexedCounts());
        assertEquals("Unknown entity type", response.getErrors().get("unknown"));
    }

    @Test
    void bulkSync_whenServiceThrows_recordsErrorAndContinues() {
        // Given
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of("users", "bikes"))
                .build();
        when(userIndexService.bulkIndexUsers(null)).thenThrow(new RuntimeException("ES unavailable"));
        when(bikeIndexService.bulkIndexBikes(null)).thenReturn(2);

        // When
        BulkSyncResponse response = indexingService.bulkSync(request);

        // Then
        assertEquals("PARTIAL_SUCCESS", response.getStatus());
        assertEquals(0, response.getIndexedCounts().get("users"));
        assertEquals(2, response.getIndexedCounts().get("bikes"));
        assertEquals("ES unavailable", response.getErrors().get("users"));
    }

    @Test
    void processIndexEvent_createUser_callsUserIndexService() {
        // Given
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("user")
                .entityId("user-123")
                .operation(IndexOperation.CREATE)
                .build();

        // When
        indexingService.processIndexEvent(event);

        // Then
        verify(userIndexService).indexUser("user-123");
        verifyNoInteractions(bikeIndexService, locationIndexService, hubIndexService);
    }

    @Test
    void processIndexEvent_updateBike_callsBikeIndexService() {
        // Given
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("bike")
                .entityId("bike-456")
                .operation(IndexOperation.UPDATE)
                .build();

        // When
        indexingService.processIndexEvent(event);

        // Then
        verify(bikeIndexService).indexBike("bike-456");
        verifyNoInteractions(userIndexService, locationIndexService, hubIndexService);
    }

    @Test
    void processIndexEvent_deleteLocation_callsLocationIndexService() {
        // Given
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("location")
                .entityId("loc-789")
                .operation(IndexOperation.DELETE)
                .build();

        // When
        indexingService.processIndexEvent(event);

        // Then
        verify(locationIndexService).deleteLocation("loc-789");
        verifyNoInteractions(userIndexService, bikeIndexService, hubIndexService);
    }

    @Test
    void processIndexEvent_deleteHub_callsHubIndexService() {
        // Given
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("hub")
                .entityId("hub-111")
                .operation(IndexOperation.DELETE)
                .build();

        // When
        indexingService.processIndexEvent(event);

        // Then
        verify(hubIndexService).deleteHub("hub-111");
    }

}
