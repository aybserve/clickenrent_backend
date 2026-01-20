package org.clickenrent.paymentservice.integration;

import com.google.gson.JsonObject;
import org.clickenrent.paymentservice.dto.mobile.MobileBankDTO;
import org.clickenrent.paymentservice.dto.mobile.MobilePaymentMethodDTO;
import org.clickenrent.paymentservice.dto.mobile.MobilePaymentRequestDTO;
import org.clickenrent.paymentservice.dto.mobile.MobilePaymentResponseDTO;
import org.clickenrent.paymentservice.service.MobilePaymentService;
import org.clickenrent.paymentservice.service.MultiSafepayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for top 10 payment methods
 * 
 * These tests require actual MultiSafepay API connection.
 * Enable by setting environment variable: MULTISAFEPAY_TEST_ENABLED=true
 * And provide: MULTISAFEPAY_API_KEY=your_test_key
 * 
 * Run with: 
 * MULTISAFEPAY_TEST_ENABLED=true MULTISAFEPAY_API_KEY=your_key mvn test
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "MULTISAFEPAY_TEST_ENABLED", matches = "true")
class PaymentMethodsIntegrationTest {

    @Autowired
    private MultiSafepayService multiSafepayService;

    @Autowired
    private MobilePaymentService mobilePaymentService;

    private static final String TEST_USER_ID = "test-user-001";
    private static final String TEST_EMAIL = "test@clickenrent.com";

    @BeforeEach
    void setUp() {
        assertNotNull(multiSafepayService);
        assertNotNull(mobilePaymentService);
    }

    @Test
    void testConnectionToMultiSafepay() {
        java.util.Map<String, Object> connectionStatus = multiSafepayService.verifyConnection();
        
        assertNotNull(connectionStatus);
        assertTrue((Boolean) connectionStatus.get("connected"), 
            "Should be connected to MultiSafepay API");
        assertTrue((Boolean) connectionStatus.get("testMode"), 
            "Should be in test mode");
    }

    @Test
    void testGetAvailablePaymentMethods() {
        List<MobilePaymentMethodDTO> methods = mobilePaymentService.getAvailablePaymentMethods();
        
        assertNotNull(methods);
        assertFalse(methods.isEmpty(), "Should return at least some payment methods");
        
        // Verify each method has required fields
        for (MobilePaymentMethodDTO method : methods) {
            assertNotNull(method.getCode(), "Payment method code should not be null");
            assertNotNull(method.getName(), "Payment method name should not be null");
        }
        
        // Check for specific methods we implemented
        assertTrue(methods.stream().anyMatch(m -> "IDEAL".equals(m.getCode())), 
            "Should include iDEAL");
    }

    // ========================================
    // Top 10 Payment Methods Integration Tests
    // ========================================

    @Test
    void testMethod1_IDEAL_DirectPayment() {
        // 1. Get list of banks
        List<MobileBankDTO> banks = mobilePaymentService.getIdealBanks();
        assertNotNull(banks);
        assertFalse(banks.isEmpty(), "Should return iDEAL banks");
        
        // 2. Create payment with first bank
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("25.00"))
            .currency("EUR")
            .paymentMethodCode("IDEAL")
            .issuerId(banks.get(0).getIssuerId())
            .description("Test iDEAL payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-001")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId(), "Order ID should not be null");
        assertNotNull(response.getPaymentUrl(), "Payment URL should not be null");
        assertEquals("direct_minimal_webview", response.getFlowType());
    }

    @Test
    void testMethod2_Bancontact_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("50.00"))
            .currency("EUR")
            .paymentMethodCode("BANCONTACT")
            .description("Test Bancontact payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-002")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod3_CreditCard_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("75.00"))
            .currency("EUR")
            .paymentMethodCode("VISA")
            .cardNumber("4111111111111111")  // Test Visa card
            .cardHolderName("Test User")
            .expiryDate("12/25")
            .cvv("123")
            .description("Test Visa payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-003")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod4_PayPal_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("80.00"))
            .currency("EUR")
            .paymentMethodCode("PAYPAL")
            .description("Test PayPal payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-004")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod5_Giropay_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("30.00"))
            .currency("EUR")
            .paymentMethodCode("GIROPAY")
            .bic("NOLADE22XXX")
            .description("Test Giropay payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-005")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod6_Bizum_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("15.00"))  // Must be between 10-20 for Completed status
            .currency("EUR")
            .paymentMethodCode("BIZUM")
            .phone("+34612345678")
            .description("Test Bizum payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-006")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod7_DirectDebit_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("55.00"))
            .currency("EUR")
            .paymentMethodCode("DIRDEB")
            .accountHolderName("Test User")
            .accountHolderIban("NL87ABNA0000000001")  // Test IBAN for Completed
            .description("Test Direct Debit payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-007")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod8_EPS_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("35.00"))
            .currency("EUR")
            .paymentMethodCode("EPS")
            .bic("RZOOAT2L420")
            .description("Test EPS payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-008")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod9_MBWay_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("15.00"))  // Must be between 10-20 for Completed
            .currency("EUR")
            .paymentMethodCode("MBWAY")
            .phone("+351912345678")
            .description("Test MB WAY payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-009")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    @Test
    void testMethod10_GiftCard_Payment() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("50.00"))
            .currency("EUR")
            .paymentMethodCode("VVVGIFTCARD")
            .cardNumber("111115")  // Test card with €100 balance
            .pin("1234")
            .description("Test Gift Card payment")
            .customerEmail(TEST_EMAIL)
            .rentalExternalId("rental-test-010")
            .build();
        
        MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, TEST_USER_ID);
        
        assertNotNull(response);
        assertNotNull(response.getOrderId());
    }

    // ========================================
    // Error Handling Tests
    // ========================================

    @Test
    void testInvalidPaymentMethod() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("25.00"))
            .currency("EUR")
            .paymentMethodCode("INVALID_METHOD")
            .description("Test invalid method")
            .customerEmail(TEST_EMAIL)
            .build();
        
        assertThrows(Exception.class, () -> 
            mobilePaymentService.createDirectPayment(request, TEST_USER_ID));
    }

    @Test
    void testMissingRequiredField_IDEAL_NoIssuer() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("25.00"))
            .currency("EUR")
            .paymentMethodCode("IDEAL")
            .description("Test without issuer")
            .customerEmail(TEST_EMAIL)
            .build();
        
        assertThrows(IllegalArgumentException.class, () -> 
            mobilePaymentService.createDirectPayment(request, TEST_USER_ID));
    }

    @Test
    void testMissingRequiredField_Bizum_NoPhone() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("15.00"))
            .currency("EUR")
            .paymentMethodCode("BIZUM")
            .description("Test without phone")
            .customerEmail(TEST_EMAIL)
            .build();
        
        assertThrows(IllegalArgumentException.class, () -> 
            mobilePaymentService.createDirectPayment(request, TEST_USER_ID));
    }

    @Test
    void testMissingRequiredField_Card_NoCardNumber() {
        MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
            .amount(new BigDecimal("75.00"))
            .currency("EUR")
            .paymentMethodCode("VISA")
            .cardHolderName("Test User")
            .expiryDate("12/25")
            .cvv("123")
            .description("Test without card number")
            .customerEmail(TEST_EMAIL)
            .build();
        
        assertThrows(IllegalArgumentException.class, () -> 
            mobilePaymentService.createDirectPayment(request, TEST_USER_ID));
    }

    // ========================================
    // Issuer List Tests
    // ========================================

    @Test
    void testGetIdealBanks() {
        List<MobileBankDTO> banks = mobilePaymentService.getIdealBanks();
        
        assertNotNull(banks);
        assertFalse(banks.isEmpty(), "Should return at least some iDEAL banks");
        
        // Verify each bank has required fields
        for (MobileBankDTO bank : banks) {
            assertNotNull(bank.getIssuerId(), "Bank issuer ID should not be null");
            assertNotNull(bank.getName(), "Bank name should not be null");
        }
        
        // Common Dutch banks should be present
        assertTrue(banks.stream().anyMatch(b -> b.getName().contains("ABN")), 
            "Should include ABN AMRO");
    }
}
