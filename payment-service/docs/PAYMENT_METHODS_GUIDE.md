# Payment Methods Implementation Guide

## Overview

This guide provides implementation examples for all 50+ payment methods supported by the payment-service through MultiSafepay integration.

## Table of Contents

1. [Quick Reference](#quick-reference)
2. [Implementation Examples](#implementation-examples)
3. [Common Patterns](#common-patterns)
4. [Best Practices](#best-practices)

---

## Quick Reference

### Supported Payment Methods

| Method | Code | Type | Direct | Redirect | Special Requirements |
|--------|------|------|--------|----------|---------------------|
| iDEAL | IDEAL | Banking | ✅ | ✅ | Issuer ID (bank selection) |
| iDEAL QR | IDEALQR | Banking | ❌ | ✅ | QR size parameter |
| Bancontact | BANCONTACT | Banking | ✅ | ✅ | Card number (direct) |
| Bancontact QR | BANCONTACTQR | Banking | ❌ | ✅ | QR size parameter |
| Belfius | BELFIUS | Banking | ✅ | ✅ | None |
| Bizum | BIZUM | Banking | ✅ | ✅ | Phone number (Spanish +34) |
| CBC | CBC | Banking | ✅ | ✅ | None |
| KBC | KBC | Banking | ✅ | ✅ | None |
| Direct Debit | DIRDEB | Banking | ✅ | ❌ | IBAN, account holder name |
| Dotpay | DOTPAY | Banking | ✅ | ✅ | Bank selection |
| EPS | EPS | Banking | ✅ | ✅ | BIC code (Austrian banks) |
| Giropay | GIROPAY | Banking | ✅ | ✅ | BIC code (German banks) |
| MB WAY | MBWAY | Banking | ✅ | ✅ | Phone number (Portuguese +351) |
| Multibanco | MULTIBANCO | Banking | ✅ | ✅ | None |
| MyBank | MYBANK | Banking | ✅ | ✅ | Bank selection (Italian) |
| Sofort | SOFORT | Banking | ✅ | ✅ | None |
| Trustly | TRUSTLY | Banking | ✅ | ✅ | None |
| Bank Transfer | BANK_TRANSFER | Banking | ✅ | ✅ | IBAN (optional) |
| Visa | VISA | Card | ✅ | ✅ | Card details |
| Mastercard | MASTERCARD | Card | ✅ | ✅ | Card details |
| Maestro | MAESTRO | Card | ✅ | ✅ | Card details |
| American Express | AMEX | Card | ✅ | ✅ | Card details + 4-digit CVV |
| Dankort | DANKORT | Card | ✅ | ✅ | Card details (Denmark) |
| Cartes Bancaires | CARTEBANCAIRE | Card | ✅ | ✅ | Card details (France) |
| Postepay | POSTEPAY | Card | ✅ | ✅ | Card details (Italy) |
| Billink | BILLINK | BNPL | ✅ | ✅ | Birthday, gender, shopping cart |
| E-Invoicing | EINVOICE | BNPL | ✅ | ✅ | Birthday, bank account, shopping cart |
| iDEAL in3 | IN3 | BNPL | ✅ | ✅ | Birthday, phone, shopping cart |
| Klarna | KLARNA | BNPL | ✅ | ✅ | Birthday, gender, phone, shopping cart |
| Pay After Delivery | PAYAFTER | BNPL | ✅ | ✅ | Birthday, bank account, shopping cart |
| Riverty | AFTERPAY | BNPL | ✅ | ✅ | Birthday, gender, phone, delivery address |
| Edenred | EDENRED | Prepaid | ✅ | ✅ | Card number |
| Gift Cards | Various | Prepaid | ✅ | ✅ | Card number, PIN |
| Monizze | MONIZZE | Prepaid | ✅ | ✅ | Card number, PIN |
| Paysafecard | PAYSAFECARD | Prepaid | ✅ | ✅ | Card number |
| Sodexo | SODEXO | Prepaid | ✅ | ✅ | Card number, PIN |
| Alipay | ALIPAY | Wallet | ✅ | ✅ | None (QR code) |
| Alipay+ | ALIPAYPLUS | Wallet | ✅ | ✅ | None (QR code) |
| Amazon Pay | AMAZONPAY | Wallet | ✅ | ✅ | None (redirect to Amazon) |
| Apple Pay | APPLEPAY | Wallet | ✅ | ✅ | Apple Pay token |
| Google Pay | GOOGLEPAY | Wallet | ✅ | ✅ | Google Pay token |
| PayPal | PAYPAL | Wallet | ✅ | ✅ | None (redirect to PayPal) |
| WeChat Pay | WECHAT | Wallet | ✅ | ✅ | None (QR code) |

---

## Implementation Examples

### Example 1: iDEAL Payment (Direct)

**Use Case**: Customer selects their bank and pays directly.

```java
// 1. Get list of available banks
JsonObject response = multiSafepayService.listPaymentMethods();
List<IssuerDTO> banks = parseIdealIssuers(response);

// 2. Create payment with selected bank
MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
    .amount(new BigDecimal("25.00"))
    .currency("EUR")
    .paymentMethodCode("IDEAL")
    .issuerId("3151") // ABN AMRO
    .description("Bike rental payment")
    .rentalExternalId("rental-001")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);

// 3. Redirect customer to payment URL
// response.getTransactionUrl() -> Customer completes payment at bank
// 4. Receive webhook notification when payment completes
```

### Example 2: Bancontact Payment (Redirect)

**Use Case**: Redirect customer to Bancontact payment page.

```java
MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
    .amount(new BigDecimal("50.00"))
    .currency("EUR")
    .paymentMethodCode("BANCONTACT")
    .description("Bike rental payment")
    .rentalExternalId("rental-002")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createRedirectPayment(request);

// Redirect to response.getPaymentUrl()
```

### Example 3: Bizum Payment (Direct)

**Use Case**: Spanish customers pay with phone number.

```java
// Validate phone number
PhoneValidator.validateForCountry("+34612345678", "ES");

MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
    .amount(new BigDecimal("15.00"))
    .currency("EUR")
    .paymentMethodCode("BIZUM")
    .phone("+34612345678")
    .description("Bike rental payment")
    .rentalExternalId("rental-003")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

### Example 4: Credit Card Payment (Direct)

**Use Case**: Customer enters card details for immediate payment.

```java
// Validate card details
CardNumberValidator.validate("4111111111111111");
CardNumberValidator.validateExpiry(12, 2025);
CardNumberValidator.validateCVV("123", "VISA");

CreditCardPaymentRequestDTO request = CreditCardPaymentRequestDTO.builder()
    .amount(new BigDecimal("75.00"))
    .currency("EUR")
    .paymentMethodCode("VISA")
    .cardNumber("4111111111111111")
    .cardHolderName("John Doe")
    .expiryDate("12/25")
    .cvv("123")
    .description("Bike rental payment")
    .rentalExternalId("rental-004")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

### Example 5: Klarna Payment (BNPL)

**Use Case**: Buy now, pay later with Klarna.

```java
// Build shopping cart
ShoppingCart cart = new ShoppingCart();
ShoppingCartItem item = new ShoppingCartItem();
item.name = "Bike Rental - 1 Day";
item.unit_price = 10000; // €100.00 in cents
item.quantity = 1;
cart.items = new ShoppingCartItem[]{item};

BNPLPaymentRequestDTO request = BNPLPaymentRequestDTO.builder()
    .amount(new BigDecimal("100.00"))
    .currency("EUR")
    .paymentMethodCode("KLARNA")
    .birthday("1990-01-01")
    .gender("male")
    .phone("+31612345678")
    .shoppingCart(cart)
    .description("Bike rental payment")
    .rentalExternalId("rental-005")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

### Example 6: Direct Debit (SEPA)

**Use Case**: Recurring payments via SEPA Direct Debit.

```java
// Validate IBAN
IbanValidator.validate("NL87ABNA0417164300");

DirectDebitPaymentRequestDTO request = DirectDebitPaymentRequestDTO.builder()
    .amount(new BigDecimal("55.00"))
    .currency("EUR")
    .paymentMethodCode("DIRDEB")
    .accountHolderName("John Doe")
    .iban("NL87ABNA0417164300")
    .description("Monthly subscription")
    .rentalExternalId("rental-006")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

### Example 7: Gift Card Payment

**Use Case**: Customer pays with VVV gift card.

```java
GiftCardPaymentRequestDTO request = GiftCardPaymentRequestDTO.builder()
    .amount(new BigDecimal("50.00"))
    .currency("EUR")
    .paymentMethodCode("VVVGIFTCARD")
    .cardNumber("111115")
    .pin("1234")
    .description("Bike rental payment")
    .rentalExternalId("rental-007")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

### Example 8: Apple Pay / Google Pay

**Use Case**: Mobile wallet payment.

```java
// Apple Pay
ApplePayPaymentRequestDTO request = ApplePayPaymentRequestDTO.builder()
    .amount(new BigDecimal("90.00"))
    .currency("EUR")
    .paymentMethodCode("APPLEPAY")
    .applePayToken("encrypted_token_from_apple")
    .description("Bike rental payment")
    .rentalExternalId("rental-008")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

### Example 9: Split Payments (Revenue Sharing)

**Use Case**: Split payment between platform and partner.

```java
// Create splits
List<SplitPaymentDTO> splits = Arrays.asList(
    SplitPaymentDTO.builder()
        .merchantId("1234567") // Partner merchant ID
        .amount(new BigDecimal("20.00")) // 80% to partner
        .description("Partner commission")
        .build(),
    SplitPaymentDTO.builder()
        .merchantId("7654321") // Platform merchant ID
        .amount(new BigDecimal("5.00")) // 20% to platform
        .description("Platform fee")
        .build()
);

MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
    .amount(new BigDecimal("25.00"))
    .currency("EUR")
    .paymentMethodCode("IDEAL")
    .issuerId("3151")
    .splits(splits)
    .description("Bike rental with split")
    .rentalExternalId("rental-009")
    .build();

MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request);
```

---

## Common Patterns

### Pattern 1: Method Selection UI

```java
/**
 * Get available payment methods for customer's location
 */
@GetMapping("/api/v1/payments/methods")
public ResponseEntity<List<MobilePaymentMethodDTO>> getPaymentMethods(
        @RequestParam(required = false) String countryCode,
        @RequestParam(required = false) String currency) {
    
    List<MobilePaymentMethodDTO> methods = mobilePaymentService.getAvailablePaymentMethods();
    
    // Filter by country if provided
    if (countryCode != null) {
        methods = methods.stream()
            .filter(m -> isAvailableInCountry(m, countryCode))
            .collect(Collectors.toList());
    }
    
    // Filter by currency if provided
    if (currency != null) {
        methods = methods.stream()
            .filter(m -> supportsCurrency(m, currency))
            .collect(Collectors.toList());
    }
    
    return ResponseEntity.ok(methods);
}
```

### Pattern 2: Amount Validation

```java
/**
 * Validate payment amount against method requirements
 */
public void validateAmount(String methodCode, BigDecimal amount) {
    // Check minimum amounts
    BigDecimal minAmount = getMinAmountForMethod(methodCode);
    if (amount.compareTo(minAmount) < 0) {
        throw new PaymentAmountException(amount, minAmount, methodCode);
    }
    
    // Check maximum amounts (if applicable)
    BigDecimal maxAmount = getMaxAmountForMethod(methodCode);
    if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
        throw new PaymentAmountException(amount, minAmount, maxAmount, methodCode);
    }
}
```

### Pattern 3: Webhook Handling

```java
/**
 * Handle MultiSafepay webhook notifications
 */
@PostMapping("/api/v1/webhooks/multisafepay")
public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
    try {
        JsonObject webhook = JsonParser.parseString(payload).getAsJsonObject();
        
        String orderId = webhook.get("order_id").getAsString();
        String status = webhook.get("status").getAsString();
        
        // Update transaction status
        updateTransactionStatus(orderId, status);
        
        // Notify customer if needed
        if ("completed".equals(status)) {
            notifyCustomer(orderId);
        }
        
        return ResponseEntity.ok("OK");
    } catch (Exception e) {
        log.error("Webhook processing failed", e);
        return ResponseEntity.status(500).body("Error");
    }
}
```

### Pattern 4: Error Handling

```java
/**
 * Handle payment method-specific errors
 */
public MobilePaymentResponseDTO handlePaymentError(Exception e, String methodCode) {
    if (e instanceof InvalidIbanException) {
        return MobilePaymentResponseDTO.builder()
            .success(false)
            .errorCode("INVALID_IBAN")
            .errorMessage("Invalid IBAN format. Please check and try again.")
            .build();
    }
    
    if (e instanceof InvalidPhoneNumberException) {
        return MobilePaymentResponseDTO.builder()
            .success(false)
            .errorCode("INVALID_PHONE")
            .errorMessage("Invalid phone number for " + methodCode)
            .build();
    }
    
    if (e instanceof PaymentAmountException pae) {
        return MobilePaymentResponseDTO.builder()
            .success(false)
            .errorCode("AMOUNT_OUT_OF_RANGE")
            .errorMessage(String.format("Amount must be between %s and %s", 
                pae.getMinAmount(), pae.getMaxAmount()))
            .build();
    }
    
    // Generic error
    return MobilePaymentResponseDTO.builder()
        .success(false)
        .errorCode("PAYMENT_FAILED")
        .errorMessage("Payment failed. Please try again.")
        .build();
}
```

---

## Best Practices

### 1. Always Validate Input

```java
// Before creating payment
if ("BIZUM".equals(methodCode)) {
    PhoneValidator.validateForCountry(phone, "ES");
}

if ("EPS".equals(methodCode)) {
    BicValidator.validate(bic);
}

if ("DIRDEB".equals(methodCode)) {
    IbanValidator.validate(iban);
}

if (isCardMethod(methodCode)) {
    CardNumberValidator.validate(cardNumber);
    CardNumberValidator.validateExpiry(month, year);
    CardNumberValidator.validateCVV(cvv, cardType);
}
```

### 2. Check Method Availability

```java
@Before
public void checkMethodEnabled(String methodCode) {
    boolean enabled = configurationService.isPaymentMethodEnabled(methodCode);
    if (!enabled) {
        throw new PaymentMethodDisabledException(methodCode);
    }
}
```

### 3. Use Appropriate Order Types

- **Direct**: Customer stays on your platform, enters details
- **Redirect**: Customer redirected to payment provider
- **Checkout**: Fast checkout with shopping cart

```java
public String getOrderType(String methodCode) {
    return switch (methodCode) {
        case "IDEAL", "BANCONTACT", "CREDITCARD" -> "direct";
        case "PAYPAL", "ALIPAY", "AMAZONPAY" -> "redirect";
        case "KLARNA", "AFTERPAY" -> "direct"; // BNPL needs customer info
        default -> "redirect"; // Safe default
    };
}
```

### 4. Handle Async Payment Status

```java
/**
 * Poll for payment status (for methods without webhooks)
 */
@Scheduled(fixedDelay = 30000) // Every 30 seconds
public void pollPendingPayments() {
    List<FinancialTransaction> pending = transactionRepository
        .findByStatusAndCreatedAtBefore(
            "INITIALIZED", 
            LocalDateTime.now().minusMinutes(5)
        );
    
    for (FinancialTransaction tx : pending) {
        String status = multiSafepayService.getOrderStatus(tx.getExternalId());
        if (!"initialized".equals(status)) {
            updateTransactionStatus(tx, status);
        }
    }
}
```

### 5. Implement Idempotency

```java
/**
 * Ensure duplicate payments aren't created
 */
public MobilePaymentResponseDTO createPayment(MobilePaymentRequestDTO request) {
    // Check if payment already exists for this rental
    Optional<FinancialTransaction> existing = transactionRepository
        .findByRentalExternalIdAndStatus(
            request.getRentalExternalId(), 
            "COMPLETED"
        );
    
    if (existing.isPresent()) {
        throw new DuplicateResourceException("Payment already exists for this rental");
    }
    
    // Create new payment
    return processPayment(request);
}
```

### 6. Log All Payment Attempts

```java
@Around("@annotation(Audited)")
public Object auditPayment(ProceedingJoinPoint pjp) throws Throwable {
    MobilePaymentRequestDTO request = (MobilePaymentRequestDTO) pjp.getArgs()[0];
    
    log.info("Payment attempt: method={}, amount={}, rental={}", 
        request.getPaymentMethodCode(),
        request.getAmount(),
        request.getRentalExternalId()
    );
    
    try {
        Object result = pjp.proceed();
        log.info("Payment successful: rental={}", request.getRentalExternalId());
        return result;
    } catch (Exception e) {
        log.error("Payment failed: rental={}, error={}", 
            request.getRentalExternalId(), 
            e.getMessage()
        );
        throw e;
    }
}
```

### 7. Test in Test Mode First

```properties
# Always test in test mode first
multisafepay.test.mode=true

# Use test API key
multisafepay.api.key=${MULTISAFEPAY_TEST_API_KEY}

# Enable test logging
payment.testing.enabled=true
payment.testing.log-requests=true
```

### 8. Monitor Payment Success Rates

```java
@Scheduled(cron = "0 0 * * * *") // Every hour
public void monitorPaymentRates() {
    LocalDateTime hourAgo = LocalDateTime.now().minusHours(1);
    
    long total = transactionRepository.countByCreatedAtAfter(hourAgo);
    long successful = transactionRepository
        .countByStatusAndCreatedAtAfter("COMPLETED", hourAgo);
    
    double successRate = (double) successful / total * 100;
    
    if (successRate < 90) {
        alertService.send("Payment success rate below 90%: " + successRate + "%");
    }
    
    metricsService.recordSuccessRate(successRate);
}
```

---

## Configuration Reference

### Environment Variables

```bash
# MultiSafepay Configuration
MULTISAFEPAY_API_KEY=your_api_key_here
MULTISAFEPAY_TEST_MODE=true
MULTISAFEPAY_NOTIFICATION_URL=https://yourdomain.com/api/v1/webhooks/multisafepay

# Payment Method Toggles
PAYMENT_METHOD_IDEAL_ENABLED=true
PAYMENT_METHOD_BANCONTACT_ENABLED=true
PAYMENT_METHOD_KLARNA_ENABLED=true
# ... (see application.properties for full list)

# Amount Limits
PAYMENT_METHOD_BIZUM_MIN_AMOUNT=10.00
PAYMENT_METHOD_KLARNA_MIN_AMOUNT=5.00
```

---

## Troubleshooting

### Issue: Payment Method Not Appearing

**Solution**:
1. Check method is enabled in `application.properties`
2. Verify method is in database: `SELECT * FROM payment_methods WHERE code = 'IDEAL'`
3. Check MultiSafepay account has method activated

### Issue: Webhook Not Received

**Solution**:
1. Verify `multisafepay.notification.url` is publicly accessible
2. Use ngrok for local testing: `ngrok http 8080`
3. Check webhook URL in MultiSafepay dashboard
4. Test manually: `curl -X POST http://localhost:8080/api/v1/webhooks/multisafepay`

### Issue: 3D Secure Failures

**Solution**:
1. Ensure customer object includes email and phone
2. Check card supports 3DS (test cards do)
3. Verify redirect URLs are configured
4. Test with different browsers

---

## Additional Resources

- [MultiSafepay API Documentation](https://docs.multisafepay.com)
- [Payment Methods Testing Guide](PAYMENT_METHODS_TESTING.md)
- [MultiSafepay Payout System](MULTISAFEPAY_PAYOUT_SYSTEM.md)

---

**Last Updated**: January 21, 2026  
**Version**: 1.0.0  
**Status**: Complete
