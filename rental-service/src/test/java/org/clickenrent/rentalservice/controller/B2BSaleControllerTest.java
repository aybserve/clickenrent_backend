package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSaleDTO;
import org.clickenrent.rentalservice.service.B2BSaleService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for B2BSaleController.
 */
@WebMvcTest(B2BSaleController.class)
@AutoConfigureMockMvc
class B2BSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSaleService b2bSaleService;

    private B2BSaleDTO b2bSaleDTO;

    @BeforeEach
    void setUp() {
        b2bSaleDTO = B2BSaleDTO.builder()
                .id(1L)
                .externalId("B2BS001")
                .locationId(1L)
                .b2bSaleStatusId(2L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllSales_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<B2BSaleDTO> page = new PageImpl<>(Collections.singletonList(b2bSaleDTO));
        when(b2bSaleService.getAllSales(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/b2b-sales")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].externalId").value("B2BS001"));

        verify(b2bSaleService, times(1)).getAllSales(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSales_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<B2BSaleDTO> page = new PageImpl<>(Collections.singletonList(b2bSaleDTO));
        when(b2bSaleService.getAllSales(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/b2b-sales")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(b2bSaleService, times(1)).getAllSales(any());
    }
    @Test
    @WithMockUser(roles = "B2B")
    void getSalesByLocation_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSaleService.getSalesByLocation(1L)).thenReturn(Arrays.asList(b2bSaleDTO));

        // When & Then
        mockMvc.perform(get("/api/b2b-sales/by-location/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(b2bSaleService, times(1)).getSalesByLocation(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSaleById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSaleService.getSaleById(1L)).thenReturn(b2bSaleDTO);

        // When & Then
        mockMvc.perform(get("/api/b2b-sales/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.externalId").value("B2BS001"));

        verify(b2bSaleService, times(1)).getSaleById(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getSaleById_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSaleService.getSaleById(1L)).thenReturn(b2bSaleDTO);

        // When & Then
        mockMvc.perform(get("/api/b2b-sales/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(b2bSaleService, times(1)).getSaleById(1L);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createSale_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        B2BSaleDTO newSale = B2BSaleDTO.builder()
                .externalId("B2BS002")
                .locationId(1L)
                .b2bSaleStatusId(1L)
                .build();

        B2BSaleDTO createdSale = B2BSaleDTO.builder()
                .id(2L)
                .externalId("B2BS002")
                .locationId(1L)
                .b2bSaleStatusId(1L)
                .build();

        when(b2bSaleService.createSale(any(B2BSaleDTO.class))).thenReturn(createdSale);

        // When & Then
        mockMvc.perform(post("/api/b2b-sales")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSale)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.externalId").value("B2BS002"));

        verify(b2bSaleService, times(1)).createSale(any(B2BSaleDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createSale_WithB2BRole_ReturnsCreated() throws Exception {
        // Given
        when(b2bSaleService.createSale(any(B2BSaleDTO.class))).thenReturn(b2bSaleDTO);

        // When & Then
        mockMvc.perform(post("/api/b2b-sales")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b2bSaleDTO)))
                .andExpect(status().isCreated());

        verify(b2bSaleService, times(1)).createSale(any(B2BSaleDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSale_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSaleService.updateSale(eq(1L), any(B2BSaleDTO.class))).thenReturn(b2bSaleDTO);

        // When & Then
        mockMvc.perform(put("/api/b2b-sales/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b2bSaleDTO)))
                .andExpect(status().isOk());

        verify(b2bSaleService, times(1)).updateSale(eq(1L), any(B2BSaleDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void updateSale_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSaleService.updateSale(eq(1L), any(B2BSaleDTO.class))).thenReturn(b2bSaleDTO);

        // When & Then
        mockMvc.perform(put("/api/b2b-sales/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b2bSaleDTO)))
                .andExpect(status().isOk());

        verify(b2bSaleService, times(1)).updateSale(eq(1L), any(B2BSaleDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSale_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(b2bSaleService).deleteSale(1L);

        // When & Then
        mockMvc.perform(delete("/api/b2b-sales/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(b2bSaleService, times(1)).deleteSale(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteSale_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(b2bSaleService).deleteSale(1L);

        // When & Then
        mockMvc.perform(delete("/api/b2b-sales/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(b2bSaleService, times(1)).deleteSale(1L);
    }
}
