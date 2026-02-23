package org.clickenrent.searchservice.controller;

import org.clickenrent.searchservice.dto.GlobalSearchResponse;
import org.clickenrent.searchservice.dto.SearchSuggestion;
import org.clickenrent.searchservice.exception.GlobalExceptionHandler;
import org.clickenrent.searchservice.security.SecurityService;
import org.clickenrent.searchservice.service.GlobalSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SearchController.
 *
 * @author Vitaliy Shvetsov
 */
@WebMvcTest(SearchController.class)
@Import({org.clickenrent.searchservice.config.SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktMjU2LWJpdC1rZXk=")
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GlobalSearchService searchService;

    @MockBean
    private SecurityService securityService;

    @Test
    @WithMockUser
    void search_withValidQuery_returnsOkAndCallsService() throws Exception {
        GlobalSearchResponse response = GlobalSearchResponse.builder()
                .query("john")
                .results(Map.of("users", List.of()))
                .totalResults(0)
                .searchTimeMs(10L)
                .build();
        when(searchService.search(eq("john"), anySet(), isNull(), eq(20)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value("john"))
                .andExpect(jsonPath("$.totalResults").value(0));

        verify(searchService).search(eq("john"), anySet(), isNull(), eq(20));
    }

    @Test
    @WithMockUser
    void search_withTypesAndCompanyAndLimit_passesToService() throws Exception {
        GlobalSearchResponse response = GlobalSearchResponse.builder()
                .query("bike")
                .results(Map.of("bikes", List.of()))
                .totalResults(0)
                .searchTimeMs(5L)
                .build();
        when(searchService.search(eq("bike"), anySet(), eq("company-1"), eq(10)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/search")
                        .param("q", "bike")
                        .param("types", "bikes")
                        .param("companyExternalId", "company-1")
                        .param("limit", "10"))
                .andExpect(status().isOk());

        verify(searchService).search(eq("bike"), anySet(), eq("company-1"), eq(10));
    }

    @Test
    @WithMockUser
    void getSuggestions_withValidQuery_returnsOkAndCallsService() throws Exception {
        List<SearchSuggestion> suggestions = List.of(
                SearchSuggestion.builder().text("John Doe").type("user").category("Users").url("/users/1").build()
        );
        when(searchService.getSuggestions(eq("jo"), isNull(), eq(10))).thenReturn(suggestions);

        mockMvc.perform(get("/api/v1/search/suggestions").param("q", "jo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("John Doe"))
                .andExpect(jsonPath("$[0].type").value("user"));

        verify(searchService).getSuggestions(eq("jo"), isNull(), eq(10));
    }

    @Test
    @WithMockUser
    void search_withQueryTooShort_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/search").param("q", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/search").param("q", "john"))
                .andExpect(status().isUnauthorized());
    }
}
