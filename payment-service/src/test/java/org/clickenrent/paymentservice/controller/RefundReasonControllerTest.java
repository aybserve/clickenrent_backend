package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.RefundReasonDTO;
import org.clickenrent.paymentservice.service.RefundReasonService;
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

@WebMvcTest(RefundReasonController.class)
class RefundReasonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefundReasonService refundReasonService;

    private RefundReasonDTO refundReasonDTO;

    @BeforeEach
    void setUp() {
        refundReasonDTO = RefundReasonDTO.builder()
                .id(1L)
                .externalId("ext-1")
                .code("CUSTOMER_REQUEST")
                .name("Customer Request")
                .build();
    }

    @Test
    void getAll_ReturnsOk() throws Exception {
        when(refundReasonService.findAll()).thenReturn(Arrays.asList(refundReasonDTO));

        mockMvc.perform(get("/api/v1/refund-reasons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CUSTOMER_REQUEST"));
    }

    @Test
    void getById_ReturnsOk() throws Exception {
        when(refundReasonService.findById(1L)).thenReturn(refundReasonDTO);

        mockMvc.perform(get("/api/v1/refund-reasons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CUSTOMER_REQUEST"));
    }

    @Test
    void getByCode_ReturnsOk() throws Exception {
        when(refundReasonService.findByCode("CUSTOMER_REQUEST")).thenReturn(refundReasonDTO);

        mockMvc.perform(get("/api/v1/refund-reasons/code/CUSTOMER_REQUEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CUSTOMER_REQUEST"));
    }
}
