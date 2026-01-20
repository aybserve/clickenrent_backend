# DTO Architecture and Shared Contracts

## Overview

This document explains the Data Transfer Object (DTO) architecture across the microservices and how shared-contracts are used to ensure consistency in cross-service communication.

## Current Architecture

### Shared Contracts Module

The `shared-contracts` module contains DTOs that define the **contract** for cross-service communication. These DTOs include:

**Auth Service Contracts:**
- `UserDTO` - User profile and authentication data
- `CompanyDTO` - Company/organization data

**Rental Service Contracts:**
- `BikeDTO` - Bike/vehicle information
- `BikeRentalDTO` - Rental session data
- `BikeTypeDTO` - Bike type classification
- `LocationDTO` - Location/hub information
- `RentalDTO` - Rental order data
- `RideDTO` - Individual ride/trip data

**Support Service Contracts:**
- `BikeRentalFeedbackDTO` - Customer feedback data

**Security Contracts:**
- `AuditEvent`, `AuditService` - Audit logging
- `TenantContext`, `TenantScoped` - Multi-tenancy support

### Microservice DTOs

Each microservice maintains its own copy of DTOs in their respective `dto` packages:
- `auth-service/dto/UserDTO.java`
- `auth-service/dto/CompanyDTO.java`
- `rental-service/dto/RentalDTO.java`
- etc.

## Synchronization Status (Jan 2026)

As of January 2026, all DTOs have been **synchronized** between shared-contracts and microservices:

### ✅ Fully Synchronized DTOs

1. **UserDTO** - All validation annotations added to auth-service
2. **CompanyDTO** - Validation constraints aligned between contract and service
3. **RentalDTO** - All validation annotations added to rental-service
4. **BikeDTO** - Already synchronized
5. **BikeRentalDTO** - Already synchronized
6. **BikeTypeDTO** - Already synchronized
7. **LocationDTO** - Already synchronized
8. **RideDTO** - Already synchronized
9. **BikeRentalFeedbackDTO** - Already synchronized

### Key Changes Made

#### UserDTO Synchronization
Added validation annotations to `auth-service/dto/UserDTO.java`:
- Pattern validation for `externalId`
- Size constraints for `userName`, `email`, `firstName`, `lastName`, `phone`, `imageUrl`
- Required field validations for `email`, `firstName`, `lastName`

#### CompanyDTO Synchronization
Aligned validation constraints in `auth-service/dto/CompanyDTO.java`:
- Pattern validation for UUID `externalId`
- Consistent size limits: `name` (2-100), `description` (max 500), `website` (max 200)
- Added `@NotNull` for `companyTypeId`
- Size constraints for `logo` and `erpPartnerId`

#### RentalDTO Synchronization
Added validation annotations to `rental-service/dto/RentalDTO.java`:
- Pattern validation for UUID `externalId`
- Required field validation for `rentalStatusId` and `userExternalId`
- Size and pattern constraints for cross-service references

## Current Usage Patterns

### Feign Client Communication
Services use **shared-contracts DTOs** for inter-service communication:

```java
// auth-service/client/RentalServiceClient.java
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.contracts.rental.RentalDTO;

// rental-service/client/AuthServiceClient.java
import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.contracts.auth.CompanyDTO;

// support-service/client/BikeServiceClient.java
import org.clickenrent.contracts.rental.BikeDTO;
```

### Internal Service Operations
Services use **their own DTOs** for internal operations:
- Controllers receive/return service-specific DTOs
- Mappers convert between Entity and service-specific DTOs
- Service layer works with service-specific DTOs

## Recommendations

### Option 1: Continue Current Architecture (Recommended for Stability)

**Pros:**
- Clear separation between internal and external contracts
- Services can evolve DTOs independently for internal use
- Shared contracts remain stable as the communication interface
- No breaking changes required

**Cons:**
- Duplicate code maintenance
- Risk of drift if not kept synchronized
- Larger codebase

**When to use:** If services need flexibility to add internal-only fields or validation logic that shouldn't be exposed in contracts.

### Option 2: Use Shared Contracts Directly (Recommended for Long-term)

**Pros:**
- Single source of truth
- No duplication or drift
- Smaller codebase
- Easier to maintain consistency

**Cons:**
- Requires refactoring all mappers and controllers
- Changes to contracts affect all consuming services immediately
- Less flexibility for service-specific requirements

**Migration Steps:**
1. Update all mappers to use `org.clickenrent.contracts.*` imports
2. Update all controllers to use shared-contracts DTOs
3. Update all service classes to use shared-contracts DTOs
4. Remove duplicate DTOs from service modules
5. Ensure all services are on compatible versions of shared-contracts

### Option 3: Hybrid Approach (Current State)

**Current Implementation:**
- Use shared-contracts for Feign client communication
- Use service-specific DTOs for internal operations
- Keep both synchronized through tooling or CI/CD validation

**Recommendation:** Implement automated tests to ensure DTO synchronization:

```java
@Test
public void testUserDTOSynchronization() {
    // Compare fields between service DTO and contract DTO
    Field[] serviceFields = org.clickenrent.authservice.dto.UserDTO.class.getDeclaredFields();
    Field[] contractFields = org.clickenrent.contracts.auth.UserDTO.class.getDeclaredFields();
    
    // Assert field names and types match
    // Assert validation annotations match
}
```

## Validation Strategy

All shared-contracts DTOs now include comprehensive validation:

### Validation Annotations Used
- `@NotNull` - Required fields (IDs)
- `@NotBlank` - Required string fields (email, names, etc.)
- `@Email` - Email format validation
- `@Size` - String length constraints
- `@Pattern` - Regular expression validation (UUIDs, external IDs, phone numbers)

### Validation Execution Points
1. **API Gateway** - First line of defense for external requests
2. **Controller Layer** - Using `@Valid` annotation on request bodies
3. **Service Layer** - Business logic validation
4. **Feign Clients** - Contract validation for inter-service calls

## Maintenance Guidelines

### When Adding New Fields

1. **Add to shared-contracts first** if the field is part of the service contract
2. Add appropriate validation annotations
3. Update version in DTO documentation comment
4. Update corresponding service DTO
5. Update mappers in all consuming services
6. Document breaking changes in CHANGELOG

### When Modifying Validations

1. Evaluate impact on all consuming services
2. Consider backward compatibility
3. Use versioning strategy if breaking change
4. Update both shared-contracts and service DTOs simultaneously
5. Update integration tests

### Versioning Strategy

Consider adding version numbers to shared-contracts DTOs:

```java
/**
 * @version 2.0.0
 * 
 * BREAKING CHANGES in v2.0.0:
 * - Removed field X
 * - Added validation for field Y
 */
```

## Future Improvements

1. **Automated Synchronization Tests** - CI/CD pipeline to detect drift
2. **DTO Generation** - Consider generating service DTOs from shared-contracts
3. **API Versioning** - Implement versioned contracts for backward compatibility
4. **OpenAPI Integration** - Generate OpenAPI specs from shared-contracts
5. **Migration to Shared-Contracts Only** - Gradual migration to eliminate duplicate DTOs

## Dependencies

All microservices have declared dependency on shared-contracts:

```xml
<dependency>
    <groupId>org.clickenrent</groupId>
    <artifactId>shared-contracts</artifactId>
</dependency>
```

## Conclusion

The current hybrid approach provides a balance between flexibility and consistency. All DTOs are now synchronized as of January 2026. For new projects or major refactoring, consider migrating to use shared-contracts DTOs directly (Option 2) to eliminate duplication and ensure long-term maintainability.
