package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeBrandDTO;
import org.clickenrent.rentalservice.service.BikeBrandService;
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

@WebMvcTest(BikeBrandController.class)
@AutoConfigureMockMvc
class BikeBrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeBrandService bikeBrandService;

    private BikeBrandDTO brandDTO;

    @BeforeEach
    void setUp() {
        brandDTO = BikeBrandDTO.builder()
                .id(1L)
                .externalId("BB001")
                .name("VanMoof")
                .companyId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeBrands_ReturnsOk() throws Exception {
        Page<BikeBrandDTO> page = new PageImpl<>(Collections.singletonList(brandDTO));
        when(bikeBrandService.getAllBikeBrands(any())).thenReturn(page);

        mockMvc.perform(get("/api/bike-brands").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeBrandById_ReturnsOk() throws Exception {
        when(bikeBrandService.getBikeBrandById(1L)).thenReturn(brandDTO);

        mockMvc.perform(get("/api/bike-brands/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("VanMoof"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeBrand_ReturnsCreated() throws Exception {
        when(bikeBrandService.createBikeBrand(any())).thenReturn(brandDTO);

        mockMvc.perform(post("/api/bike-brands")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createBikeBrand_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/bike-brands")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBikeBrand_ReturnsOk() throws Exception {
        when(bikeBrandService.updateBikeBrand(eq(1L), any())).thenReturn(brandDTO);

        mockMvc.perform(put("/api/bike-brands/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeBrand_ReturnsNoContent() throws Exception {
        doNothing().when(bikeBrandService).deleteBikeBrand(1L);

        mockMvc.perform(delete("/api/bike-brands/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
