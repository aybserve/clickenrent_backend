package org.clickenrent.searchservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.searchservice.document.BikeDocument;
import org.clickenrent.searchservice.document.HubDocument;
import org.clickenrent.searchservice.document.LocationDocument;
import org.clickenrent.searchservice.document.UserDocument;
import org.clickenrent.searchservice.dto.GlobalSearchResponse;
import org.clickenrent.searchservice.dto.SearchResult;
import org.clickenrent.searchservice.dto.SearchSuggestion;
import org.clickenrent.searchservice.security.SecurityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Global search service that searches across all entity types with tenant filtering.
 * 
 * @author Vitaliy Shvetsov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final SecurityService securityService;

    /**
     * Perform global search across specified entity types
     */
    public GlobalSearchResponse search(String query, Set<String> types, String companyId, Integer limit) {
        long startTime = System.currentTimeMillis();
        
        // Get tenant context
        List<String> allowedCompanies = resolveAllowedCompanies(companyId);
        
        Map<String, List<SearchResult>> results = new HashMap<>();
        int totalResults = 0;
        
        // Use limit directly per type (not divided)
        int limitPerType = limit;
        
        // Search users
        if (types.contains("users")) {
            List<SearchResult> userResults = searchUsers(query, allowedCompanies, limitPerType);
            results.put("users", userResults);
            totalResults += userResults.size();
        }
        
        // Search bikes
        if (types.contains("bikes")) {
            List<SearchResult> bikeResults = searchBikes(query, allowedCompanies, limitPerType);
            results.put("bikes", bikeResults);
            totalResults += bikeResults.size();
        }
        
        // Search locations
        if (types.contains("locations")) {
            List<SearchResult> locationResults = searchLocations(query, allowedCompanies, limitPerType);
            results.put("locations", locationResults);
            totalResults += locationResults.size();
        }
        
        // Search hubs
        if (types.contains("hubs")) {
            List<SearchResult> hubResults = searchHubs(query, allowedCompanies, limitPerType);
            results.put("hubs", hubResults);
            totalResults += hubResults.size();
        }
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        return GlobalSearchResponse.builder()
                .query(query)
                .results(results)
                .totalResults(totalResults)
                .searchTimeMs(searchTime)
                .build();
    }

    /**
     * Get search suggestions for autocomplete
     */
    public List<SearchSuggestion> getSuggestions(String query, String companyId, Integer limit) {
        List<String> allowedCompanies = resolveAllowedCompanies(companyId);
        List<SearchSuggestion> suggestions = new ArrayList<>();
        
        // Get suggestions from each type (3 per type)
        int limitPerType = Math.max(1, limit / 4);
        
        suggestions.addAll(getUserSuggestions(query, allowedCompanies, limitPerType));
        suggestions.addAll(getBikeSuggestions(query, allowedCompanies, limitPerType));
        suggestions.addAll(getLocationSuggestions(query, allowedCompanies, limitPerType));
        suggestions.addAll(getHubSuggestions(query, allowedCompanies, limitPerType));
        
        return suggestions.stream().limit(limit).collect(Collectors.toList());
    }

    // ==================== Private Helper Methods ====================

    private List<String> resolveAllowedCompanies(String companyId) {
        if (securityService.isAdmin()) {
            return List.of(); // Admin can see all
        }
        
        if (companyId != null) {
            return List.of(companyId);
        }
        
        return securityService.getCurrentUserCompanyExternalIds();
    }

    private List<SearchResult> searchUsers(String query, List<String> allowedCompanies, int limit) {
        try {
            NativeQuery searchQuery = buildSearchQuery(query, allowedCompanies, limit, "companyExternalIds");
            SearchHits<UserDocument> hits = elasticsearchOperations.search(searchQuery, UserDocument.class);
            
            return hits.stream()
                    .map(hit -> mapUserToSearchResult(hit.getContent()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search users", e);
            return List.of();
        }
    }

    private List<SearchResult> searchBikes(String query, List<String> allowedCompanies, int limit) {
        try {
            NativeQuery searchQuery = buildSearchQuery(query, allowedCompanies, limit, "companyExternalId");
            SearchHits<BikeDocument> hits = elasticsearchOperations.search(searchQuery, BikeDocument.class);
            
            return hits.stream()
                    .map(hit -> mapBikeToSearchResult(hit.getContent()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search bikes", e);
            return List.of();
        }
    }

    private List<SearchResult> searchLocations(String query, List<String> allowedCompanies, int limit) {
        try {
            NativeQuery searchQuery = buildSearchQuery(query, allowedCompanies, limit, "companyExternalId");
            SearchHits<LocationDocument> hits = elasticsearchOperations.search(searchQuery, LocationDocument.class);
            
            return hits.stream()
                    .map(hit -> mapLocationToSearchResult(hit.getContent()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search locations", e);
            return List.of();
        }
    }

    private List<SearchResult> searchHubs(String query, List<String> allowedCompanies, int limit) {
        try {
            NativeQuery searchQuery = buildSearchQuery(query, allowedCompanies, limit, "companyExternalId");
            SearchHits<HubDocument> hits = elasticsearchOperations.search(searchQuery, HubDocument.class);
            
            return hits.stream()
                    .map(hit -> mapHubToSearchResult(hit.getContent()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search hubs", e);
            return List.of();
        }
    }

    private NativeQuery buildSearchQuery(String query, List<String> allowedCompanies, int limit, String companyField) {
        Query textQuery = buildMultiFieldQuery(query);
        
        if (securityService.isAdmin() || allowedCompanies.isEmpty()) {
            // Admin or no tenant filtering
            return NativeQuery.builder()
                    .withQuery(textQuery)
                    .withPageable(PageRequest.of(0, limit))
                    .build();
        } else {
            // Apply tenant filter
            Query tenantQuery = Query.of(q -> q
                    .terms(t -> t
                            .field(companyField)
                            .terms(terms -> terms.value(allowedCompanies.stream()
                                    .map(c -> co.elastic.clients.elasticsearch._types.FieldValue.of(c))
                                    .collect(Collectors.toList())))));
            
            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .must(textQuery)
                    .filter(tenantQuery));
            
            return NativeQuery.builder()
                    .withQuery(q -> q.bool(boolQuery))
                    .withPageable(PageRequest.of(0, limit))
                    .build();
        }
    }

    /**
     * Build multi-field query with prefix, wildcard, and fuzzy matching
     * Prioritizes: 1) Prefix matches, 2) Wildcard matches, 3) Fuzzy matches
     */
    private Query buildMultiFieldQuery(String queryText) {
        String lowerQuery = queryText.toLowerCase().trim();
        
        // Build a bool query with multiple should clauses
        return Query.of(q -> q.bool(b -> b
                .should(
                        // Prefix match - highest priority (e.g., "joh" matches "john")
                        Query.of(sq -> sq.multiMatch(m -> m
                                .query(lowerQuery)
                                .fields("userName^5", "firstName^4", "lastName^4", "email^2", "name^4", "code^3", "frameNumber^2", "address^2")
                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.PhrasePrefix)
                                .boost(3.0f))),
                        
                        // Exact match with multi-fields - high priority
                        Query.of(sq -> sq.multiMatch(m -> m
                                .query(queryText)
                                .fields("userName^5", "firstName^4", "lastName^4", "email^2", "name^4", "code^3", "frameNumber^2", "address^2", "searchableText^1")
                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                                .boost(2.0f))),
                        
                        // Wildcard match - medium priority (e.g., "mitt" matches "schmidt")
                        Query.of(sq -> sq.queryString(qs -> qs
                                .query("*" + lowerQuery + "*")
                                .fields("userName^3", "firstName^2", "lastName^2", "email^1", "name^2", "code^2", "frameNumber^1", "address^1")
                                .boost(1.5f)
                                .analyzeWildcard(true))),
                        
                        // Fuzzy match - fallback for typos
                        Query.of(sq -> sq.multiMatch(m -> m
                                .query(queryText)
                                .fields("userName^3", "firstName^2", "lastName^2", "email^1", "name^2", "code^2", "searchableText^1")
                                .fuzziness("AUTO")
                                .prefixLength(1)
                                .boost(1.0f)))
                )
                .minimumShouldMatch("1")));
    }

    // Mapping methods
    private SearchResult mapUserToSearchResult(UserDocument doc) {
        return SearchResult.builder()
                .externalId(doc.getExternalId())
                .type("user")
                .title(doc.getFirstName() + " " + doc.getLastName())
                .subtitle(doc.getEmail())
                .url("/users/" + doc.getExternalId())
                .imageUrl(doc.getImageUrl())
                .metadata(Map.of(
                        "userName", doc.getUserName() != null ? doc.getUserName() : "",
                        "isActive", doc.getIsActive() != null ? doc.getIsActive() : false
                ))
                .build();
    }

    private SearchResult mapBikeToSearchResult(BikeDocument doc) {
        return SearchResult.builder()
                .externalId(doc.getExternalId())
                .type("bike")
                .title(doc.getCode() != null ? doc.getCode() : "Bike " + doc.getExternalId())
                .subtitle("Frame: " + (doc.getFrameNumber() != null ? doc.getFrameNumber() : "N/A"))
                .url("/bikes/" + doc.getExternalId())
                .metadata(Map.of(
                        "batteryLevel", doc.getBatteryLevel() != null ? doc.getBatteryLevel() : 0
                ))
                .build();
    }

    private SearchResult mapLocationToSearchResult(LocationDocument doc) {
        return SearchResult.builder()
                .externalId(doc.getExternalId())
                .type("location")
                .title(doc.getName())
                .subtitle(doc.getAddress())
                .url("/locations/" + doc.getExternalId())
                .metadata(Map.of(
                        "isPublic", doc.getIsPublic() != null ? doc.getIsPublic() : false
                ))
                .build();
    }

    private SearchResult mapHubToSearchResult(HubDocument doc) {
        return SearchResult.builder()
                .externalId(doc.getExternalId())
                .type("hub")
                .title(doc.getName())
                .subtitle("Capacity: " + (doc.getCapacity() != null ? doc.getCapacity() : "N/A"))
                .url("/hubs/" + doc.getExternalId())
                .metadata(Map.of(
                        "isActive", doc.getIsActive() != null ? doc.getIsActive() : false
                ))
                .build();
    }

    /**
     * Build optimized query for suggestions (autocomplete)
     * Heavily prioritizes prefix matching for better autocomplete UX
     */
    private Query buildSuggestionQuery(String queryText, List<String> allowedCompanies, String companyField) {
        String lowerQuery = queryText.toLowerCase().trim();
        
        // Base query focusing on prefix matching
        Query prefixQuery = Query.of(q -> q.bool(b -> b
                .should(
                        // Prefix match - highest priority for autocomplete
                        Query.of(sq -> sq.multiMatch(m -> m
                                .query(lowerQuery)
                                .fields("userName^6", "firstName^5", "lastName^5", "name^5", "code^4", "email^2", "address^3")
                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.PhrasePrefix)
                                .boost(4.0f))),
                        
                        // Wildcard as fallback
                        Query.of(sq -> sq.queryString(qs -> qs
                                .query("*" + lowerQuery + "*")
                                .fields("userName^3", "firstName^2", "lastName^2", "name^2", "code^2")
                                .boost(1.0f)
                                .analyzeWildcard(true)))
                )
                .minimumShouldMatch("1")));
        
        // Add tenant filter if needed
        if (!securityService.isAdmin() && !allowedCompanies.isEmpty()) {
            Query tenantQuery = Query.of(q -> q
                    .terms(t -> t
                            .field(companyField)
                            .terms(terms -> terms.value(allowedCompanies.stream()
                                    .map(c -> co.elastic.clients.elasticsearch._types.FieldValue.of(c))
                                    .collect(Collectors.toList())))));
            
            return Query.of(q -> q.bool(b -> b
                    .must(prefixQuery)
                    .filter(tenantQuery)));
        }
        
        return prefixQuery;
    }

    // Suggestion methods
    private List<SearchSuggestion> getUserSuggestions(String query, List<String> allowedCompanies, int limit) {
        try {
            Query suggestionQuery = buildSuggestionQuery(query, allowedCompanies, "companyExternalIds");
            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(suggestionQuery)
                    .withPageable(PageRequest.of(0, limit))
                    .build();
            
            SearchHits<UserDocument> hits = elasticsearchOperations.search(nativeQuery, UserDocument.class);
            
            return hits.stream()
                    .map(hit -> {
                        UserDocument doc = hit.getContent();
                        return SearchSuggestion.builder()
                                .text(doc.getFirstName() + " " + doc.getLastName() + " (" + doc.getUserName() + ")")
                                .type("user")
                                .category("Users")
                                .url("/users/" + doc.getExternalId())
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get user suggestions", e);
            return List.of();
        }
    }

    private List<SearchSuggestion> getBikeSuggestions(String query, List<String> allowedCompanies, int limit) {
        try {
            Query suggestionQuery = buildSuggestionQuery(query, allowedCompanies, "companyExternalId");
            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(suggestionQuery)
                    .withPageable(PageRequest.of(0, limit))
                    .build();
            
            SearchHits<BikeDocument> hits = elasticsearchOperations.search(nativeQuery, BikeDocument.class);
            
            return hits.stream()
                    .map(hit -> {
                        BikeDocument doc = hit.getContent();
                        return SearchSuggestion.builder()
                                .text(doc.getCode() != null ? doc.getCode() : "Bike " + doc.getExternalId())
                                .type("bike")
                                .category("Bikes")
                                .url("/bikes/" + doc.getExternalId())
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get bike suggestions", e);
            return List.of();
        }
    }

    private List<SearchSuggestion> getLocationSuggestions(String query, List<String> allowedCompanies, int limit) {
        try {
            Query suggestionQuery = buildSuggestionQuery(query, allowedCompanies, "companyExternalId");
            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(suggestionQuery)
                    .withPageable(PageRequest.of(0, limit))
                    .build();
            
            SearchHits<LocationDocument> hits = elasticsearchOperations.search(nativeQuery, LocationDocument.class);
            
            return hits.stream()
                    .map(hit -> {
                        LocationDocument doc = hit.getContent();
                        return SearchSuggestion.builder()
                                .text(doc.getName() + " - " + doc.getAddress())
                                .type("location")
                                .category("Locations")
                                .url("/locations/" + doc.getExternalId())
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get location suggestions", e);
            return List.of();
        }
    }

    private List<SearchSuggestion> getHubSuggestions(String query, List<String> allowedCompanies, int limit) {
        try {
            Query suggestionQuery = buildSuggestionQuery(query, allowedCompanies, "companyExternalId");
            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(suggestionQuery)
                    .withPageable(PageRequest.of(0, limit))
                    .build();
            
            SearchHits<HubDocument> hits = elasticsearchOperations.search(nativeQuery, HubDocument.class);
            
            return hits.stream()
                    .map(hit -> {
                        HubDocument doc = hit.getContent();
                        return SearchSuggestion.builder()
                                .text(doc.getName())
                                .type("hub")
                                .category("Hubs")
                                .url("/hubs/" + doc.getExternalId())
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get hub suggestions", e);
            return List.of();
        }
    }
}
