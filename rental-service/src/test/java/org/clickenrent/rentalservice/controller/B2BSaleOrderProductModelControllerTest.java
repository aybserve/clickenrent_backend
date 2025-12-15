package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSaleOrderProductModelDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderProductModelService;
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

@WebMvcTest(B2BSaleOrderProductModelController.class)
@AutoConfigureMockMvc
class B2BSaleOrderProductModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSaleOrderProductModelService b2bSaleOrderProductModelService;

    private B2BSaleOrderProductModelDTO productModelDTO;

    @BeforeEach
    void setUp() {
        productModelDTO = B2BSaleOrderProductModelDTO.builder()
                .id(1L)
                .b2bSaleOrderId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProductModels_ReturnsOk() throws Exception {
        Page<B2BSaleOrderProductModelDTO> page = new PageImpl<>(Collections.singletonList(productModelDTO));
        when(b2bSaleOrderProductModelService.getAllProductModels(any())).thenReturn(page);

        mockMvc.perform(get("/api/b2b-sale-order-product-models").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductModelsByOrderId_ReturnsOk() throws Exception {
        List<B2BSaleOrderProductModelDTO> productModels = Collections.singletonList(productModelDTO);
        when(b2bSaleOrderProductModelService.getProductModelsByOrderId(1L)).thenReturn(productModels);

        mockMvc.perform(get("/api/b2b-sale-order-product-models/by-order/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductModelById_ReturnsOk() throws Exception {
        when(b2bSaleOrderProductModelService.getProductModelById(1L)).thenReturn(productModelDTO);

        mockMvc.perform(get("/api/b2b-sale-order-product-models/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductModel_ReturnsCreated() throws Exception {
        when(b2bSaleOrderProductModelService.createProductModel(any())).thenReturn(productModelDTO);

        mockMvc.perform(post("/api/b2b-sale-order-product-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productModelDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createProductModel_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/b2b-sale-order-product-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productModelDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductModel_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSaleOrderProductModelService).deleteProductModel(1L);

        mockMvc.perform(delete("/api/b2b-sale-order-product-models/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
