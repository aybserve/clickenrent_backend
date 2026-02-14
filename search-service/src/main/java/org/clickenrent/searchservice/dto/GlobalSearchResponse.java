package org.clickenrent.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Global search response containing results from all entity types.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResponse {

    /**
     * Original search query
     */
    private String query;

    /**
     * Search results grouped by entity type
     */
    private Map<String, List<SearchResult>> results;

    /**
     * Total number of results across all types
     */
    private Integer totalResults;

    /**
     * Time taken to execute search in milliseconds
     */
    private Long searchTimeMs;
}
