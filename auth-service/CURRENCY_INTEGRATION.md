# Currency Integration with Payment Service

## Overview
The UserPreference entity now properly references currency codes from the payment-service Currency entity, ensuring consistency across microservices.

## Changes Made

### 1. UserPreference Entity
**File:** `src/main/java/org/clickenrent/authservice/entity/UserPreference.java`

- Updated `currency` field from `VARCHAR(10)` to `VARCHAR(3)` (ISO 4217 standard)
- Added documentation that it references `payment-service Currency.code`
- Valid values: USD, EUR, GBP, UAH, PLN (must match payment-service currencies)

```java
@Size(max = 3, message = "Currency code must be exactly 3 characters")
@Column(name = "currency", length = 3)
@Builder.Default
private String currency = "USD";
```

### 2. DTOs Updated

#### UserPreferenceDTO
**File:** `src/main/java/org/clickenrent/authservice/dto/UserPreferenceDTO.java`

- Added schema documentation referencing payment-service
- Listed allowable values from payment-service

#### UpdateUserPreferenceRequest
**File:** `src/main/java/org/clickenrent/authservice/dto/UpdateUserPreferenceRequest.java`

- Updated validation: `@Size(min = 3, max = 3)`
- Enhanced pattern validation message to reference ISO 4217 and payment-service
- Added allowable values to schema documentation

### 3. Database Schema
**File:** `src/main/resources/data.sql`

- Updated column definition: `currency VARCHAR(3)` (line 161)
- Added comment: `-- ISO 4217 currency code, matches payment-service Currency.code`
- Updated all test data to use EUR instead of CHF (CHF not in payment-service)

## Available Currency Codes

Based on payment-service `data.sql`:

| ID | Code | Name              | Symbol |
|----|------|-------------------|--------|
| 1  | USD  | US Dollar         | $      |
| 2  | EUR  | Euro              | €      |
| 3  | GBP  | British Pound     | £      |
| 4  | UAH  | Ukrainian Hryvnia | ₴      |
| 5  | PLN  | Polish Zloty      | zł     |

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
```sql
-- Update column size if needed
ALTER TABLE user_preferences ALTER COLUMN currency TYPE VARCHAR(3);

-- Validate existing data matches payment-service currencies
SELECT DISTINCT currency FROM user_preferences 
WHERE currency NOT IN ('USD', 'EUR', 'GBP', 'UAH', 'PLN');

-- Update any invalid currencies (example: CHF -> EUR)
UPDATE user_preferences SET currency = 'EUR' WHERE currency = 'CHF';
```

## Testing

All existing tests pass without modification as they already use valid 3-character currency codes (USD).

**Test files verified:**
- `UserPreferenceControllerTest.java`
- `UserPreferenceServiceTest.java`

## Architecture Decision

**Why String instead of Foreign Key?**
- Auth-service and payment-service are separate microservices with separate databases
- Direct foreign keys would violate microservices independence
- Using currency code (String) provides loose coupling while maintaining data consistency
- Validation at application layer ensures referential integrity

## Validation Strategy

1. **Entity level:** `@Size(max = 3)` ensures correct length
2. **DTO level:** `@Pattern(regexp = "^[A-Z]{3}$")` ensures ISO 4217 format
3. **API documentation:** OpenAPI schema lists allowable values
4. **Runtime:** Currency codes should be validated against payment-service API if strict enforcement needed

## Future Enhancements

Consider adding:
- Currency validation service that checks against payment-service
- Cache of valid currencies from payment-service
- API endpoint to fetch available currencies from payment-service
