package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.StockMovementDTO;
import org.clickenrent.rentalservice.service.StockMovementService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockMovementController.class)
@AutoConfigureMockMvc
class StockMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockMovementService stockMovementService;

    private StockMovementDTO movementDTO;

    @BeforeEach
    void setUp() {
        movementDTO = StockMovementDTO.builder()
                .id(1L)
                .externalId("SM001")
                .productId(1L)
                .fromHubId(1L)
                .toHubId(2L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllStockMovements_ReturnsOk() throws Exception {
        Page<StockMovementDTO> page = new PageImpl<>(Collections.singletonList(movementDTO));
        when(stockMovementService.getAllStockMovements(any())).thenReturn(page);

        mockMvc.perform(get("/api/stock-movements").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStockMovementsByProduct_ReturnsOk() throws Exception {
        List<StockMovementDTO> movements = Collections.singletonList(movementDTO);
        when(stockMovementService.getStockMovementsByProduct(1L)).thenReturn(movements);

        mockMvc.perform(get("/api/stock-movements/by-product/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStockMovementById_ReturnsOk() throws Exception {
        when(stockMovementService.getStockMovementById(1L)).thenReturn(movementDTO);

        mockMvc.perform(get("/api/stock-movements/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("SM001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStockMovement_ReturnsCreated() throws Exception {
        when(stockMovementService.createStockMovement(any())).thenReturn(movementDTO);

        mockMvc.perform(post("/api/stock-movements")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movementDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStockMovement_ReturnsNoContent() throws Exception {
        doNothing().when(stockMovementService).deleteStockMovement(1L);

        mockMvc.perform(delete("/api/stock-movements/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








