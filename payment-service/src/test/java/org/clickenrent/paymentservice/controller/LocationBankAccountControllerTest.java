package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.LocationBankAccountDTO;
import org.clickenrent.paymentservice.service.LocationBankAccountService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationBankAccountController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class LocationBankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationBankAccountService locationBankAccountService;

    private LocationBankAccountDTO dto;

    @BeforeEach
    void setUp() {
        dto = LocationBankAccountDTO.builder()
                .externalId("loc-bank-ext-1")
                .locationExternalId("location-1")
                .iban("NL91ABNA0417164300")
                .accountHolderName("Test Holder")
                .currency("EUR")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLocationBankAccount_ReturnsCreated() throws Exception {
        when(locationBankAccountService.createLocationBankAccount(any(LocationBankAccountDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/location-bank-accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalId").value("loc-bank-ext-1"))
                .andExpect(jsonPath("$.locationExternalId").value("location-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByLocationExternalId_WhenFound_ReturnsOk() throws Exception {
        when(locationBankAccountService.getByLocationExternalId("location-1")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/location-bank-accounts/location/location-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationExternalId").value("location-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByLocationExternalId_WhenNotFound_Returns404() throws Exception {
        when(locationBankAccountService.getByLocationExternalId("missing")).thenReturn(null);

        mockMvc.perform(get("/api/v1/location-bank-accounts/location/missing").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_WhenFound_ReturnsOk() throws Exception {
        when(locationBankAccountService.getByExternalId("loc-bank-ext-1")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/location-bank-accounts/loc-bank-ext-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("loc-bank-ext-1"));
    }
}
