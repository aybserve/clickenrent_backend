package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionOrderService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BSubscriptionOrderController.class)
@AutoConfigureMockMvc
class B2BSubscriptionOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSubscriptionOrderService b2bSubscriptionOrderService;

    private B2BSubscriptionOrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = B2BSubscriptionOrderDTO.builder()
                .id(1L)
                .externalId("B2BSORD001")
                .locationId(1L)
                .dateTime(LocalDateTime.now())
                .b2bSubscriptionOrderStatusId(2L)
                .b2bSubscriptionId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_ReturnsOk() throws Exception {
        Page<B2BSubscriptionOrderDTO> page = new PageImpl<>(Collections.singletonList(orderDTO));
        when(b2bSubscriptionOrderService.getAllOrders(any())).thenReturn(page);

        mockMvc.perform(get("/api/b2b-subscription-orders").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById_ReturnsOk() throws Exception {
        when(b2bSubscriptionOrderService.getOrderById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/b2b-subscription-orders/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_ReturnsCreated() throws Exception {
        when(b2bSubscriptionOrderService.createOrder(any())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/b2b-subscription-orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrder_ReturnsOk() throws Exception {
        when(b2bSubscriptionOrderService.updateOrder(eq(1L), any())).thenReturn(orderDTO);

        mockMvc.perform(put("/api/b2b-subscription-orders/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSubscriptionOrderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/b2b-subscription-orders/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}







