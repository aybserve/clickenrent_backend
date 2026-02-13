package org.clickenrent.searchservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.searchservice.dto.GlobalSearchResponse;
import org.clickenrent.searchservice.dto.SearchSuggestion;
import org.clickenrent.searchservice.service.GlobalSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST controller for global search operations.
 * Provides unified search across users, bikes, locations, and hubs.
 * 
 * @author Vitaliy Shvetsov
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Search", description = "Global search endpoints for CMS")
@PreAuthorize("isAuthenticated()")
public class SearchController {

    private final GlobalSearchService searchService;

    /**
     * Global search across all entity types
     */
    @GetMapping
    @Operation(
            summary = "Global search",
            description = "Searches across all entity types with tenant-scoped results"
    )
    public ResponseEntity<GlobalSearchResponse> search(
            @Parameter(description = "Search query", required = true)
            @RequestParam @Size(min = 2, max = 100) String q,
            
            @Parameter(description = "Comma-separated entity types: users,bikes,locations,hubs")
            @RequestParam(required = false) String types,
            
            @Parameter(description = "Company external ID filter (optional, defaults to user's companies)")
            @RequestParam(required = false) String companyExternalId,
            
            @Parameter(description = "Maximum results per type")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer limit
    ) {
        log.info("Search request: q={}, types={}, companyExternalId={}, limit={}", q, types, companyExternalId, limit);
        
        // Parse types
        Set<String> searchTypes = types != null && !types.isBlank()
                ? Set.of(types.split(","))
                : Set.of("users", "bikes", "locations", "hubs");
        
        GlobalSearchResponse response = searchService.search(q, searchTypes, companyExternalId, limit);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get search suggestions for autocomplete
     */
    @GetMapping("/suggestions")
    @Operation(
            summary = "Search suggestions",
            description = "Returns autocomplete suggestions for search input"
    )
    public ResponseEntity<List<SearchSuggestion>> getSuggestions(
            @Parameter(description = "Search prefix", required = true)
            @RequestParam @Size(min = 1, max = 50) String q,
            
            @Parameter(description = "Company external ID filter (optional)")
            @RequestParam(required = false) String companyExternalId,
            
            @Parameter(description = "Maximum suggestions to return")
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer limit
    ) {
        log.info("Suggestions request: q={}, companyExternalId={}, limit={}", q, companyExternalId, limit);
        
        List<SearchSuggestion> suggestions = searchService.getSuggestions(q, companyExternalId, limit);
        
        return ResponseEntity.ok(suggestions);
    }
}
