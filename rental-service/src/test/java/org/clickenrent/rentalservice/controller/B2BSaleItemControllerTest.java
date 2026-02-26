package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSaleItemDTO;
import org.clickenrent.rentalservice.service.B2BSaleItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BSaleItemController.class)
@AutoConfigureMockMvc
class B2BSaleItemControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSaleItemService b2bSaleItemService;

    private B2BSaleItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = B2BSaleItemDTO.builder()
                .id(1L)
                .externalId("B2BSI001")
                .b2bSaleId(1L)
                .productId(1L)
                .quantity(10)
                .price(new BigDecimal("250.00"))
                .totalPrice(new BigDecimal("2500.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemsBySale_ReturnsOk() throws Exception {
        List<B2BSaleItemDTO> items = Collections.singletonList(itemDTO);
        when(b2bSaleItemService.getItemsBySale(1L)).thenReturn(items);

        mockMvc.perform(get("/api/b2b-sale-items/by-sale/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemById_ReturnsOk() throws Exception {
        when(b2bSaleItemService.getItemById(1L)).thenReturn(itemDTO);

        mockMvc.perform(get("/api/b2b-sale-items/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_ReturnsCreated() throws Exception {
        when(b2bSaleItemService.createItem(any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/b2b-sale-items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_ReturnsOk() throws Exception {
        when(b2bSaleItemService.updateItem(eq(1L), any())).thenReturn(itemDTO);

        mockMvc.perform(put("/api/b2b-sale-items/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSaleItemService).deleteItem(1L);

        mockMvc.perform(delete("/api/b2b-sale-items/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

