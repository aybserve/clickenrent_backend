package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BatteryChargeStatusDTO;
import org.clickenrent.rentalservice.service.BatteryChargeStatusService;
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

@WebMvcTest(BatteryChargeStatusController.class)
@AutoConfigureMockMvc
class BatteryChargeStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BatteryChargeStatusService batteryChargeStatusService;

    private BatteryChargeStatusDTO statusDTO;

    @BeforeEach
    void setUp() {
        statusDTO = BatteryChargeStatusDTO.builder()
                .id(1L)
                .name("Charging")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllStatuses_ReturnsOk() throws Exception {
        when(batteryChargeStatusService.getAllStatuses()).thenReturn(Arrays.asList(statusDTO));

        mockMvc.perform(get("/api/battery-charge-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Charging"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStatusById_ReturnsOk() throws Exception {
        when(batteryChargeStatusService.getStatusById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/battery-charge-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charging"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatus_ReturnsCreated() throws Exception {
        when(batteryChargeStatusService.createStatus(any())).thenReturn(statusDTO);

        mockMvc.perform(post("/api/battery-charge-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStatus_ReturnsOk() throws Exception {
        when(batteryChargeStatusService.updateStatus(eq(1L), any())).thenReturn(statusDTO);

        mockMvc.perform(put("/api/battery-charge-statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStatus_ReturnsNoContent() throws Exception {
        doNothing().when(batteryChargeStatusService).deleteStatus(1L);

        mockMvc.perform(delete("/api/battery-charge-statuses/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




