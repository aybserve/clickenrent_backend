package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.ServiceDTO;
import org.clickenrent.rentalservice.service.ServiceService;
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

@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceService serviceService;

    private ServiceDTO serviceDTO;

    @BeforeEach
    void setUp() {
        serviceDTO = ServiceDTO.builder()
                .id(1L)
                .name("Bike Maintenance")
                .b2bSubscriptionPrice(new BigDecimal("50.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllServices_ReturnsOk() throws Exception {
        Page<ServiceDTO> page = new PageImpl<>(Collections.singletonList(serviceDTO));
        when(serviceService.getAllServices(any())).thenReturn(page);

        mockMvc.perform(get("/api/services").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getServiceById_ReturnsOk() throws Exception {
        when(serviceService.getServiceById(1L)).thenReturn(serviceDTO);

        mockMvc.perform(get("/api/services/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bike Maintenance"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createService_ReturnsCreated() throws Exception {
        when(serviceService.createService(any())).thenReturn(serviceDTO);

        mockMvc.perform(post("/api/services")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateService_ReturnsOk() throws Exception {
        when(serviceService.updateService(eq(1L), any())).thenReturn(serviceDTO);

        mockMvc.perform(put("/api/services/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteService_ReturnsNoContent() throws Exception {
        doNothing().when(serviceService).deleteService(1L);

        mockMvc.perform(delete("/api/services/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




