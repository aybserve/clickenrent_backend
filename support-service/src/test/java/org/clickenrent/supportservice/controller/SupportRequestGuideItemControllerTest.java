package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.SupportRequestGuideItemDTO;
import org.clickenrent.supportservice.service.SupportRequestGuideItemService;
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

@WebMvcTest(SupportRequestGuideItemController.class)
@AutoConfigureMockMvc
class SupportRequestGuideItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupportRequestGuideItemService supportRequestGuideItemService;

    private SupportRequestGuideItemDTO guideItemDTO;

    @BeforeEach
    void setUp() {
        guideItemDTO = SupportRequestGuideItemDTO.builder()
                .id(1L)
                .itemIndex(1)
                .description("Check if battery is properly connected")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(supportRequestGuideItemService.getAll()).thenReturn(Arrays.asList(guideItemDTO));

        mockMvc.perform(get("/api/support-request-guide-items").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemIndex").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_ReturnsOk() throws Exception {
        when(supportRequestGuideItemService.getById(1L)).thenReturn(guideItemDTO);

        mockMvc.perform(get("/api/support-request-guide-items/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemIndex").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(supportRequestGuideItemService.create(any())).thenReturn(guideItemDTO);

        mockMvc.perform(post("/api/support-request-guide-items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guideItemDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(supportRequestGuideItemService.update(eq(1L), any())).thenReturn(guideItemDTO);

        mockMvc.perform(put("/api/support-request-guide-items/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guideItemDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(supportRequestGuideItemService).delete(1L);

        mockMvc.perform(delete("/api/support-request-guide-items/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
