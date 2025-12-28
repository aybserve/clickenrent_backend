package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.LockDTO;
import org.clickenrent.rentalservice.service.LockService;
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

@WebMvcTest(LockController.class)
@AutoConfigureMockMvc
class LockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LockService lockService;

    private LockDTO lockDTO;

    @BeforeEach
    void setUp() {
        lockDTO = LockDTO.builder()
                .id(1L)
                .externalId("LOCK001")
                .macAddress("AA:BB:CC:DD:EE:FF")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllLocks_ReturnsOk() throws Exception {
        Page<LockDTO> page = new PageImpl<>(Collections.singletonList(lockDTO));
        when(lockService.getAllLocks(any())).thenReturn(page);

        mockMvc.perform(get("/api/locks").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLockById_ReturnsOk() throws Exception {
        when(lockService.getLockById(1L)).thenReturn(lockDTO);

        mockMvc.perform(get("/api/locks/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("LOCK001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLock_ReturnsCreated() throws Exception {
        when(lockService.createLock(any())).thenReturn(lockDTO);

        mockMvc.perform(post("/api/locks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lockDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLock_ReturnsOk() throws Exception {
        when(lockService.updateLock(eq(1L), any())).thenReturn(lockDTO);

        mockMvc.perform(put("/api/locks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lockDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLock_ReturnsNoContent() throws Exception {
        doNothing().when(lockService).deleteLock(1L);

        mockMvc.perform(delete("/api/locks/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








