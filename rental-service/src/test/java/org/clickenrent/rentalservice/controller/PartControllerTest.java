package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.PartDTO;
import org.clickenrent.rentalservice.service.PartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartController.class)
@AutoConfigureMockMvc
class PartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartService partService;

    private PartDTO partDTO;

    @BeforeEach
    void setUp() {
        partDTO = PartDTO.builder()
                .id(1L)
                .externalId("PART001")
                .name("Test Part")
                .partCategoryId(1L)
                .partBrandId(1L)
                .hubId(1L)
                .quantity(10)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllParts_ReturnsOk() throws Exception {
        Page<PartDTO> page = new PageImpl<>(Collections.singletonList(partDTO));
        when(partService.getAllParts(any())).thenReturn(page);

        mockMvc.perform(get("/api/parts").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPartById_ReturnsOk() throws Exception {
        when(partService.getPartById(1L)).thenReturn(partDTO);

        mockMvc.perform(get("/api/parts/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("PART001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPart_ReturnsCreated() throws Exception {
        when(partService.createPart(any())).thenReturn(partDTO);

        mockMvc.perform(post("/api/parts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePart_ReturnsOk() throws Exception {
        when(partService.updatePart(eq(1L), any())).thenReturn(partDTO);

        mockMvc.perform(put("/api/parts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePart_ReturnsNoContent() throws Exception {
        doNothing().when(partService).deletePart(1L);

        mockMvc.perform(delete("/api/parts/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
