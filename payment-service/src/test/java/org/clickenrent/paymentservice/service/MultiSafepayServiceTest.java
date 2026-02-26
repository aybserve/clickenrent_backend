package org.clickenrent.paymentservice.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.clickenrent.paymentservice.client.multisafepay.MultiSafepayClient;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for MultiSafepayService.
 * Uses MockedStatic for MultiSafepayClient so tests run without real API key or network.
 */
@ExtendWith(MockitoExtension.class)
class MultiSafepayServiceTest {

    @InjectMocks
    private MultiSafepayService multiSafepayService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(multiSafepayService, "multiSafepayApiKey", "test_api_key");
        ReflectionTestUtils.setField(multiSafepayService, "testMode", true);
        ReflectionTestUtils.setField(multiSafepayService, "notificationUrl",
                "http://localhost:8080/api/v1/webhooks/multisafepay");
        ReflectionTestUtils.setField(multiSafepayService, "cancelUrl",
                "http://localhost:3000/payment/cancelled");
        ReflectionTestUtils.setField(multiSafepayService, "redirectUrl",
                "http://localhost:3000/payment/success");
    }

    private static JsonObject successOrderResponse() {
        JsonObject data = new JsonObject();
        data.addProperty("order_id", "order_test_123");
        data.addProperty("payment_url", "https://pay.multisafepay.com/test");
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);
        return response;
    }

    private static JsonObject successGatewaysResponse() {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", new JsonArray());
        return response;
    }

    private static JsonObject successIssuersResponse() {
        JsonArray data = new JsonArray();
        JsonObject bank = new JsonObject();
        bank.addProperty("id", "3151");
        bank.addProperty("name", "ABN AMRO");
        data.add(bank);
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);
        return response;
    }

    private static JsonObject successPaymentMethodsResponse() {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", new JsonArray());
        return response;
    }

    @Test
    void createDirectIdealOrder_WhenClientReturnsSuccess_ReturnsResponse() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(successOrderResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.createDirectIdealOrder(
                    new BigDecimal("25.00"),
                    "EUR",
                    "test@example.com",
                    "Test iDEAL payment",
                    "3151");

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
            assertTrue(response.has("data"));
            JsonObject data = response.getAsJsonObject("data");
            assertTrue(data.has("order_id"));
            assertTrue(data.has("payment_url"));
        }
    }

    @Test
    void createBancontactOrder_WhenClientReturnsSuccess_ReturnsResponse() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(successOrderResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.createBancontactOrder(
                    new BigDecimal("50.00"),
                    "EUR",
                    "test@example.com",
                    "Test Bancontact payment");

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
        }
    }

    @Test
    void createBizumOrder_WhenClientReturnsSuccess_ReturnsResponse() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(successOrderResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.createBizumOrder(
                    new BigDecimal("15.00"),
                    "EUR",
                    "test@example.com",
                    "Test Bizum payment",
                    "+34612345678");

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
        }
    }

    @Test
    void createGiropayOrder_WhenClientReturnsSuccess_ReturnsResponse() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(successOrderResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.createGiropayOrder(
                    new BigDecimal("30.00"),
                    "EUR",
                    "test@example.com",
                    "Test Giropay payment",
                    "NOLADE22XXX");

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
        }
    }

    @Test
    void createCreditCardOrder_WhenClientReturnsSuccess_ReturnsResponse() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(successOrderResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.createCreditCardOrder(
                    new BigDecimal("75.00"),
                    "EUR",
                    "test@example.com",
                    "Test card payment",
                    "4111111111111111",
                    "123",
                    "12/25",
                    "Test User");

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
        }
    }

    @Test
    void createPayPalOrder_WhenClientReturnsSuccess_ReturnsResponse() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(successOrderResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.createPayPalOrder(
                    new BigDecimal("80.00"),
                    "EUR",
                    "test@example.com",
                    "Test PayPal payment");

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
        }
    }

    @Test
    void getIdealIssuers_WhenClientReturnsSuccess_ReturnsIssuers() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(MultiSafepayClient::GetIdealIssuers).thenReturn(successIssuersResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.getIdealIssuers();

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
            assertTrue(response.has("data"));
            assertTrue(response.getAsJsonArray("data").size() > 0);
        }
    }

    @Test
    void listPaymentMethods_WhenClientReturnsSuccess_ReturnsMethods() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(MultiSafepayClient::listPaymentMethods).thenReturn(successPaymentMethodsResponse());

            multiSafepayService.init();
            JsonObject response = multiSafepayService.listPaymentMethods();

            assertNotNull(response);
            assertTrue(response.has("success"));
            assertTrue(response.get("success").getAsBoolean());
        }
    }

    @Test
    void verifyConnection_WhenGatewaysReturnSuccess_ReturnsConnectedStatus() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(MultiSafepayClient::GetGateways).thenReturn(successGatewaysResponse());

            multiSafepayService.init();
            Map<String, Object> result = multiSafepayService.verifyConnection();

            assertNotNull(result);
            assertTrue((Boolean) result.get("connected"));
            assertTrue((Boolean) result.get("apiKeyConfigured"));
            assertEquals(true, result.get("testMode"));
        }
    }

    @Test
    void createOrder_WhenAmountIsNull_ThrowsException() {
        assertThrows(Exception.class, () ->
                multiSafepayService.createOrder(null, "EUR", "test@example.com", "Test"));
    }

    @Test
    void createOrder_WhenCurrencyIsNull_ThrowsException() {
        assertThrows(Exception.class, () ->
                multiSafepayService.createOrder(new BigDecimal("25.00"), null, "test@example.com", "Test"));
    }

    @Test
    void createOrderWithResponse_WhenClientReturnsSuccessFalse_ThrowsMultiSafepayIntegrationException() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            JsonObject failureResponse = new JsonObject();
            failureResponse.addProperty("success", false);
            failureResponse.addProperty("error_info", "Invalid request");
            mockClient.when(() -> MultiSafepayClient.createOrder(any())).thenReturn(failureResponse);

            multiSafepayService.init();

            assertThrows(MultiSafepayIntegrationException.class, () ->
                    multiSafepayService.createOrderWithResponse(
                            new BigDecimal("25.00"), "EUR", "test@example.com", "Test"));
        }
    }

    @Test
    void createDirectIdealOrder_WhenClientThrows_ThrowsMultiSafepayIntegrationException() {
        try (MockedStatic<MultiSafepayClient> mockClient = mockStatic(MultiSafepayClient.class)) {
            mockClient.when(() -> MultiSafepayClient.init(anyBoolean(), anyString())).thenAnswer(inv -> null);
            mockClient.when(() -> MultiSafepayClient.createOrder(any()))
                    .thenThrow(new RuntimeException("Network error"));

            multiSafepayService.init();

            assertThrows(MultiSafepayIntegrationException.class, () ->
                    multiSafepayService.createDirectIdealOrder(
                            new BigDecimal("25.00"), "EUR", "test@example.com", "Test", "3151"));
        }
    }

    @Test
    void createCustomer_ReturnsEmailAsCustomerId() {
        String email = "customer@example.com";
        String userExternalId = "user-123";
        String result = multiSafepayService.createCustomer(userExternalId, email);
        assertEquals(email, result);
    }
}
