package org.clickenrent.paymentservice.service;

import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.RefundCreateParams;
import org.clickenrent.paymentservice.exception.StripeIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StripeService.
 * Uses MockedStatic for Stripe API so tests run without real API key or network.
 */
@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @InjectMocks
    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripeService, "stripeApiKey", "sk_test_dummy");
        ReflectionTestUtils.setField(stripeService, "webhookSecret", "whsec_test_dummy");
    }

    @Test
    void init_WhenCalled_DoesNotThrow() {
        assertDoesNotThrow(() -> stripeService.init());
    }

    @Test
    void createCustomer_WhenStripeSucceeds_ReturnsCustomerId() throws StripeException {
        Customer mockCustomer = mock(Customer.class);
        when(mockCustomer.getId()).thenReturn("cus_test123");

        try (MockedStatic<Customer> mockCustomerStatic = mockStatic(Customer.class)) {
            mockCustomerStatic.when(() -> Customer.create(any(CustomerCreateParams.class)))
                    .thenReturn(mockCustomer);

            String result = stripeService.createCustomer("user-ext-1", "test@example.com");

            assertEquals("cus_test123", result);
        }
    }

    @Test
    void createCustomer_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<Customer> mockCustomerStatic = mockStatic(Customer.class)) {
            mockCustomerStatic.when(() -> Customer.create(any(CustomerCreateParams.class)))
                    .thenThrow(new InvalidRequestException("Invalid API key", "api_key", "req_1", "api_key_invalid", 401, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.createCustomer("user-ext-1", "test@example.com"));
        }
    }

    @Test
    void createPaymentIntent_WhenStripeSucceeds_ReturnsPaymentIntentId() throws StripeException {
        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getId()).thenReturn("pi_test123");

        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockPi);

            String result = stripeService.createPaymentIntent(
                    new BigDecimal("100.00"), "usd", "cus_test");

            assertEquals("pi_test123", result);
        }
    }

    @Test
    void createPaymentIntent_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(new InvalidRequestException("Card declined", "card", "req_1", "card_declined", 402, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.createPaymentIntent(new BigDecimal("100.00"), "usd", "cus_test"));
        }
    }

    @Test
    void confirmPaymentIntent_WhenSucceeded_ReturnsLatestChargeId() throws StripeException {
        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getStatus()).thenReturn("succeeded");
        when(mockPi.getLatestCharge()).thenReturn("ch_test123");

        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.retrieve(anyString())).thenReturn(mockPi);

            String result = stripeService.confirmPaymentIntent("pi_test123");

            assertEquals("ch_test123", result);
        }
    }

    @Test
    void confirmPaymentIntent_WhenNotSucceeded_ConfirmsAndReturnsChargeId() throws StripeException {
        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getStatus()).thenReturn("requires_confirmation");

        PaymentIntent confirmedPi = mock(PaymentIntent.class);
        when(confirmedPi.getLatestCharge()).thenReturn("ch_after_confirm");

        when(mockPi.confirm(any(PaymentIntentConfirmParams.class))).thenReturn(confirmedPi);

        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.retrieve(anyString())).thenReturn(mockPi);

            String result = stripeService.confirmPaymentIntent("pi_test123");

            assertEquals("ch_after_confirm", result);
        }
    }

    @Test
    void confirmPaymentIntent_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.retrieve(anyString()))
                    .thenThrow(new InvalidRequestException("Not found", "id", "req_1", "resource_missing", 404, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.confirmPaymentIntent("pi_invalid"));
        }
    }

    @Test
    void attachPaymentMethod_WhenStripeSucceeds_ReturnsPaymentMethodId() throws StripeException {
        PaymentMethod mockPm = mock(PaymentMethod.class);

        try (MockedStatic<PaymentMethod> mockPmStatic = mockStatic(PaymentMethod.class)) {
            mockPmStatic.when(() -> PaymentMethod.retrieve(anyString())).thenReturn(mockPm);
            when(mockPm.attach(any(PaymentMethodAttachParams.class))).thenReturn(mockPm);

            String result = stripeService.attachPaymentMethod("pm_test", "cus_test");

            assertEquals("pm_test", result);
        }
    }

    @Test
    void attachPaymentMethod_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<PaymentMethod> mockPmStatic = mockStatic(PaymentMethod.class)) {
            mockPmStatic.when(() -> PaymentMethod.retrieve(anyString()))
                    .thenThrow(new InvalidRequestException("Invalid", "payment_method", "req_1", "invalid_request", 400, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.attachPaymentMethod("pm_invalid", "cus_test"));
        }
    }

    @Test
    void createRefund_WhenStripeSucceeds_ReturnsRefundId() throws StripeException {
        Refund mockRefund = mock(Refund.class);
        when(mockRefund.getId()).thenReturn("re_test123");

        try (MockedStatic<Refund> mockRefundStatic = mockStatic(Refund.class)) {
            mockRefundStatic.when(() -> Refund.create(any(RefundCreateParams.class)))
                    .thenReturn(mockRefund);

            String result = stripeService.createRefund("ch_test", new BigDecimal("50.00"));

            assertEquals("re_test123", result);
        }
    }

    @Test
    void createRefund_FullRefund_WhenStripeSucceeds_ReturnsRefundId() throws StripeException {
        Refund mockRefund = mock(Refund.class);
        when(mockRefund.getId()).thenReturn("re_full123");

        try (MockedStatic<Refund> mockRefundStatic = mockStatic(Refund.class)) {
            mockRefundStatic.when(() -> Refund.create(any(RefundCreateParams.class)))
                    .thenReturn(mockRefund);

            String result = stripeService.createRefund("ch_test", null);

            assertEquals("re_full123", result);
        }
    }

    @Test
    void createRefund_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<Refund> mockRefundStatic = mockStatic(Refund.class)) {
            mockRefundStatic.when(() -> Refund.create(any(RefundCreateParams.class)))
                    .thenThrow(new InvalidRequestException("Charge already refunded", "charge", "req_1", "charge_already_refunded", 400, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.createRefund("ch_test", new BigDecimal("50.00")));
        }
    }

    @Test
    void retrievePaymentIntent_WhenStripeSucceeds_ReturnsPaymentIntent() throws StripeException {
        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getId()).thenReturn("pi_retrieve");

        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.retrieve(anyString())).thenReturn(mockPi);

            PaymentIntent result = stripeService.retrievePaymentIntent("pi_retrieve");

            assertNotNull(result);
            assertEquals("pi_retrieve", result.getId());
        }
    }

    @Test
    void retrievePaymentIntent_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<PaymentIntent> mockPiStatic = mockStatic(PaymentIntent.class)) {
            mockPiStatic.when(() -> PaymentIntent.retrieve(anyString()))
                    .thenThrow(new InvalidRequestException("Not found", "id", "req_1", "resource_missing", 404, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.retrievePaymentIntent("pi_invalid"));
        }
    }

    @Test
    void retrieveCustomer_WhenStripeSucceeds_ReturnsCustomer() throws StripeException {
        Customer mockCustomer = mock(Customer.class);
        when(mockCustomer.getId()).thenReturn("cus_retrieve");

        try (MockedStatic<Customer> mockCustomerStatic = mockStatic(Customer.class)) {
            mockCustomerStatic.when(() -> Customer.retrieve(anyString())).thenReturn(mockCustomer);

            Customer result = stripeService.retrieveCustomer("cus_retrieve");

            assertNotNull(result);
            assertEquals("cus_retrieve", result.getId());
        }
    }

    @Test
    void retrieveCustomer_WhenStripeThrows_ThrowsStripeIntegrationException() {
        try (MockedStatic<Customer> mockCustomerStatic = mockStatic(Customer.class)) {
            mockCustomerStatic.when(() -> Customer.retrieve(anyString()))
                    .thenThrow(new InvalidRequestException("No such customer", "customer", "req_1", "resource_missing", 404, null));

            assertThrows(StripeIntegrationException.class, () ->
                    stripeService.retrieveCustomer("cus_invalid"));
        }
    }
}
