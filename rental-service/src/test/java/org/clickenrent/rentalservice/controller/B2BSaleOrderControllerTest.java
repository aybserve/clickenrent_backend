package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSaleOrderDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderService;
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

@WebMvcTest(B2BSaleOrderController.class)
@AutoConfigureMockMvc
class B2BSaleOrderControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSaleOrderService b2bSaleOrderService;

    private B2BSaleOrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = B2BSaleOrderDTO.builder()
                .id(1L)
                .externalId("B2BSO001")
                .sellerCompanyExternalId("company-ext-001")
                .buyerCompanyExternalId("company-ext-002")
                .b2bSaleOrderStatusId(2L)
                .locationId(1L)
                .b2bSaleId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_WithAdminRole_ReturnsOk() throws Exception {
        Page<B2BSaleOrderDTO> page = new PageImpl<>(Collections.singletonList(orderDTO));
        when(b2bSaleOrderService.getAllOrders(any())).thenReturn(page);

        mockMvc.perform(get("/api/b2b-sale-orders").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(b2bSaleOrderService, times(1)).getAllOrders(any());
    }
    @Test
    @WithMockUser(roles = "B2B")
    void getOrdersBySellerCompany_ReturnsOk() throws Exception {
        when(b2bSaleOrderService.getOrdersBySellerCompanyExternalId("company-ext-001")).thenReturn(Arrays.asList(orderDTO));

        mockMvc.perform(get("/api/b2b-sale-orders/by-seller/1").with(csrf()))
                .andExpect(status().isOk());

        verify(b2bSaleOrderService, times(1)).getOrdersBySellerCompanyExternalId("company-ext-001");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById_ReturnsOk() throws Exception {
        when(b2bSaleOrderService.getOrderById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/b2b-sale-orders/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_ReturnsCreated() throws Exception {
        when(b2bSaleOrderService.createOrder(any())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/b2b-sale-orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrder_ReturnsOk() throws Exception {
        when(b2bSaleOrderService.updateOrder(eq(1L), any())).thenReturn(orderDTO);

        mockMvc.perform(put("/api/b2b-sale-orders/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSaleOrderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/b2b-sale-orders/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




