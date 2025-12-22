package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.RentalUnitDTO;
import org.clickenrent.rentalservice.service.RentalUnitService;
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

@WebMvcTest(RentalUnitController.class)
@AutoConfigureMockMvc
class RentalUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalUnitService rentalUnitService;

    private RentalUnitDTO unitDTO;

    @BeforeEach
    void setUp() {
        unitDTO = RentalUnitDTO.builder()
                .id(1L)
                .name("Day")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllUnits_ReturnsOk() throws Exception {
        when(rentalUnitService.getAllUnits()).thenReturn(Arrays.asList(unitDTO));

        mockMvc.perform(get("/api/rental-units").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Day"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUnitById_ReturnsOk() throws Exception {
        when(rentalUnitService.getUnitById(1L)).thenReturn(unitDTO);

        mockMvc.perform(get("/api/rental-units/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Day"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUnit_ReturnsCreated() throws Exception {
        when(rentalUnitService.createUnit(any())).thenReturn(unitDTO);

        mockMvc.perform(post("/api/rental-units")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unitDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUnit_ReturnsNoContent() throws Exception {
        doNothing().when(rentalUnitService).deleteUnit(1L);

        mockMvc.perform(delete("/api/rental-units/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




