package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.ChargingStationBrandDTO;
import org.clickenrent.rentalservice.service.ChargingStationBrandService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChargingStationBrandController.class)
@AutoConfigureMockMvc
class ChargingStationBrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargingStationBrandService chargingStationBrandService;

    private ChargingStationBrandDTO brandDTO;

    @BeforeEach
    void setUp() {
        brandDTO = ChargingStationBrandDTO.builder()
                .id(1L)
                .name("Tesla")
                .companyId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBrands_ReturnsOk() throws Exception {
        Page<ChargingStationBrandDTO> page = new PageImpl<>(Collections.singletonList(brandDTO));
        when(chargingStationBrandService.getAllBrands(any())).thenReturn(page);

        mockMvc.perform(get("/api/charging-station-brands").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBrandsByCompany_ReturnsOk() throws Exception {
        List<ChargingStationBrandDTO> brands = Collections.singletonList(brandDTO);
        when(chargingStationBrandService.getBrandsByCompany(1L)).thenReturn(brands);

        mockMvc.perform(get("/api/charging-station-brands/by-company/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tesla"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBrandById_ReturnsOk() throws Exception {
        when(chargingStationBrandService.getBrandById(1L)).thenReturn(brandDTO);

        mockMvc.perform(get("/api/charging-station-brands/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tesla"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBrand_ReturnsCreated() throws Exception {
        when(chargingStationBrandService.createBrand(any())).thenReturn(brandDTO);

        mockMvc.perform(post("/api/charging-station-brands")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createBrand_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/charging-station-brands")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBrand_ReturnsOk() throws Exception {
        when(chargingStationBrandService.updateBrand(eq(1L), any())).thenReturn(brandDTO);

        mockMvc.perform(put("/api/charging-station-brands/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBrand_ReturnsNoContent() throws Exception {
        doNothing().when(chargingStationBrandService).deleteBrand(1L);

        mockMvc.perform(delete("/api/charging-station-brands/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
