package org.clickenrent.searchservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.searchservice.dto.BulkSyncRequest;
import org.clickenrent.searchservice.dto.BulkSyncResponse;
import org.clickenrent.searchservice.dto.IndexEventRequest;
import org.clickenrent.searchservice.service.IndexingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for indexing operations.
 * Provides bulk sync and event-driven indexing endpoints.
 * 
 * @author Vitaliy Shvetsov
 */
@RestController
@RequestMapping("/api/v1/index")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Indexing", description = "Indexing management endpoints")
public class IndexingController {

    private final IndexingService indexingService;

    /**
     * Bulk synchronization of entities from source services
     * Admin only endpoint for initial indexing or re-indexing
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Bulk synchronization",
            description = "Performs bulk indexing of entities from source services. Admin only."
    )
    public ResponseEntity<BulkSyncResponse> bulkSync(@Valid @RequestBody BulkSyncRequest request) {
        log.info("Bulk sync request: {}", request);
        
        BulkSyncResponse response = indexingService.bulkSync(request);
        
        log.info("Bulk sync completed: {} entities indexed", response.getIndexedCounts());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Process single entity index event
     * Internal endpoint for other services to notify of entity changes
     */
    @PostMapping("/event")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Index event",
            description = "Processes a single entity index event (CREATE/UPDATE/DELETE). " +
                    "Internal endpoint for other services."
    )
    public ResponseEntity<Void> processIndexEvent(@Valid @RequestBody IndexEventRequest event) {
        log.info("Index event received: {}", event);
        
        // Process asynchronously
        indexingService.processIndexEvent(event);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
