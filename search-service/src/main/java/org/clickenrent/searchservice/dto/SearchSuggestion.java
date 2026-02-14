package org.clickenrent.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search suggestion DTO for autocomplete.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestion {

    /**
     * Suggestion text
     */
    private String text;

    /**
     * Type of entity
     */
    private String type;

    /**
     * Category for grouping
     */
    private String category;

    /**
     * URL to navigate to
     */
    private String url;
}
