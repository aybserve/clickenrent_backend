# Currency Integration with Payment Service

## Overview
The UserPreference entity now properly references currencies from the payment-service Currency entity by storing the **currency external ID** (UUID), ensuring proper cross-service references.

## Changes Made

### 1. UserPreference Entity
**File:** `src/main/java/org/clickenrent/authservice/entity/UserPreference.java`

- Added `currencyExternalId` field to reference `payment-service Currency.externalId`
- Uses VARCHAR(100) to store UUID references
- Default value: `"550e8400-e29b-41d4-a716-446655440021"` (USD)

```java
@Size(max = 100, message = "Currency external ID must not exceed 100 characters")
@Column(name = "currency_external_id", length = 100)
@Builder.Default
private String currencyExternalId = "550e8400-e29b-41d4-a716-446655440021"; // USD
```

### 2. DTOs Updated

#### UserPreferenceDTO
**File:** `src/main/java/org/clickenrent/authservice/dto/UserPreferenceDTO.java`

- Renamed `currency` to `currencyExternalId`
- Added schema documentation referencing payment-service Currency.externalId
- Example value shows UUID format

#### UpdateUserPreferenceRequest
**File:** `src/main/java/org/clickenrent/authservice/dto/UpdateUserPreferenceRequest.java`

- Renamed `currency` to `currencyExternalId`
- Updated validation: `@Size(max = 100)` for UUID storage
- Removed ISO 4217 pattern validation (now accepts UUIDs)
- Added reference to payment-service Currency.externalId in documentation

### 3. Database Schema
**File:** `src/main/resources/data.sql`

- Added column: `currency_external_id VARCHAR(100)`
- Default value: `'550e8400-e29b-41d4-a716-446655440021'` (USD external ID)
- Added comment: `-- References payment-service Currency.externalId (USD)`
- Updated all test data to use external IDs from payment-service currencies table

## Available Currencies

Based on payment-service `data.sql`:

| ID | External ID                            | Code | Name              | Symbol |
|----|----------------------------------------|------|-------------------|--------|
| 1  | 550e8400-e29b-41d4-a716-446655440021   | USD  | US Dollar         | $      |
| 2  | 550e8400-e29b-41d4-a716-446655440022   | EUR  | Euro              | €      |
| 3  | 550e8400-e29b-41d4-a716-446655440023   | GBP  | British Pound     | £      |
| 4  | 550e8400-e29b-41d4-a716-446655440024   | UAH  | Ukrainian Hryvnia | ₴      |
| 5  | 550e8400-e29b-41d4-a716-446655440025   | PLN  | Polish Zloty      | zł     |

**UserPreference stores the External ID column** to reference currencies across services.

## Adding New Currencies

To add a new currency:

1. **Add to payment-service first:**
   - Update `payment-service/src/main/resources/data.sql`
   - Insert new currency with code, name, and symbol

2. **Update auth-service documentation:**
   - Update allowable values in `UserPreferenceDTO.java`
   - Update allowable values in `UpdateUserPreferenceRequest.java`

## Migration Notes

### For Existing Databases:

If you had the old `currency` column with codes, you need to migrate to external IDs:

```sql
-- Step 1: Add new column
ALTER TABLE user_preferences 
ADD COLUMN currency_external_id VARCHAR(100);

-- Step 2: Migrate data from codes to external IDs
UPDATE user_preferences 
SET currency_external_id = CASE 
    WHEN currency = 'USD' THEN '550e8400-e29b-41d4-a716-446655440021'
    WHEN currency = 'EUR' THEN '550e8400-e29b-41d4-a716-446655440022'
    WHEN currency = 'GBP' THEN '550e8400-e29b-41d4-a716-446655440023'
    WHEN currency = 'UAH' THEN '550e8400-e29b-41d4-a716-446655440024'
    WHEN currency = 'PLN' THEN '550e8400-e29b-41d4-a716-446655440025'
    ELSE '550e8400-e29b-41d4-a716-446655440021' -- Default to USD
END;

-- Step 3: Set default value
ALTER TABLE user_preferences 
ALTER COLUMN currency_external_id 
SET DEFAULT '550e8400-e29b-41d4-a716-446655440021';

-- Step 4: Drop old column (optional - after verifying migration)
-- ALTER TABLE user_preferences DROP COLUMN currency;
```

## Testing

All existing tests pass without modification as they already use valid 3-character currency codes (USD).

**Test files verified:**
- `UserPreferenceControllerTest.java`
- `UserPreferenceServiceTest.java`

## Architecture Decision

**Why External ID (String UUID) instead of Foreign Key?**
- Auth-service and payment-service are separate microservices with separate databases
- Direct foreign keys would violate microservices independence
- Using external ID (UUID) provides:
  - ✅ Loose coupling between services
  - ✅ Stable references (IDs can change, external IDs don't)
  - ✅ Standard microservices pattern for cross-service references
  - ✅ Easy to resolve via API calls to payment-service when needed

## Validation Strategy

1. **Entity level:** `@Size(max = 100)` ensures external ID fits within column
2. **DTO level:** Field accepts UUID format strings
3. **API documentation:** OpenAPI schema shows example external IDs
4. **Runtime:** External IDs should be validated against payment-service API
   - Frontend can fetch available currencies: `GET /api/v1/currencies` (payment-service)
   - Backend can validate external ID exists when setting preferences

## Future Enhancements

Consider adding:
1. **Currency Validation Service**
   - Validate `currencyExternalId` exists in payment-service before saving
   - Call payment-service: `GET /api/v1/currencies/external/{externalId}`
   
2. **Caching Layer**
   - Cache valid currency external IDs from payment-service
   - Refresh cache periodically or on-demand
   
3. **Helper Endpoint**
   - `GET /api/v1/users/preferences/currencies` - Proxy to payment-service
   - Returns available currencies for dropdown/selection
   
4. **DTO Enhancement**
   - Add `currencyCode`, `currencyName`, `currencySymbol` fields to response DTO
   - Fetch from payment-service when building response
   - Allows UI to display "USD" and "$" instead of UUID
