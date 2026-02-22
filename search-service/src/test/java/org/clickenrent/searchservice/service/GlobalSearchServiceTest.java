package org.clickenrent.searchservice.service;

import org.clickenrent.searchservice.dto.GlobalSearchResponse;
import org.clickenrent.searchservice.dto.SearchSuggestion;
import org.clickenrent.searchservice.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalSearchService.
 *
 * @author Vitaliy Shvetsov
 */
@ExtendWith(MockitoExtension.class)
class GlobalSearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private GlobalSearchService globalSearchService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(elasticsearchOperations.search(any(NativeQuery.class), any(Class.class))).thenAnswer(inv -> emptySearchHits());
    }

    @SuppressWarnings("unchecked")
    private static <T> SearchHits<T> emptySearchHits() {
        SearchHits<T> hits = mock(SearchHits.class);
        when(hits.stream()).thenReturn(Stream.empty());
        return hits;
    }

    @Test
    void search_whenAdmin_returnsEmptyResultsWithoutTenantFilter() {
        // Given
        when(securityService.isAdmin()).thenReturn(true);

        // When
        GlobalSearchResponse response = globalSearchService.search(
                "test", Set.of("users", "bikes"), null, 20);

        // Then
        assertNotNull(response);
        assertEquals("test", response.getQuery());
        assertEquals(0, response.getTotalResults());
        assertTrue(response.getResults().containsKey("users"));
        assertTrue(response.getResults().containsKey("bikes"));
        assertTrue(response.getResults().get("users").isEmpty());
        assertTrue(response.getResults().get("bikes").isEmpty());
        assertNotNull(response.getSearchTimeMs());
    }

    @Test
    void search_whenNotAdminAndCompanyProvided_usesCompanyFilter() {
        // Given
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserCompanyExternalIds()).thenReturn(List.of());

        // When
        GlobalSearchResponse response = globalSearchService.search(
                "john", Set.of("users"), "company-1", 10);

        // Then
        assertNotNull(response);
        assertEquals("john", response.getQuery());
        assertEquals(1, response.getResults().size());
        assertTrue(response.getResults().get("users").isEmpty());
        verify(elasticsearchOperations, atLeast(1)).search(any(NativeQuery.class), eq(org.clickenrent.searchservice.document.UserDocument.class));
    }

    @Test
    void search_withMultipleTypes_invokesSearchPerType() {
        // Given
        when(securityService.isAdmin()).thenReturn(true);

        // When
        globalSearchService.search("q", Set.of("users", "locations", "hubs"), null, 5);

        // Then - each type triggers one search
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(org.clickenrent.searchservice.document.UserDocument.class));
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(org.clickenrent.searchservice.document.LocationDocument.class));
        verify(elasticsearchOperations, times(1)).search(any(NativeQuery.class), eq(org.clickenrent.searchservice.document.HubDocument.class));
    }

    @Test
    void getSuggestions_returnsLimitedSuggestions() {
        // Given
        when(securityService.isAdmin()).thenReturn(true);

        // When
        List<SearchSuggestion> suggestions = globalSearchService.getSuggestions("ab", null, 10);

        // Then
        assertNotNull(suggestions);
        assertTrue(suggestions.size() <= 10);
    }

    @Test
    void getSuggestions_withCompanyFilter_callsSecurityService() {
        // Given
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserCompanyExternalIds()).thenReturn(List.of("company-1"));

        // When
        globalSearchService.getSuggestions("x", "company-1", 8);

        // Then
        verify(securityService).isAdmin();
    }
}
