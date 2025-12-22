package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.PartBrandDTO;
import org.clickenrent.rentalservice.service.PartBrandService;
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

@WebMvcTest(PartBrandController.class)
@AutoConfigureMockMvc
class PartBrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartBrandService partBrandService;

    private PartBrandDTO brandDTO;

    @BeforeEach
    void setUp() {
        brandDTO = PartBrandDTO.builder()
                .id(1L)
                .name("Samsung")
                .companyExternalId("company-ext-001")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBrands_ReturnsOk() throws Exception {
        Page<PartBrandDTO> page = new PageImpl<>(Collections.singletonList(brandDTO));
        when(partBrandService.getAllBrands(any())).thenReturn(page);

        mockMvc.perform(get("/api/part-brands").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBrandsByCompany_ReturnsOk() throws Exception {
        List<PartBrandDTO> brands = Collections.singletonList(brandDTO);
        when(partBrandService.getBrandsByCompanyExternalId("company-ext-001")).thenReturn(brands);

        mockMvc.perform(get("/api/part-brands/by-company/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Samsung"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBrandById_ReturnsOk() throws Exception {
        when(partBrandService.getBrandById(1L)).thenReturn(brandDTO);

        mockMvc.perform(get("/api/part-brands/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Samsung"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBrand_ReturnsCreated() throws Exception {
        when(partBrandService.createBrand(any())).thenReturn(brandDTO);

        mockMvc.perform(post("/api/part-brands")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBrand_ReturnsOk() throws Exception {
        when(partBrandService.updateBrand(eq(1L), any())).thenReturn(brandDTO);

        mockMvc.perform(put("/api/part-brands/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBrand_ReturnsNoContent() throws Exception {
        doNothing().when(partBrandService).deleteBrand(1L);

        mockMvc.perform(delete("/api/part-brands/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




