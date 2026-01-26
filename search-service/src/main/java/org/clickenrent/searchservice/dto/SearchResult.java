package org.clickenrent.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Search result DTO representing a single search hit.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    /**
     * Internal ID
     */
    private Long id;

    /**
     * External ID (UUID)
     */
    private String externalId;

    /**
     * Type of entity (user, bike, location, hub)
     */
    private String type;

    /**
     * Main display text
     */
    private String title;

    /**
     * Secondary display text
     */
    private String subtitle;

    /**
     * Relative URL path
     */
    private String url;

    /**
     * Optional image URL
     */
    private String imageUrl;

    /**
     * Type-specific metadata
     */
    private Map<String, Object> metadata;
}
