package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.mobile.MobileBankDTO;
import org.clickenrent.paymentservice.dto.mobile.MobilePaymentMethodDTO;
import org.clickenrent.paymentservice.service.MobilePaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MobilePaymentController.class)
class MobilePaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MobilePaymentService mobilePaymentService;

    private MobilePaymentMethodDTO methodDTO;
    private MobileBankDTO bankDTO;

    @BeforeEach
    void setUp() {
        methodDTO = MobilePaymentMethodDTO.builder()
                .code("IDEAL")
                .name("iDEAL")
                .build();
        bankDTO = MobileBankDTO.builder()
                .issuerId("3151")
                .name("ABN AMRO")
                .build();
    }

    @Test
    void getPaymentMethods_ReturnsOk() throws Exception {
        when(mobilePaymentService.getAvailablePaymentMethods()).thenReturn(Arrays.asList(methodDTO));

        mockMvc.perform(get("/api/v1/payments/mobile/methods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("IDEAL"));
    }

    @Test
    void getIdealBanks_ReturnsOk() throws Exception {
        when(mobilePaymentService.getIdealBanks()).thenReturn(Arrays.asList(bankDTO));

        mockMvc.perform(get("/api/v1/payments/mobile/ideal/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].issuerId").value("3151"))
                .andExpect(jsonPath("$[0].name").value("ABN AMRO"));
    }
}
