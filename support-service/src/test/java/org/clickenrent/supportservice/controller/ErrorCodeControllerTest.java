package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.ErrorCodeDTO;
import org.clickenrent.supportservice.service.ErrorCodeService;
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

@WebMvcTest(ErrorCodeController.class)
@AutoConfigureMockMvc
class ErrorCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ErrorCodeService errorCodeService;

    private ErrorCodeDTO errorCodeDTO;

    @BeforeEach
    void setUp() {
        errorCodeDTO = ErrorCodeDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440201")
                .name("E001")
                .bikeEngineId(1L)
                .description("Battery Low Voltage")
                .isFixableByClient(false)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(errorCodeService.getAll()).thenReturn(Arrays.asList(errorCodeDTO));

        mockMvc.perform(get("/api/error-codes").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("E001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(errorCodeService.getById(1L)).thenReturn(errorCodeDTO);

        mockMvc.perform(get("/api/error-codes/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("E001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(errorCodeService.create(any())).thenReturn(errorCodeDTO);

        mockMvc.perform(post("/api/error-codes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(errorCodeDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(errorCodeService.update(eq(1L), any())).thenReturn(errorCodeDTO);

        mockMvc.perform(put("/api/error-codes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(errorCodeDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(errorCodeService).delete(1L);

        mockMvc.perform(delete("/api/error-codes/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

