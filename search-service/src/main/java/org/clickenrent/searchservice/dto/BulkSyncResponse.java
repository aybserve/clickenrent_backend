package org.clickenrent.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for bulk synchronization operations.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkSyncResponse {

    /**
     * Total entities indexed per type
     */
    private Map<String, Integer> indexedCounts;

    /**
     * Errors encountered during indexing
     */
    private Map<String, String> errors;

    /**
     * Overall sync status
     */
    private String status;

    /**
     * Total time taken in milliseconds
     */
    private Long durationMs;
}
