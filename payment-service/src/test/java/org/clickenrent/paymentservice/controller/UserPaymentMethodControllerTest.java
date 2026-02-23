package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.UserPaymentMethodDTO;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.service.UserPaymentMethodService;
import org.clickenrent.paymentservice.service.SecurityService;
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

@WebMvcTest(UserPaymentMethodController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class UserPaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPaymentMethodService userPaymentMethodService;

    @MockBean
    private SecurityService securityService;

    private UserPaymentMethodDTO methodDTO;

    @BeforeEach
    void setUp() {
        methodDTO = UserPaymentMethodDTO.builder()
                .id(1L)
                .userPaymentProfile(UserPaymentProfileDTO.builder().id(1L).build())
                .stripePaymentMethodId("pm_test123")
                .isDefault(false)
                .isActive(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(userPaymentMethodService.findAll()).thenReturn(Arrays.asList(methodDTO));

        mockMvc.perform(get("/api/user-payment-methods").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/user-payment-methods").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
