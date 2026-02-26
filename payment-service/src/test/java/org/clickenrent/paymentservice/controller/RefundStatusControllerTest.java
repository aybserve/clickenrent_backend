package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.RefundStatusDTO;
import org.clickenrent.paymentservice.service.RefundStatusService;
import org.clickenrent.paymentservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RefundStatusController.class)
@org.springframework.context.annotation.Import(org.clickenrent.paymentservice.config.SecurityConfig.class)
@org.springframework.test.context.TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class RefundStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefundStatusService refundStatusService;

    @MockBean
    private SecurityService securityService;

    private RefundStatusDTO refundStatusDTO;

    @BeforeEach
    void setUp() {
        refundStatusDTO = RefundStatusDTO.builder()
                .id(1L)
                .externalId("ext-1")
                .code("PROCESSING")
                .name("Processing")
                .build();
    }

    @Test
    void getAll_ReturnsOk() throws Exception {
        when(refundStatusService.findAll()).thenReturn(Arrays.asList(refundStatusDTO));

        mockMvc.perform(get("/api/v1/refund-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("PROCESSING"));
    }

    @Test
    void getById_ReturnsOk() throws Exception {
        when(refundStatusService.findById(1L)).thenReturn(refundStatusDTO);

        mockMvc.perform(get("/api/v1/refund-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROCESSING"));
    }

    @Test
    void getByCode_ReturnsOk() throws Exception {
        when(refundStatusService.findByCode("PROCESSING")).thenReturn(refundStatusDTO);

        mockMvc.perform(get("/api/v1/refund-statuses/code/PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROCESSING"));
    }
}
