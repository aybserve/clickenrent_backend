package org.clickenrent.paymentservice.controller;

import com.stripe.model.Event;
import org.clickenrent.paymentservice.repository.FinancialTransactionRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.clickenrent.paymentservice.service.StripeService;
import org.clickenrent.paymentservice.service.SecurityService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StripeWebhookController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g="
})
class StripeWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StripeService stripeService;

    @MockBean
    private FinancialTransactionRepository financialTransactionRepository;

    @MockBean
    private PaymentStatusRepository paymentStatusRepository;

    @MockBean
    private SecurityService securityService;

    @Test
    void handleWebhook_ReturnsOk() throws Exception {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getType()).thenReturn("payment_intent.succeeded");
        when(stripeService.handleWebhookEvent(anyString(), anyString())).thenReturn(mockEvent);

        mockMvc.perform(post("/api/webhooks/stripe")
                        .header("Stripe-Signature", "test_signature")
                        .content("{\"id\": \"evt_test\", \"type\": \"payment_intent.succeeded\"}"))
                .andExpect(status().isOk());
    }
}
