package org.clickenrent.rentalservice.controller;

import org.clickenrent.rentalservice.dto.LockStatusDTO;
import org.clickenrent.rentalservice.service.LockStatusService;
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

@WebMvcTest(LockStatusController.class)
class LockStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LockStatusService lockStatusService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllLockStatuses() throws Exception {
        // Given
        LockStatusDTO dto = LockStatusDTO.builder()
                .id(1L)
                .name("locked")
                .build();
        Page<LockStatusDTO> page = new PageImpl<>(List.of(dto));
        when(lockStatusService.getAllLockStatuses(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/lock-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("locked"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetLockStatusById() throws Exception {
        // Given
        LockStatusDTO dto = LockStatusDTO.builder()
                .id(1L)
                .name("locked")
                .build();
        when(lockStatusService.getLockStatusById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/lock-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("locked"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateLockStatus() throws Exception {
        // Given
        LockStatusDTO dto = LockStatusDTO.builder()
                .id(1L)
                .name("locked")
                .build();
        when(lockStatusService.createLockStatus(any(LockStatusDTO.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(post("/api/lock-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"locked\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("locked"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateLockStatus() throws Exception {
        // Given
        LockStatusDTO dto = LockStatusDTO.builder()
                .id(1L)
                .name("unlocked")
                .build();
        when(lockStatusService.updateLockStatus(eq(1L), any(LockStatusDTO.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(put("/api/lock-statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"unlocked\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("unlocked"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLockStatus() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/lock-statuses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
