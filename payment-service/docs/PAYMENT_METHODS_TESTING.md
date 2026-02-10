# Payment Methods Testing Guide

## Overview

This guide provides comprehensive testing instructions for all 50+ payment methods supported by the payment-service through MultiSafepay. Based on the official [MultiSafepay Testing Documentation](https://docs.multisafepay.com/docs/testing).

## Prerequisites

- **Test Environment**: Payment service must be configured with `multisafepay.test.mode=true`
- **Test API Key**: Use your MultiSafepay test account API key
- **Test Endpoint**: Base URL `http://localhost:8080/api/v1/payments/mobile/test`

## Table of Contents

1. [Banking Methods](#banking-methods)
2. [Card Methods](#card-methods)
3. [BNPL Methods](#bnpl-methods)
4. [Prepaid Cards](#prepaid-cards)
5. [Wallets](#wallets)

---

## Banking Methods

### iDEAL

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Scenarios**:

| Scenario | Action | Expected Result |
|----------|--------|-----------------|
| Success | Select "Success" on test platform | Transaction status: Completed |
| Failure | Select "Failure" on test platform | Transaction status: Declined |
| Cancelled | Select "Cancelled" on test platform | Transaction status: Void |
| Expired | Select "Expired" on test platform | Transaction status: Expired |

**Example Request**:
```json
{
  "amount": 25.00,
  "currency": "EUR",
  "paymentMethodCode": "IDEAL",
  "issuerId": "3151",
  "description": "Test iDEAL payment",
  "rentalExternalId": "rental-test-001"
}
```

**Available Test Banks (Issuer IDs)**:
- `3151` - ABN AMRO
- `0021` - Rabobank
- `0751` - SNS Bank
- `0721` - ING
- `0031` - ABN AMRO

---

### Bancontact

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Card Numbers**:

| Card Number | Scenario | Description |
|-------------|----------|-------------|
| `67034500054620008` | Completed | Transaction completed (3D enrolled) |
| `67039902990000045` | Declined | Transaction declined (3D authentication failed) |
| `67039902990000011` | Declined | Transaction declined (insufficient funds) |

**Example Request**:
```json
{
  "amount": 50.00,
  "currency": "EUR",
  "paymentMethodCode": "BANCONTACT",
  "description": "Test Bancontact payment",
  "rentalExternalId": "rental-test-002"
}
```

**Test Steps**:
1. Enter card number from table above
2. Enter any future expiry date
3. Click "Confirm"
4. Observe result based on card number used

---

### Bizum

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Amount-Based Scenarios**:

| Amount Range | Expected Result |
|--------------|-----------------|
| €10.00 - €20.00 | Completed |
| < €9.99 | Declined |
| > €20.00 | Expired (after 84 hours) |

**Example Request**:
```json
{
  "amount": 15.00,
  "currency": "EUR",
  "paymentMethodCode": "BIZUM",
  "phone": "+34612345678",
  "description": "Test Bizum payment",
  "rentalExternalId": "rental-test-003"
}
```

**Note**: Bizum requires Spanish phone number (+34).

---

### Giropay

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 30.00,
  "currency": "EUR",
  "paymentMethodCode": "GIROPAY",
  "bic": "NOLADE22XXX",
  "description": "Test Giropay payment",
  "rentalExternalId": "rental-test-004"
}
```

**Test Steps**:
1. Enter any valid German BIC code (e.g., `NOLADE22XXX`)
2. Click "Confirm"
3. On test platform, select "Completed"
4. Click "Test"

---

### EPS

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 35.00,
  "currency": "EUR",
  "paymentMethodCode": "EPS",
  "bic": "RZOOAT2L420",
  "description": "Test EPS payment",
  "rentalExternalId": "rental-test-005"
}
```

**Test Steps**:
1. Enter any valid Austrian BIC code (e.g., `RZOOAT2L420`)
2. Click "Confirm"
3. On test platform, select "Completed"
4. Click "Test"

---

### Belfius, CBC, KBC

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 40.00,
  "currency": "EUR",
  "paymentMethodCode": "BELFIUS",
  "description": "Test Belfius payment",
  "rentalExternalId": "rental-test-006"
}
```

**Test Steps**:
1. On test platform, select "Completed"
2. Click "Test"
3. Transaction will show as Completed

**Note**: Same process applies for CBC and KBC, just change `paymentMethodCode`.

---

### Sofort & Trustly

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 45.00,
  "currency": "EUR",
  "paymentMethodCode": "SOFORT",
  "description": "Test Sofort payment",
  "rentalExternalId": "rental-test-007"
}
```

**Test Steps**:
1. On test platform, select "Completed"
2. Click "Test"
3. Transaction will show as Completed

---

### Multibanco & MB WAY

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Amount-Based Scenarios**:

| Amount | Scenario | Description |
|--------|----------|-------------|
| €9.99 and below | Declined | Transaction declined |
| €10.00 - €20.00 | Completed | Transaction completed |
| Above €20.00 | Expired | Initialized, expires after 84 hours |

**MB WAY Example Request**:
```json
{
  "amount": 15.00,
  "currency": "EUR",
  "paymentMethodCode": "MBWAY",
  "phone": "+351912345678",
  "description": "Test MB WAY payment",
  "rentalExternalId": "rental-test-008"
}
```

**Note**: MB WAY requires Portuguese phone number (+351).

---

### Direct Debit (SEPA)

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test IBANs**:

| IBAN | Initial Status | Final Status (after 2 min) | Notes |
|------|----------------|----------------------------|-------|
| `NL87ABNA0000000001` | Initiated | Completed | Use for successful tests and refunds |
| `NL87ABNA0000000002` | Initiated | Declined | Failed payment |
| `NL87ABNA0000000003` | Initiated | Uncleared → Completed | Uncleared then successful |
| `NL87ABNA0000000004` | Initiated | Uncleared → Declined | Uncleared then failed |

**Example Request**:
```json
{
  "amount": 55.00,
  "currency": "EUR",
  "paymentMethodCode": "DIRDEB",
  "accountHolderName": "Test User",
  "iban": "NL87ABNA0000000001",
  "description": "Test Direct Debit payment",
  "rentalExternalId": "rental-test-009"
}
```

---

### Bank Transfer

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test IBANs**:

| IBAN | Initial Status | Status After 2 min |
|------|----------------|-------------------|
| `NL87ABNA0000000001` | Initiated | Completed |
| `NL87ABNA0000000002` | Initiated | Expired |
| `NL87ABNA0000000004` | Initiated | Declined |
| Any other IBAN | Initiated | Expired (after 5 days) |

**Example Request**:
```json
{
  "amount": 60.00,
  "currency": "EUR",
  "paymentMethodCode": "BANK_TRANSFER",
  "accountHolderName": "Test User",
  "iban": "NL87ABNA0000000001",
  "description": "Test Bank Transfer",
  "rentalExternalId": "rental-test-010"
}
```

---

### Dotpay

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 65.00,
  "currency": "EUR",
  "paymentMethodCode": "DOTPAY",
  "description": "Test Dotpay payment",
  "rentalExternalId": "rental-test-011"
}
```

**Test Steps**:
1. Enter any email address
2. Enter any phone number
3. Select a bank (you may see more banks in live environment)
4. On test platform, select "Completed"
5. Click "Test"

---

### MyBank

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Banks** (select on test platform):

| Bank | Scenario |
|------|----------|
| Allianz Bank FA SPA | Completed (2 min) |
| Banca di Cesena - Credito Coop. | Declined (2 min) |
| Credito Artigiano | Cancelled (2 min) |
| Volksbank - Banca Popolare | Expired (2 min) |

**Example Request**:
```json
{
  "amount": 70.00,
  "currency": "EUR",
  "paymentMethodCode": "MYBANK",
  "description": "Test MyBank payment",
  "rentalExternalId": "rental-test-012"
}
```

---

## Card Methods

### Credit Card (Visa, Mastercard, Maestro, Amex)

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Card Numbers**:

| Card Type | Card Number | Expiry | CVV | Scenario |
|-----------|-------------|--------|-----|----------|
| Visa | `4111111111111111` | Any future | 123 | Completed (3D enrolled) |
| Visa | `4761340000000019` | Any future | 123 | Completed (3D enrolled) |
| Visa | `4917300000000008` | Any future | 123 | Uncleared → Void (3 min) |
| Visa | `4462000000000003` | Any future | 123 | Uncleared → Completed (3 min) |
| Visa | `4012001037461114` | Any future | 123 | Declined (3D failed) |
| Visa | `4012001038488884` | Any future | 123 | Declined (insufficient funds) |
| Mastercard | `5500000000000004` | Any future | 123 | Completed (3D enrolled) |
| Maestro | `6799990000000000011` | Any future | 123 | Completed (3D enrolled) |
| Amex | `374500000000015` | Any future | 1234 | Completed (3D enrolled) |
| Amex | `378734493671000` | Any future | 1234 | Uncleared → Void (3 min) |
| Amex | `374200000000004` | Any future | 1234 | Declined (3D failed) |

**Example Request**:
```json
{
  "amount": 75.00,
  "currency": "EUR",
  "paymentMethodCode": "VISA",
  "cardNumber": "4111111111111111",
  "cardHolderName": "Test User",
  "expiryDate": "12/25",
  "cvv": "123",
  "description": "Test Visa payment",
  "rentalExternalId": "rental-test-013"
}
```

**Test Steps**:
1. Enter card details from table above
2. Click "Confirm"
3. On 3D payment page, select "Authenticated (Y)"
4. Click "Confirm"
5. Check transaction status

---

## BNPL Methods

### Klarna

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 100.00,
  "currency": "EUR",
  "paymentMethodCode": "KLARNA",
  "birthday": "1990-01-01",
  "gender": "male",
  "phone": "+31612345678",
  "description": "Test Klarna payment",
  "rentalExternalId": "rental-test-014"
}
```

**Test Steps**:
1. Click "Kopen"
2. Enter any mobile number
3. Click "Ga verder"
4. Enter any 6-digit verification code
5. Click "Bevestigen"
6. Transaction shows as Uncleared

**Shipping Test**: Change order status to "Shipped" to test invoice generation.

**Refund Test**: Must ship order first, then initiate refund.

---

### Billink

**Requires Activation**: Email support@multisafepay.com to enable Billink for test account.

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 110.00,
  "currency": "EUR",
  "paymentMethodCode": "BILLINK",
  "birthday": "1990-01-01",
  "gender": "male",
  "companyType": "private",
  "description": "Test Billink payment",
  "rentalExternalId": "rental-test-015"
}
```

**Test Steps**:
1. Select "Private" or "Business"
2. Click "Confirm"
3. On test platform, select test scenario:
   - **Success**: Order status Completed, transaction Uncleared
   - **Failure**: Order status Declined, transaction Declined
   - **Cancelled**: Order status Void, transaction Void

---

### in3

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Customer Details**:
- **Success**: Birthday `01-01-1999`, Postal code `1234AB`, House number `1`
- **Decline**: Birthday `01-01-2000`, Postal code `1111AB`, House number `1`

**Example Request**:
```json
{
  "amount": 120.00,
  "currency": "EUR",
  "paymentMethodCode": "IN3",
  "birthday": "1999-01-01",
  "phone": "+31612345678",
  "description": "Test in3 payment",
  "rentalExternalId": "rental-test-016"
}
```

**Test Steps**:
1. Enter birthday, phone, and postal code
2. Select title and click "Confirm"
3. Accept terms and click "Afronden"
4. On test platform, select "Completed"
5. Click "Test"
6. On in3 page, click "Terug naar webshop"

---

### Riverty (AfterPay)

**Requires Activation**: Request test API key from Riverty, email support@multisafepay.com to enable.

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Customer Email**:
- **Reject**: Use email `test-reject@afterpay.nl` to test rejection

**Example Request**:
```json
{
  "amount": 130.00,
  "currency": "EUR",
  "paymentMethodCode": "AFTERPAY",
  "birthday": "1990-01-01",
  "gender": "female",
  "phone": "+31612345678",
  "email": "test@example.com",
  "description": "Test Riverty payment",
  "rentalExternalId": "rental-test-017"
}
```

**Test Steps**:
1. Select checkbox on Riverty page
2. Click "Confirm"
3. Transaction shows as Uncleared

**Note**: Cannot test receiving successful payment notifications or refunds in test environment.

---

### Pay After Delivery & E-Invoicing

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 140.00,
  "currency": "EUR",
  "paymentMethodCode": "PAYAFTER",
  "birthday": "1990-01-01",
  "bankAccount": "1234567890",
  "email": "test@example.com",
  "phone": "+31612345678",
  "description": "Test Pay After Delivery",
  "rentalExternalId": "rental-test-018"
}
```

**Test Steps**:
1. Enter birthday (DD-MM-YYYY format)
2. Enter 10-digit bank account number
3. Enter email and phone
4. Click "Confirm"
5. Order and transaction statuses show as Uncleared

**Decline Test**: In test dashboard, click "Decline" under Order summary.

**Shipping Test**: For E-Invoicing, update order status to "shipped" to receive `invoice_url`.

---

## Prepaid Cards

### Gift Cards (VVV, Beauty Cadeau, etc.)

**Supported Gift Cards**:
- Beauty Cadeau, Boeken Voordeel, Huis & Tuin Cadeau, Klus Cadeau
- Nationale Bioscoopbon, VVV Cadeaukaart, Wijn Cadeaukaart

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Card Numbers**:

| Card Number | Balance |
|-------------|---------|
| `111115` | €100 |
| `111112` | €5 |
| `111110` | €0 (no balance) |

**Example Request**:
```json
{
  "amount": 50.00,
  "currency": "EUR",
  "paymentMethodCode": "VVVGIFTCARD",
  "cardNumber": "111115",
  "pin": "1234",
  "description": "Test Gift Card payment",
  "rentalExternalId": "rental-test-019"
}
```

**Test Steps**:
1. Enter card number from table above
2. Enter any 4-digit security code
3. Click "Add discount"
4. Transaction shows as Completed

---

### Edenred

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 25.00,
  "currency": "EUR",
  "paymentMethodCode": "EDENRED",
  "cardNumber": "111115",
  "description": "Test Edenred payment",
  "rentalExternalId": "rental-test-020"
}
```

**Test Steps**:
1. On payment page, click "Add discount"
2. Select discount from test scenario list
3. Click "Test"
4. Transaction shows as Completed

---

### Monizze & Sodexo

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Test Card Numbers**:

| Card Number | Balance |
|-------------|---------|
| `111115` | €100 |
| `111112` | €5 |
| `111110` | €0 |

**Example Request** (Monizze):
```json
{
  "amount": 30.00,
  "currency": "EUR",
  "paymentMethodCode": "MONIZZE",
  "cardNumber": "111115",
  "pin": "1234",
  "description": "Test Monizze payment",
  "rentalExternalId": "rental-test-021"
}
```

**Test Steps**:
1. Enter card number from table
2. Enter any 4-digit security code
3. Click "Add discount"
4. Transaction shows as Completed

---

### Paysafecard

**Note**: Cannot test in test environment. Must test in live account with actual small payments.

---

## Wallets

### PayPal

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 80.00,
  "currency": "EUR",
  "paymentMethodCode": "PAYPAL",
  "description": "Test PayPal payment",
  "rentalExternalId": "rental-test-022"
}
```

**Test Steps**:
1. On test platform, select test scenario:
   - **Approved**: Order Completed, transaction Initialized
   - **Cancelled**: Order Void
   - **Closed**: Order Expired
2. Click "Test"

**Note**: Transaction status remains "Initialized" (MultiSafepay doesn't collect payments on behalf of PayPal).

---

### Apple Pay

**Compatible Devices**: iPhone, iPad, Mac with Touch ID/Face ID, or Apple Watch.

**Alternative**: Use [Appetize.io emulator](https://appetize.io) for testing without Apple device.

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 90.00,
  "currency": "EUR",
  "paymentMethodCode": "APPLEPAY",
  "applePayToken": "test_token_here",
  "description": "Test Apple Pay payment",
  "rentalExternalId": "rental-test-023"
}
```

**Test Steps**:
1. Click Apple Pay button on payment page
2. Sign in to Apple Developer account
3. Select test card
4. Authorize payment
5. Transaction completed

**Note**: Requires Apple Developer account with test cards configured.

---

### Google Pay

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 95.00,
  "currency": "EUR",
  "paymentMethodCode": "GOOGLEPAY",
  "googlePayToken": "test_token_here",
  "description": "Test Google Pay payment",
  "rentalExternalId": "rental-test-024"
}
```

**Test Steps**:
1. Click Google Pay button
2. Complete payment using Google account
3. Must add at least one chargeable card to Google account
4. May be redirected to 3D Secure based on card type
5. Check payment status in test dashboard

**Note**: Real card details not processed in test environment.

---

### Alipay

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 100.00,
  "currency": "EUR",
  "paymentMethodCode": "ALIPAY",
  "description": "Test Alipay payment",
  "rentalExternalId": "rental-test-025"
}
```

**Test Steps**:
1. On test platform, select "Completed"
2. Click "Test"
3. Order status: Completed, transaction status: Initialized

**Note**: Cannot test declined transactions.

---

### Amazon Pay

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 105.00,
  "currency": "EUR",
  "paymentMethodCode": "AMAZONPAY",
  "description": "Test Amazon Pay payment",
  "rentalExternalId": "rental-test-026"
}
```

**Test Steps**:
1. Wait 5 seconds or click "Amazon Pay"
2. On test platform, select "Completed"
3. Click "Test"
4. Order status: Completed, transaction status: Initialized

---

### WeChat Pay

**Test Endpoint**: `POST /api/v1/payments/mobile/test/direct`

**Example Request**:
```json
{
  "amount": 110.00,
  "currency": "EUR",
  "paymentMethodCode": "WECHAT",
  "description": "Test WeChat Pay payment",
  "rentalExternalId": "rental-test-027"
}
```

**Test Steps**:
1. Scan QR code with general QR reader (NOT WeChat app)
2. On test platform, select "Completed"
3. Click "Test"
4. Order status: Completed, transaction status: Completed

---

## Refund Testing

### General Refund Process

**Test Endpoint**: `POST /api/v1/payments/mobile/test/refund`

**Supported Payment Methods**:
- Banking: Bancontact, Bank Transfer, Belfius, CBC/KBC, Direct Debit, EPS, Giropay, iDEAL, Przelewy24, Sofort, Trustly
- Cards: All credit/debit cards
- BNPL: in3, Klarna
- Wallets: Alipay, PayPal, WeChat Pay

**Example Request**:
```json
{
  "orderId": "order_123456",
  "amount": 25.00,
  "currency": "EUR",
  "description": "Refund for test order"
}
```

**Test Steps (via Dashboard)**:
1. Create and complete a test order
2. Wait for transaction status to become "Completed"
3. In test dashboard, go to Order summary
4. Click "Refund order"
5. Enter refund details (amount, IBAN, reason)
6. Click "Confirm"
7. New refund order created with status "Reserved" or "Initialized"
8. Under Related transactions, select refund order ID
9. Click "Accept"
10. Order status changes to "Completed"

**Test Steps (via API)**:
1. Create and complete a test order
2. Make refund API request
3. New refund order created with status "Reserved"
4. In dashboard, accept the refund manually
5. Order status changes to "Completed"

---

## Cancellation Testing

**Supported Methods**: Belfius, CBC/KBC, Przelewy24, EPS, Giropay, iDEAL, Sofort, Trustly, Alipay, PayPal

**Test Steps**:
1. Create order via API or backend
2. On test platform, select "Cancelled"
3. Click "Test"
4. Order status changes to "Void"

---

## Troubleshooting

### Common Issues

#### No Payment Methods Appearing
- Check `multisafepay.test.mode=true` in application.properties
- Verify MultiSafepay API key is correct
- Check payment method is enabled in configuration

#### Transaction Stuck in "Initialized"
- Normal for PayPal, Alipay, Amazon Pay, Google Pay
- For other methods, check webhook configuration
- Verify notification URL is accessible

#### Webhook Not Received
- Check `multisafepay.notification.url` is configured correctly
- Use ngrok for local testing: `https://[your-ngrok].ngrok.io/api/v1/webhooks/multisafepay`
- Check Gateway route order (specific routes before general)
- Test manually: `POST /api/v1/webhooks/multisafepay/payout/test`

#### Test Card/IBAN Not Working
- Ensure you're using exact values from this guide
- Remove all spaces and formatting
- Check currency matches payment method requirements

#### 3D Secure Page Not Appearing
- Normal in test environment for some methods
- Select "Authenticated (Y)" when 3D page does appear
- Check browser allows popups/redirects

### Debug Mode

Enable detailed logging:
```properties
logging.level.org.clickenrent.paymentservice=DEBUG
payment.testing.log-requests=true
```

### Support

For additional help:
- **Technical Issues**: Check application logs
- **MultiSafepay Issues**: support@multisafepay.com
- **Integration Questions**: See [PAYMENT_METHODS_GUIDE.md](PAYMENT_METHODS_GUIDE.md)

---

## Test Coverage Checklist

Use this checklist to ensure all payment methods are tested:

### Banking Methods
- [ ] iDEAL (direct & QR)
- [ ] Bancontact (direct & QR)
- [ ] Belfius
- [ ] Bizum
- [ ] CBC/KBC
- [ ] Direct Debit (SEPA)
- [ ] Dotpay
- [ ] EPS
- [ ] Giropay
- [ ] MB WAY
- [ ] Multibanco
- [ ] MyBank
- [ ] Sofort
- [ ] Trustly
- [ ] Bank Transfer

### Cards
- [ ] Visa
- [ ] Mastercard
- [ ] Maestro
- [ ] American Express
- [ ] Dankort
- [ ] Cartes Bancaires
- [ ] Postepay

### BNPL
- [ ] Billink
- [ ] E-Invoicing
- [ ] in3
- [ ] Klarna
- [ ] Pay After Delivery
- [ ] Riverty (AfterPay)

### Prepaid
- [ ] Edenred
- [ ] Gift Cards (VVV, Beauty, etc.)
- [ ] Monizze
- [ ] Sodexo
- [ ] Paysafecard (live only)

### Wallets
- [ ] Alipay
- [ ] Alipay+
- [ ] Amazon Pay
- [ ] Apple Pay
- [ ] Google Pay
- [ ] PayPal
- [ ] WeChat Pay

### Additional Tests
- [ ] Refunds (5+ methods)
- [ ] Cancellations (3+ methods)
- [ ] Split payments
- [ ] Webhooks
- [ ] Error handling

---

**Last Updated**: January 21, 2026  
**Version**: 1.0.0  
**Status**: Complete
