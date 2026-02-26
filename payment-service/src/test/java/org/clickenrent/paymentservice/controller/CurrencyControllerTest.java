package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.CurrencyDTO;
import org.clickenrent.paymentservice.service.CurrencyService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class CurrencyControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    private CurrencyDTO currencyDTO;

    @BeforeEach
    void setUp() {
        currencyDTO = CurrencyDTO.builder()
                .id(1L)
                .externalId(UUID.randomUUID().toString())
                .code("USD")
                .name("US Dollar")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCurrencies_ReturnsOk() throws Exception {
        when(currencyService.findAll()).thenReturn(Arrays.asList(currencyDTO));

        mockMvc.perform(get("/api/v1/currencies").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCurrencyById_ReturnsOk() throws Exception {
        when(currencyService.findById(1L)).thenReturn(currencyDTO);

        mockMvc.perform(get("/api/v1/currencies/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCurrency_ReturnsCreated() throws Exception {
        when(currencyService.create(any())).thenReturn(currencyDTO);

        mockMvc.perform(post("/api/v1/currencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currencyDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCurrency_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/currencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currencyDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCurrency_ReturnsOk() throws Exception {
        when(currencyService.update(eq(1L), any())).thenReturn(currencyDTO);

        mockMvc.perform(put("/api/v1/currencies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currencyDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCurrency_ReturnsNoContent() throws Exception {
        doNothing().when(currencyService).delete(1L);

        mockMvc.perform(delete("/api/v1/currencies/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
