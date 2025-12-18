package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.ServiceProductDTO;
import org.clickenrent.rentalservice.service.ServiceProductService;
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

@WebMvcTest(ServiceProductController.class)
@AutoConfigureMockMvc
class ServiceProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceProductService serviceProductService;

    private ServiceProductDTO serviceProductDTO;

    @BeforeEach
    void setUp() {
        serviceProductDTO = ServiceProductDTO.builder()
                .id(1L)
                .externalId("SP001")
                .serviceId(1L)
                .productId(1L)
                .isB2BRentable(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllServiceProducts_ReturnsOk() throws Exception {
        Page<ServiceProductDTO> page = new PageImpl<>(Collections.singletonList(serviceProductDTO));
        when(serviceProductService.getAllServiceProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/service-products").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getServiceProductById_ReturnsOk() throws Exception {
        when(serviceProductService.getServiceProductById(1L)).thenReturn(serviceProductDTO);

        mockMvc.perform(get("/api/service-products/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("SP001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createServiceProduct_ReturnsCreated() throws Exception {
        when(serviceProductService.createServiceProduct(any())).thenReturn(serviceProductDTO);

        mockMvc.perform(post("/api/service-products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceProductDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteServiceProduct_ReturnsNoContent() throws Exception {
        doNothing().when(serviceProductService).deleteServiceProduct(1L);

        mockMvc.perform(delete("/api/service-products/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

