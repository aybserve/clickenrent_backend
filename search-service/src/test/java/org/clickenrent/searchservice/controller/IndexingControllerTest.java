package org.clickenrent.searchservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.contracts.search.IndexEventRequest.IndexOperation;
import org.clickenrent.searchservice.dto.BulkSyncRequest;
import org.clickenrent.searchservice.dto.BulkSyncResponse;
import org.clickenrent.searchservice.security.SecurityService;
import org.clickenrent.searchservice.service.IndexingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for IndexingController.
 *
 * @author Vitaliy Shvetsov
 */
@WebMvcTest(IndexingController.class)
@Import(org.clickenrent.searchservice.config.SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktMjU2LWJpdC1rZXk=")
class IndexingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IndexingService indexingService;

    @MockBean
    private SecurityService securityService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void bulkSync_withValidRequest_returnsOkAndCallsService() throws Exception {
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of("users", "bikes"))
                .companyExternalId("company-1")
                .build();
        BulkSyncResponse response = BulkSyncResponse.builder()
                .indexedCounts(Map.of("users", 10, "bikes", 5))
                .errors(Map.of())
                .status("SUCCESS")
                .durationMs(100L)
                .build();
        when(indexingService.bulkSync(any(BulkSyncRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/index/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(indexingService).bulkSync(any(BulkSyncRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void bulkSync_withEmptyEntityTypes_returnsBadRequest() throws Exception {
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of())
                .build();

        mockMvc.perform(post("/api/v1/index/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void processIndexEvent_withValidEvent_returnsAcceptedAndCallsService() throws Exception {
        IndexEventRequest event = IndexEventRequest.builder()
                .entityType("user")
                .entityId("user-123")
                .operation(IndexOperation.CREATE)
                .build();

        mockMvc.perform(post("/api/v1/index/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isAccepted());

        verify(indexingService).processIndexEvent(any(IndexEventRequest.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void bulkSync_withoutAdminRole_returnsForbidden() throws Exception {
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of("users"))
                .build();
        mockMvc.perform(post("/api/v1/index/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void bulkSync_withoutAuth_returnsUnauthorized() throws Exception {
        BulkSyncRequest request = BulkSyncRequest.builder()
                .entityTypes(List.of("users"))
                .build();
        mockMvc.perform(post("/api/v1/index/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
