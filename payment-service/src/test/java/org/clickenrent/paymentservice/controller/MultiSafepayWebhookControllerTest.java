package org.clickenrent.paymentservice.controller;

import com.google.gson.JsonObject;
import org.clickenrent.paymentservice.repository.FinancialTransactionRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.clickenrent.paymentservice.service.MultiSafepayService;
import org.clickenrent.paymentservice.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MultiSafepayWebhookController.class)
@org.springframework.context.annotation.Import(org.clickenrent.paymentservice.config.SecurityConfig.class)
@org.springframework.test.context.TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class MultiSafepayWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MultiSafepayService multiSafepayService;

    @MockBean
    private FinancialTransactionRepository financialTransactionRepository;

    @MockBean
    private PaymentStatusRepository paymentStatusRepository;

    @MockBean
    private SecurityService securityService;

    private static JsonObject successOrderResponse(String orderId, String status) {
        JsonObject data = new JsonObject();
        data.addProperty("order_id", orderId);
        data.addProperty("status", status);
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);
        return response;
    }

    @Test
    void handleWebhookPost_WhenTransactionIdProvided_ReturnsOk() throws Exception {
        when(multiSafepayService.getOrder(anyString())).thenReturn(successOrderResponse("order_123", "completed"));

        mockMvc.perform(post("/api/v1/webhooks/multisafepay")
                        .param("transactionid", "order_123")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void handleWebhookPost_WhenNoTransactionId_ReturnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/webhooks/multisafepay")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void handleWebhookGet_WhenTransactionIdProvided_ReturnsOk() throws Exception {
        when(multiSafepayService.getOrder(anyString())).thenReturn(successOrderResponse("order_456", "initialized"));

        mockMvc.perform(get("/api/v1/webhooks/multisafepay").param("transactionid", "order_456"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
