package org.clickenrent.rentalservice.controller;

import org.clickenrent.rentalservice.dto.LockProviderDTO;
import org.clickenrent.rentalservice.service.LockProviderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LockProviderController.class)
class LockProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LockProviderService lockProviderService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllLockProviders() throws Exception {
        // Given
        LockProviderDTO dto = LockProviderDTO.builder()
                .id(1L)
                .name("AXA")
                .isActive(true)
                .build();
        Page<LockProviderDTO> page = new PageImpl<>(List.of(dto));
        when(lockProviderService.getAllLockProviders(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/lock-providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("AXA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetLockProviderById() throws Exception {
        // Given
        LockProviderDTO dto = LockProviderDTO.builder()
                .id(1L)
                .name("AXA")
                .isActive(true)
                .build();
        when(lockProviderService.getLockProviderById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/lock-providers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("AXA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateLockProvider() throws Exception {
        // Given
        LockProviderDTO dto = LockProviderDTO.builder()
                .id(1L)
                .name("AXA")
                .isActive(true)
                .build();
        when(lockProviderService.createLockProvider(any(LockProviderDTO.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(post("/api/lock-providers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"AXA\",\"isActive\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("AXA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateLockProvider() throws Exception {
        // Given
        LockProviderDTO dto = LockProviderDTO.builder()
                .id(1L)
                .name("OMNI")
                .isActive(true)
                .build();
        when(lockProviderService.updateLockProvider(eq(1L), any(LockProviderDTO.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(put("/api/lock-providers/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"OMNI\",\"isActive\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("OMNI"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLockProvider() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/lock-providers/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}








