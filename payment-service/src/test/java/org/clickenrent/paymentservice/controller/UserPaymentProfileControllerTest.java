package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.service.UserPaymentProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.clickenrent.paymentservice.config.SecurityConfig;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPaymentProfileController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class UserPaymentProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPaymentProfileService userPaymentProfileService;

    private UserPaymentProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        profileDTO = UserPaymentProfileDTO.builder()
                .id(1L)
                .userId(1L)
                .stripeCustomerId("cus_test123")
                .isActive(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(userPaymentProfileService.findAll()).thenReturn(Arrays.asList(profileDTO));

        mockMvc.perform(get("/api/user-payment-profiles").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/user-payment-profiles").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(userPaymentProfileService.findById(1L)).thenReturn(profileDTO);

        mockMvc.perform(get("/api/user-payment-profiles/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
