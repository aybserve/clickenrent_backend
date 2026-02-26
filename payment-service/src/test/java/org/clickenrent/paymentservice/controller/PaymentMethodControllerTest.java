package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.PaymentMethodDTO;
import org.clickenrent.paymentservice.service.PaymentMethodService;
import org.clickenrent.paymentservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.clickenrent.paymentservice.config.SecurityConfig;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentMethodController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentMethodService paymentMethodService;

    @MockBean
    private SecurityService securityService;

    private PaymentMethodDTO paymentMethodDTO;

    @BeforeEach
    void setUp() {
        paymentMethodDTO = PaymentMethodDTO.builder()
                .id(1L)
                .code("CREDIT_CARD")
                .name("Credit Card")
                .isActive(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(paymentMethodService.findAll()).thenReturn(Arrays.asList(paymentMethodDTO));

        mockMvc.perform(get("/api/payment-methods").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CREDIT_CARD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActive_ReturnsOk() throws Exception {
        when(paymentMethodService.findActivePaymentMethods()).thenReturn(Arrays.asList(paymentMethodDTO));

        mockMvc.perform(get("/api/payment-methods/active").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(paymentMethodService.findById(1L)).thenReturn(paymentMethodDTO);

        mockMvc.perform(get("/api/payment-methods/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CREDIT_CARD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(paymentMethodService.create(any())).thenReturn(paymentMethodDTO);

        mockMvc.perform(post("/api/payment-methods")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentMethodDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/payment-methods")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentMethodDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(paymentMethodService.update(eq(1L), any())).thenReturn(paymentMethodDTO);

        mockMvc.perform(put("/api/payment-methods/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentMethodDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(paymentMethodService).delete(1L);

        mockMvc.perform(delete("/api/payment-methods/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
