package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderItemDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionOrderItemService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BSubscriptionOrderItemController.class)
@AutoConfigureMockMvc
class B2BSubscriptionOrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSubscriptionOrderItemService b2bSubscriptionOrderItemService;

    private B2BSubscriptionOrderItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = B2BSubscriptionOrderItemDTO.builder()
                .id(1L)
                .externalId("BSOI001")
                .b2bSubscriptionOrderId(1L)
                .productModelType("BikeModel")
                .productModelId(1L)
                .quantity(10)
                .price(new BigDecimal("100.00"))
                .totalPrice(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllItems_ReturnsOk() throws Exception {
        Page<B2BSubscriptionOrderItemDTO> page = new PageImpl<>(Collections.singletonList(itemDTO));
        when(b2bSubscriptionOrderItemService.getAllItems(any())).thenReturn(page);

        mockMvc.perform(get("/api/b2b-subscription-order-items").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemsByOrderId_ReturnsOk() throws Exception {
        List<B2BSubscriptionOrderItemDTO> items = Collections.singletonList(itemDTO);
        when(b2bSubscriptionOrderItemService.getItemsByOrderId(1L)).thenReturn(items);

        mockMvc.perform(get("/api/b2b-subscription-order-items/by-order/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemById_ReturnsOk() throws Exception {
        when(b2bSubscriptionOrderItemService.getItemById(1L)).thenReturn(itemDTO);

        mockMvc.perform(get("/api/b2b-subscription-order-items/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_ReturnsCreated() throws Exception {
        when(b2bSubscriptionOrderItemService.createItem(any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/b2b-subscription-order-items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_ReturnsOk() throws Exception {
        when(b2bSubscriptionOrderItemService.updateItem(eq(1L), any())).thenReturn(itemDTO);

        mockMvc.perform(put("/api/b2b-subscription-order-items/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSubscriptionOrderItemService).deleteItem(1L);

        mockMvc.perform(delete("/api/b2b-subscription-order-items/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}


