package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.SupportRequestDTO;
import org.clickenrent.supportservice.service.SupportRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupportRequestController.class)
@AutoConfigureMockMvc
class SupportRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupportRequestService supportRequestService;

    private SupportRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = SupportRequestDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440401")
                .userExternalId("user-uuid-1")
                .bikeExternalId("bike-uuid-201")
                .isNearLocation(true)
                .photoUrl("https://example.com/photo.jpg")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(supportRequestService.getAll()).thenReturn(Arrays.asList(requestDTO));

        mockMvc.perform(get("/api/support-requests").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userExternalId").value("user-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_ReturnsOk() throws Exception {
        when(supportRequestService.getById(1L)).thenReturn(requestDTO);

        mockMvc.perform(get("/api/support-requests/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value("user-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_ReturnsCreated() throws Exception {
        when(supportRequestService.create(any())).thenReturn(requestDTO);

        mockMvc.perform(post("/api/support-requests")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void update_ReturnsOk() throws Exception {
        when(supportRequestService.update(eq(1L), any())).thenReturn(requestDTO);

        mockMvc.perform(put("/api/support-requests/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(supportRequestService).delete(1L);

        mockMvc.perform(delete("/api/support-requests/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




