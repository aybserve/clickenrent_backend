# Mobile Payment Split Payments Implementation

## Overview

Split payment functionality has been successfully integrated into both test and production mobile payment endpoints.

## Endpoint Categories

All endpoints are available in both **Test** (no auth) and **Production** (JWT auth) versions:

| Category | Endpoint Pattern | Purpose | Splits |
|----------|-----------------|---------|--------|
| **Basic** | `/direct`, `/redirect` | Full control via request body | Optional |
| **Quick** | `/direct/ideal`, `/redirect/quick` | Simplified query params | No splits |
| **Explicit Splits** | `/direct/splits`, `/redirect/splits` | Body with required splits | Required |
| **Quick Splits** | `/direct/ideal/splits`, `/redirect/quick/splits` | Query params with splits | Required |

### When to Use Each Endpoint Type

1. **Basic Endpoints** (`/direct`, `/redirect`)
   - Full flexibility
   - Splits are optional
   - All payment details in request body
   - Best for production applications

2. **Quick Endpoints** (`/direct/ideal`, `/redirect/quick`)
   - Testing and simple integrations
   - Query parameters only
   - No split payments support
   - Fast and convenient

3. **Explicit Splits Endpoints** (`/direct/splits`, `/redirect/splits`)
   - When splits are always required
   - Clear API semantics
   - Full request body control
   - Best for B2B integrations

4. **Quick Splits Endpoints** (`/direct/ideal/splits`, `/redirect/quick/splits`)
   - Testing split payments quickly
   - Simple query parameter interface
   - One partner split only (can be customized)
   - Great for development

## Endpoints

### Test Endpoints (No Authentication Required)

**Basic Endpoints:**
- `GET /api/v1/payments/mobile/test/methods` - List available payment methods
- `GET /api/v1/payments/mobile/test/ideal/banks` - Get iDEAL bank list
- `POST /api/v1/payments/mobile/test/direct` - Create direct payment with optional splits
- `POST /api/v1/payments/mobile/test/redirect` - Create redirect payment with optional splits
- `GET /api/v1/payments/mobile/test/status/{orderId}` - Get payment status
- `GET /api/v1/payments/mobile/test/health` - Health check

**Quick Endpoints:**
- `POST /api/v1/payments/mobile/test/direct/ideal?amount=10&issuerId=0031` - Quick iDEAL payment
- `POST /api/v1/payments/mobile/test/redirect/quick?amount=10` - Quick redirect payment

**Explicit Split Endpoints:**
- `POST /api/v1/payments/mobile/test/direct/splits` - Direct payment with splits (body)
- `POST /api/v1/payments/mobile/test/redirect/splits` - Redirect payment with splits (body)

**Quick Split Endpoints:**
- `POST /api/v1/payments/mobile/test/direct/ideal/splits?amount=25&issuerId=0031&partnerMerchantId=XXX&partnerPercentage=30` - Quick iDEAL with splits
- `POST /api/v1/payments/mobile/test/redirect/quick/splits?amount=25&partnerMerchantId=XXX&partnerPercentage=30` - Quick redirect with splits

### Production Endpoints (JWT Authentication Required)

**Basic Endpoints:**
- `GET /api/v1/payments/mobile/methods` - List available payment methods
- `GET /api/v1/payments/mobile/ideal/banks` - Get iDEAL bank list
- `POST /api/v1/payments/mobile/direct` - Create direct payment with optional splits
- `POST /api/v1/payments/mobile/redirect` - Create redirect payment with optional splits
- `GET /api/v1/payments/mobile/status/{orderId}` - Get payment status
- `GET /api/v1/payments/mobile/health` - Health check

**Quick Endpoints:**
- `POST /api/v1/payments/mobile/direct/ideal?amount=10&issuerId=0031` - Quick iDEAL payment
- `POST /api/v1/payments/mobile/redirect/quick?amount=10` - Quick redirect payment

**Explicit Split Endpoints:**
- `POST /api/v1/payments/mobile/direct/splits` - Direct payment with splits (body)
- `POST /api/v1/payments/mobile/redirect/splits` - Redirect payment with splits (body)

**Quick Split Endpoints:**
- `POST /api/v1/payments/mobile/direct/ideal/splits?amount=25&issuerId=0031&partnerMerchantId=XXX&partnerPercentage=30` - Quick iDEAL with splits
- `POST /api/v1/payments/mobile/redirect/quick/splits?amount=25&partnerMerchantId=XXX&partnerPercentage=30` - Quick redirect with splits

**All production endpoints require:**
- **Authentication**: Bearer JWT token required
- **Authorization**: Requires `USER`, `ADMIN`, or `B2B_CLIENT` authority
- **Rate Limiting**: User-based rate limiting applied

## Request Format

Both direct and redirect payment endpoints accept the same `MobilePaymentRequestDTO` with an optional `splits` array:

```json
{
  "amount": 100.00,
  "currency": "EUR",
  "customerEmail": "customer@example.com",
  "description": "Bike rental payment",
  "paymentMethodCode": "IDEAL",
  "issuerId": "0031",
  "splits": [
    {
      "merchantId": "12345678",
      "percentage": 15.5,
      "fixedAmountCents": null,
      "description": "Partner commission",
      "reference": "PARTNER-001"
    }
  ],
  "rentalExternalId": "rental-123",
  "metadata": {
    "bikeId": "bike-456",
    "locationId": "loc-789"
  }
}
```

## Split Payment Fields

- `merchantId` (required): MultiSafepay merchant ID of the partner
- `percentage` (optional): Percentage of the total amount (BigDecimal, e.g., 15.5 for 15.5%)
- `fixedAmountCents` (optional): Fixed amount in cents (Integer, e.g., 1500 for €15.00)
- `description` (optional): Description of the split
- `reference` (optional): Your internal reference for tracking

**Note**: Either `percentage` OR `fixedAmountCents` should be specified, not both.

## Payment Flows

### Direct Payment with Splits (iDEAL, DirectBank)

1. Client includes `splits` array in the payment request
2. Backend validates split payment data
3. MultiSafepay creates order with affiliate/split configuration
4. Returns transaction URL for minimal WebView authentication
5. Funds are automatically split according to the configuration upon payment completion

### Redirect Payment with Splits

1. Client includes `splits` array in the payment request
2. Backend validates split payment data
3. MultiSafepay creates order with affiliate/split configuration
4. Returns payment URL for full WebView
5. Funds are automatically split according to the configuration upon payment completion

## Validation

The system validates:
- At least one split is provided if `splits` array is not null
- Each split has a valid merchant ID
- Each split has either percentage OR fixed amount (not both)
- Percentages are between 0 and 100
- Fixed amounts are positive

## Implementation Details

### Updated Files

1. **MobilePaymentRequestDTO**
   - Added `List<SplitPaymentDTO> splits` field

2. **MobilePaymentService**
   - Updated `createDirectPayment()` to conditionally add splits
   - Updated `createRedirectPayment()` to conditionally add splits

3. **MobilePaymentController** (Production)
   - Updated API documentation to mention split payment support
   - Added split logging for both direct and redirect endpoints

4. **MobilePaymentTestController** (Test)
   - Already supports splits via `MobilePaymentRequestDTO`

5. **Order Model**
   - Added `setDirectIdealWithSplits()` method
   - Added `setDirectBankWithSplits()` method

### API Gateway Configuration

- Test endpoints: `/api/v1/payments/mobile/test/**` (public access)
- Production endpoints: `/api/v1/payments/mobile/**` (JWT auth + rate limiting)

## Testing

### Test Without Splits

```bash
curl -X POST http://localhost:8888/api/v1/payments/mobile/test/direct \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 25.00,
    "currency": "EUR",
    "customerEmail": "test@example.com",
    "description": "Test payment",
    "paymentMethodCode": "IDEAL",
    "issuerId": "0031"
  }'
```

### Test With Splits

```bash
curl -X POST http://localhost:8888/api/v1/payments/mobile/test/direct \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "currency": "EUR",
    "customerEmail": "test@example.com",
    "description": "Test payment with splits",
    "paymentMethodCode": "IDEAL",
    "issuerId": "0031",
    "splits": [
      {
        "merchantId": "YOUR_PARTNER_MERCHANT_ID",
        "percentage": 20.0,
        "description": "Partner commission",
        "reference": "PARTNER-001"
      }
    ]
  }'
```

### Production Request With Authentication

**Basic Direct Payment with Splits (Full body):**
```bash
curl -X POST http://localhost:8888/api/v1/payments/mobile/direct \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 100.00,
    "currency": "EUR",
    "customerEmail": "customer@example.com",
    "description": "Bike rental",
    "paymentMethodCode": "IDEAL",
    "issuerId": "0031",
    "splits": [
      {
        "merchantId": "PARTNER_MERCHANT_ID",
        "percentage": 15.0,
        "description": "Location owner commission"
      }
    ]
  }'
```

**Explicit Splits Endpoint:**
```bash
curl -X POST http://localhost:8888/api/v1/payments/mobile/direct/splits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 100.00,
    "currency": "EUR",
    "paymentMethodCode": "IDEAL",
    "issuerId": "0031",
    "splits": [
      {
        "merchantId": "12345678",
        "percentage": 20.0,
        "description": "Partner share"
      }
    ]
  }'
```

**Quick iDEAL Payment (Query parameters):**
```bash
curl -X POST "http://localhost:8888/api/v1/payments/mobile/direct/ideal?amount=50.00&issuerId=0031&currency=EUR" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Quick iDEAL with Splits (Query parameters):**
```bash
curl -X POST "http://localhost:8888/api/v1/payments/mobile/direct/ideal/splits?amount=100.00&issuerId=0031&partnerMerchantId=12345678&partnerPercentage=25&currency=EUR" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Quick Redirect Payment:**
```bash
curl -X POST "http://localhost:8888/api/v1/payments/mobile/redirect/quick?amount=75.00&currency=EUR" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Quick Redirect with Splits:**
```bash
curl -X POST "http://localhost:8888/api/v1/payments/mobile/redirect/quick/splits?amount=100.00&partnerMerchantId=12345678&partnerPercentage=30&currency=EUR" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## MultiSafepay Requirements

To use split payments, you need:

1. A MultiSafepay account with split payments enabled
2. Valid merchant IDs for all partners receiving split payments
3. Partners must have their own MultiSafepay accounts
4. All partners must be configured in your MultiSafepay dashboard

## Logging

The production controller now logs split payment information:

```
Creating direct payment for user: user-123 with method: IDEAL (with 2 splits)
Creating redirect payment for user: user-456 (with 1 splits)
```

## Security

- Production endpoints require valid JWT authentication
- User-based rate limiting prevents abuse
- Split payment data is validated before sending to MultiSafepay
- Unauthorized users cannot create payments on behalf of others

## Error Handling

Common errors:
- `400 Bad Request`: Invalid split payment data or validation failure
- `401 Unauthorized`: Missing or invalid JWT token (production endpoints only)
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: MultiSafepay API error or integration issue

## Payment Method Clarifications

### iDEAL vs DirectBank

Both payment methods go through MultiSafepay and support split payments:

| Feature | iDEAL | DirectBank (SEPA Direct Debit) |
|---------|-------|--------------------------------|
| **Type** | Instant bank transfer | Bank account authorization |
| **Flow** | Customer selects bank → authenticates → pays | Customer provides bank details → you debit later |
| **Speed** | Immediate | Delayed (authorization now, debit later) |
| **Country** | Netherlands only | SEPA countries |
| **Destination** | Your MultiSafepay account | Your MultiSafepay account |
| **Splits** | To other MultiSafepay merchants | To other MultiSafepay merchants |
| **Use Case** | Immediate payment required | Subscription/recurring payments |

### Important: Splits Are NOT Direct Bank Transfers

**What splits ARE:**
- Revenue sharing between MultiSafepay merchant accounts
- Automatic distribution at point of sale
- Partner must have a MultiSafepay merchant account
- Example: €100 payment → €70 to you, €30 to partner (both MultiSafepay accounts)

**What splits are NOT:**
- Direct bank account transfers
- Payouts to any bank account (IBAN)
- For that, use the MultiSafepay Payout API

### iDEAL Split Payment Example Flow

```
1. Customer pays €100 via iDEAL
   └─> Money goes to YOUR MultiSafepay merchant account
   
2. MultiSafepay automatically splits:
   ├─> €70 stays in YOUR account (merchant ID: 11111111)
   └─> €30 goes to PARTNER account (merchant ID: 22222222)
   
3. Both you and partner can withdraw to your respective bank accounts
```

### When You Need Direct Bank Payouts

If you want to pay partners who DON'T have MultiSafepay accounts, or pay on a schedule (monthly), use:
- **MultiSafepay Payout API** (not yet implemented)
- This allows you to send money directly to IBANs
- Good for monthly commission payouts, refunds, etc.

## Next Steps

1. Test split payments in MultiSafepay test environment
2. Verify split fund distribution in MultiSafepay dashboard
3. Implement webhook handling for split payment status updates
4. Set up monitoring for split payment transactions
5. Configure partner merchant IDs in your application database
6. Consider implementing MultiSafepay Payout API for non-MultiSafepay partners
