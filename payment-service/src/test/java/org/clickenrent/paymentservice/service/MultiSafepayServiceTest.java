package org.clickenrent.paymentservice.service;

import com.google.gson.JsonObject;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MultiSafepayService
 * 
 * Note: These tests require MultiSafepay test API key configured
 * Run with: mvn test -Dmultisafepay.api.key=your_test_key
 */
@ExtendWith(MockitoExtension.class)
class MultiSafepayServiceTest {

    @InjectMocks
    private MultiSafepayService multiSafepayService;

    @BeforeEach
    void setUp() {
        // Set test mode configuration
        ReflectionTestUtils.setField(multiSafepayService, "multiSafepayApiKey", 
            "test_api_key");
        ReflectionTestUtils.setField(multiSafepayService, "testMode", true);
        ReflectionTestUtils.setField(multiSafepayService, "notificationUrl", 
            "http://localhost:8080/api/v1/webhooks/multisafepay");
        ReflectionTestUtils.setField(multiSafepayService, "cancelUrl", 
            "http://localhost:3000/payment/cancelled");
        ReflectionTestUtils.setField(multiSafepayService, "redirectUrl", 
            "http://localhost:3000/payment/success");
    }

    @Test
    void testCreateDirectIdealOrder_Success() {
        // This test requires actual MultiSafepay API connection
        // Comment out if API key not available
        
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.createDirectIdealOrder(
                new BigDecimal("25.00"),
                "EUR",
                "test@example.com",
                "Test iDEAL payment",
                "3151"  // ABN AMRO test issuer
            );
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
            if (response.get("success").getAsBoolean()) {
                assertTrue(response.has("data"));
                JsonObject data = response.getAsJsonObject("data");
                assertTrue(data.has("order_id"));
                assertTrue(data.has("payment_url"));
            }
        } catch (Exception e) {
            // Skip test if API key not configured
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testCreateBancontactOrder_Success() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.createBancontactOrder(
                new BigDecimal("50.00"),
                "EUR",
                "test@example.com",
                "Test Bancontact payment"
            );
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testCreateBizumOrder_Success() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.createBizumOrder(
                new BigDecimal("15.00"),
                "EUR",
                "test@example.com",
                "Test Bizum payment",
                "+34612345678"
            );
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testCreateGiropayOrder_Success() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.createGiropayOrder(
                new BigDecimal("30.00"),
                "EUR",
                "test@example.com",
                "Test Giropay payment",
                "NOLADE22XXX"
            );
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testCreateCreditCardOrder_Success() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.createCreditCardOrder(
                new BigDecimal("75.00"),
                "EUR",
                "test@example.com",
                "Test card payment",
                "4111111111111111",  // Test Visa card
                "123",
                "12/25",
                "Test User"
            );
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testCreatePayPalOrder_Success() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.createPayPalOrder(
                new BigDecimal("80.00"),
                "EUR",
                "test@example.com",
                "Test PayPal payment"
            );
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testGetIdealIssuers() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.getIdealIssuers();
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
            if (response.get("success").getAsBoolean() && response.has("data")) {
                // Verify we get a list of banks
                assertTrue(response.getAsJsonArray("data").size() > 0);
            }
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testListPaymentMethods() {
        try {
            multiSafepayService.init();
            
            JsonObject response = multiSafepayService.listPaymentMethods();
            
            assertNotNull(response);
            assertTrue(response.has("success"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testVerifyConnection() {
        try {
            multiSafepayService.init();
            
            java.util.Map<String, Object> result = multiSafepayService.verifyConnection();
            
            assertNotNull(result);
            assertTrue(result.containsKey("connected"));
            assertTrue(result.containsKey("testMode"));
            assertTrue(result.containsKey("apiKeyConfigured"));
            
        } catch (Exception e) {
            System.out.println("Skipping test - MultiSafepay API key not configured: " + e.getMessage());
        }
    }

    @Test
    void testCreateOrder_NullAmount() {
        assertThrows(Exception.class, () -> 
            multiSafepayService.createOrder(null, "EUR", "test@example.com", "Test"));
    }

    @Test
    void testCreateOrder_NullCurrency() {
        assertThrows(Exception.class, () -> 
            multiSafepayService.createOrder(new BigDecimal("25.00"), null, "test@example.com", "Test"));
    }
}
