# Cross-Service Reference Audit Report

## Summary

This document provides an audit of entities across microservices to identify which ones need `externalId` fields for cross-service references.

## Audit Date
December 19, 2025

## Principles

In a microservices architecture:
- **Internal IDs (Long)** should NEVER be shared between services
- **External IDs (String/UUID)** should be used for all cross-service references
- Services should maintain both fields during migration for backward compatibility

## Status by Service

### ✅ Auth Service

All entities that need to be referenced externally already have `externalId`:
- ✅ `User` - has externalId
- ✅ `Company` - has externalId

### ✅ Rental Service

#### Entities with External References (Properly Configured)
- ✅ `Rental` - has both `userId/userExternalId` and `companyId/companyExternalId`
- ✅ `Location` - has `companyId/companyExternalId`
- ✅ `BikeReservation` - has `userId/userExternalId`
- ✅ `UserLocation` - has `userId/userExternalId`
- ✅ `BikeBrand` - has `companyId/companyExternalId`
- ✅ `PartBrand` - has `companyId/companyExternalId`
- ✅ `ChargingStationBrand` - has `companyId/companyExternalId`
- ✅ `BikeRental` - has externalId
- ✅ `BikeEngine` - has externalId
- ✅ `PartCategory` - has externalId
- ✅ **`BikeType`** - **FIXED** - now has externalId (added in this implementation)

#### Lookup Tables (Internal Only - No ExternalId Needed)
These entities are internal lookup tables within rental-service and are NOT referenced from other services:
- `BikeStatus` - internal status enum
- `RentalStatus` - internal status enum
- `BikeRentalStatus` - internal status enum
- `RideStatus` - internal status enum
- `LockStatus` - internal status enum
- `ChargingStationStatus` - internal status enum
- `BatteryChargeStatus` - internal status enum
- `B2BSaleStatus` - internal status enum
- `B2BSaleOrderStatus` - internal status enum
- `B2BSubscriptionStatus` - internal status enum
- `B2BSubscriptionOrderStatus` - internal status enum
- `LocationRole` - internal role enum
- `LockProvider` - internal provider enum
- `RentalPlan` - internal pricing entity

### ✅ Payment Service

All entities with cross-service references properly configured:
- ✅ `UserPaymentProfile` - has `userId/userExternalId`
- ✅ `RentalFinTransaction` - has `rentalId/rentalExternalId` and `bikeRentalId/bikeRentalExternalId`
- ✅ `B2BRevenueSharePayout` - has `companyId/companyExternalId`
- ✅ `FinancialTransaction` - has externalId
- ✅ `PaymentMethod` - has externalId (lookup table)
- ✅ `PaymentStatus` - has externalId (lookup table)
- ✅ `ServiceProvider` - has externalId (lookup table)
- ✅ `Currency` - has externalId (lookup table)

### ✅ Support Service

All entities with cross-service references properly configured:
- ✅ `SupportRequest` - has `userId/userExternalId` and `bikeId/bikeExternalId`
- ✅ `Feedback` - has `userId/userExternalId`
- ✅ `BikeRentalFeedback` - has `userId/userExternalId` and `bikeRentalId/bikeRentalExternalId`
- ✅ **`BikeTypeBikeIssue`** - **FIXED** - now has `bikeTypeExternalId` (added in this implementation)

#### Lookup Tables (Internal Only)
- `BikeIssue` - has externalId (can be referenced)
- `ErrorCode` - has externalId (can be referenced)
- `SupportRequestStatus` - internal status enum

## Recently Fixed Issues

### BikeType Cross-Service Reference ✅ RESOLVED

**Problem:** 
`BikeTypeBikeIssue` in support-service was referencing `bikeTypeId` as Long, which is the internal database ID from rental-service.

**Solution Implemented:**
1. ✅ Added `externalId` field to `BikeType` entity in rental-service
2. ✅ Added `bikeTypeExternalId` field to `BikeTypeBikeIssue` entity in support-service
3. ✅ Updated DTOs and mappers to include externalId fields
4. ✅ Added repository methods to query by externalId
5. ✅ Updated services to support externalId queries
6. ✅ Added API endpoints to accept externalId
7. ✅ Created database migrations
8. ✅ Updated all tests

**Migration Files Created:**
- `rental-service/src/main/resources/db/migration/V3__add_bike_type_external_id.sql`
- `support-service/src/main/resources/db/migration/V3__add_bike_type_external_id_reference.sql`

## Recommendations

### 1. Maintain Dual-Field Pattern During Migration
Keep both internal IDs and external IDs during the transition period:
```java
private Long bikeTypeId;              // Keep for backward compatibility
private String bikeTypeExternalId;    // New cross-service reference
```

### 2. Eventually Remove Internal ID References
After services are fully migrated and tested:
- Remove `bikeTypeId` field from `BikeTypeBikeIssue`
- Update all code to use only `bikeTypeExternalId`
- Remove old API endpoints that accept internal IDs

### 3. API Design Guidelines
- New endpoints should use externalId in path: `/api/bike-types/external/{externalId}`
- Keep old endpoints for backward compatibility: `/api/bike-types/{id}`
- Eventually deprecate and remove old endpoints

### 4. Testing Strategy
- Ensure both ID types work during migration
- Test cross-service communication using externalId
- Verify data consistency between old and new fields

## Conclusion

✅ **All cross-service references are now properly configured with externalId pattern**

The main issue with `BikeType` and `BikeTypeBikeIssue` has been resolved. All other entities either:
1. Already have proper externalId configuration, or
2. Are internal lookup tables that don't need externalId

No further action required for other entities at this time.
