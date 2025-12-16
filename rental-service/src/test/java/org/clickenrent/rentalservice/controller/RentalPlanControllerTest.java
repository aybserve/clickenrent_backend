package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.RentalPlanDTO;
import org.clickenrent.rentalservice.service.RentalPlanService;
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

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalPlanController.class)
@AutoConfigureMockMvc
class RentalPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalPlanService rentalPlanService;

    private RentalPlanDTO planDTO;

    @BeforeEach
    void setUp() {
        planDTO = RentalPlanDTO.builder()
                .id(1L)
                .name("Daily Plan")
                .rentalUnitId(1L)
                .minUnit(1)
                .maxUnit(24)
                .locationId(1L)
                .defaultPrice(new BigDecimal("25.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllRentalPlans_ReturnsOk() throws Exception {
        Page<RentalPlanDTO> page = new PageImpl<>(Collections.singletonList(planDTO));
        when(rentalPlanService.getAllRentalPlans(any())).thenReturn(page);

        mockMvc.perform(get("/api/rental-plans").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalPlanById_ReturnsOk() throws Exception {
        when(rentalPlanService.getRentalPlanById(1L)).thenReturn(planDTO);

        mockMvc.perform(get("/api/rental-plans/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Daily Plan"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRentalPlan_ReturnsCreated() throws Exception {
        when(rentalPlanService.createRentalPlan(any())).thenReturn(planDTO);

        mockMvc.perform(post("/api/rental-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRentalPlan_ReturnsOk() throws Exception {
        when(rentalPlanService.updateRentalPlan(eq(1L), any())).thenReturn(planDTO);

        mockMvc.perform(put("/api/rental-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRentalPlan_ReturnsNoContent() throws Exception {
        doNothing().when(rentalPlanService).deleteRentalPlan(1L);

        mockMvc.perform(delete("/api/rental-plans/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
