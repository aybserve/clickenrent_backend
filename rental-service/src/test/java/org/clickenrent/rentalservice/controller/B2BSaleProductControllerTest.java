package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSaleProductDTO;
import org.clickenrent.rentalservice.service.B2BSaleProductService;
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

@WebMvcTest(B2BSaleProductController.class)
@AutoConfigureMockMvc
class B2BSaleProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSaleProductService b2bSaleProductService;

    private B2BSaleProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = B2BSaleProductDTO.builder()
                .id(1L)
                .externalId("B2BSP001")
                .b2bSaleId(1L)
                .productId(1L)
                .quantity(10)
                .price(new BigDecimal("250.00"))
                .totalPrice(new BigDecimal("2500.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductsBySale_ReturnsOk() throws Exception {
        List<B2BSaleProductDTO> products = Collections.singletonList(productDTO);
        when(b2bSaleProductService.getProductsBySale(1L)).thenReturn(products);

        mockMvc.perform(get("/api/b2b-sale-products/by-sale/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductById_ReturnsOk() throws Exception {
        when(b2bSaleProductService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/b2b-sale-products/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_ReturnsCreated() throws Exception {
        when(b2bSaleProductService.createProduct(any())).thenReturn(productDTO);

        mockMvc.perform(post("/api/b2b-sale-products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_ReturnsOk() throws Exception {
        when(b2bSaleProductService.updateProduct(eq(1L), any())).thenReturn(productDTO);

        mockMvc.perform(put("/api/b2b-sale-products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSaleProductService).deleteProduct(1L);

        mockMvc.perform(delete("/api/b2b-sale-products/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}







