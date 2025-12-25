package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.KeyDTO;
import org.clickenrent.rentalservice.service.KeyService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KeyController.class)
@AutoConfigureMockMvc
class KeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KeyService keyService;

    private KeyDTO keyDTO;

    @BeforeEach
    void setUp() {
        keyDTO = KeyDTO.builder()
                .id(1L)
                .externalId("KEY001")
                .lockId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getKeysByLock_ReturnsOk() throws Exception {
        List<KeyDTO> keys = Arrays.asList(keyDTO);
        when(keyService.getKeysByLock(1L)).thenReturn(keys);

        mockMvc.perform(get("/api/keys/by-lock/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].externalId").value("KEY001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getKeyById_ReturnsOk() throws Exception {
        when(keyService.getKeyById(1L)).thenReturn(keyDTO);

        mockMvc.perform(get("/api/keys/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("KEY001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createKey_ReturnsCreated() throws Exception {
        when(keyService.createKey(any())).thenReturn(keyDTO);

        mockMvc.perform(post("/api/keys")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(keyDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteKey_ReturnsNoContent() throws Exception {
        doNothing().when(keyService).deleteKey(1L);

        mockMvc.perform(delete("/api/keys/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}






