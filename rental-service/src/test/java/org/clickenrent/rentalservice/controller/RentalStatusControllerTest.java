package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.RentalStatusDTO;
import org.clickenrent.rentalservice.service.RentalStatusService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalStatusController.class)
@AutoConfigureMockMvc
class RentalStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalStatusService rentalStatusService;

    private RentalStatusDTO statusDTO;

    @BeforeEach
    void setUp() {
        statusDTO = RentalStatusDTO.builder()
                .id(1L)
                .name("Active")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllStatuses_ReturnsOk() throws Exception {
        when(rentalStatusService.getAllStatuses()).thenReturn(Arrays.asList(statusDTO));

        mockMvc.perform(get("/api/rental-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Active"));
    }

    @Test
    void getAllStatuses_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/rental-statuses").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStatusById_ReturnsOk() throws Exception {
        when(rentalStatusService.getStatusById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/rental-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Active"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatus_ReturnsCreated() throws Exception {
        when(rentalStatusService.createStatus(any())).thenReturn(statusDTO);

        mockMvc.perform(post("/api/rental-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createStatus_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/rental-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStatus_ReturnsOk() throws Exception {
        when(rentalStatusService.updateStatus(eq(1L), any())).thenReturn(statusDTO);

        mockMvc.perform(put("/api/rental-statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStatus_ReturnsNoContent() throws Exception {
        doNothing().when(rentalStatusService).deleteStatus(1L);

        mockMvc.perform(delete("/api/rental-statuses/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
