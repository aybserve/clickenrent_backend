package org.clickenrent.searchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.searchservice.dto.BulkSyncRequest;
import org.clickenrent.searchservice.dto.BulkSyncResponse;
import org.clickenrent.searchservice.dto.IndexEventRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Main indexing service that orchestrates bulk sync and event-driven indexing.
 * 
 * @author Vitaliy Shvetsov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingService {

    private final UserIndexService userIndexService;
    private final BikeIndexService bikeIndexService;
    private final LocationIndexService locationIndexService;
    private final HubIndexService hubIndexService;

    /**
     * Perform bulk synchronization of entities
     */
    public BulkSyncResponse bulkSync(BulkSyncRequest request) {
        long startTime = System.currentTimeMillis();
        Map<String, Integer> indexedCounts = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        log.info("Starting bulk sync for entity types: {}", request.getEntityTypes());
        
        for (String entityType : request.getEntityTypes()) {
            try {
                int count = switch (entityType.toLowerCase()) {
                    case "users" -> userIndexService.bulkIndexUsers(request.getCompanyId());
                    case "bikes" -> bikeIndexService.bulkIndexBikes(request.getCompanyId());
                    case "locations" -> locationIndexService.bulkIndexLocations(request.getCompanyId());
                    case "hubs" -> hubIndexService.bulkIndexHubs(request.getCompanyId());
                    default -> {
                        errors.put(entityType, "Unknown entity type");
                        yield 0;
                    }
                };
                indexedCounts.put(entityType, count);
            } catch (Exception e) {
                log.error("Failed to index entity type: {}", entityType, e);
                errors.put(entityType, e.getMessage());
                indexedCounts.put(entityType, 0);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        String status = errors.isEmpty() ? "SUCCESS" : "PARTIAL_SUCCESS";
        
        return BulkSyncResponse.builder()
                .indexedCounts(indexedCounts)
                .errors(errors)
                .status(status)
                .durationMs(duration)
                .build();
    }

    /**
     * Process a single index event (async)
     */
    @Async("indexingTaskExecutor")
    public void processIndexEvent(IndexEventRequest event) {
        log.info("Processing index event: {} {} {}", event.getOperation(), event.getEntityType(), event.getEntityId());
        
        try {
            switch (event.getOperation()) {
                case CREATE, UPDATE -> indexEntity(event.getEntityType(), event.getEntityId());
                case DELETE -> deleteEntity(event.getEntityType(), event.getEntityId());
            }
        } catch (Exception e) {
            log.error("Failed to process index event: {}", event, e);
        }
    }

    /**
     * Index a single entity
     */
    private void indexEntity(String entityType, String entityId) {
        switch (entityType.toLowerCase()) {
            case "user" -> userIndexService.indexUser(entityId);
            case "bike" -> bikeIndexService.indexBike(entityId);
            case "location" -> locationIndexService.indexLocation(entityId);
            case "hub" -> hubIndexService.indexHub(entityId);
            default -> log.warn("Unknown entity type for indexing: {}", entityType);
        }
    }

    /**
     * Delete a single entity from index
     */
    private void deleteEntity(String entityType, String entityId) {
        switch (entityType.toLowerCase()) {
            case "user" -> userIndexService.deleteUser(entityId);
            case "bike" -> bikeIndexService.deleteBike(entityId);
            case "location" -> locationIndexService.deleteLocation(entityId);
            case "hub" -> hubIndexService.deleteHub(entityId);
            default -> log.warn("Unknown entity type for deletion: {}", entityType);
        }
    }
}
