# Shared Contracts Module

**Version**: 1.0.0  
**Purpose**: Single source of truth for cross-service DTOs

---

## Overview

This module contains Data Transfer Objects (DTOs) used for **cross-service communication** between microservices. By centralizing these contracts, we ensure type safety and eliminate code duplication.

---

## Package Structure

```
org.clickenrent.contracts/
├── auth/           - Auth Service contracts (User, Company)
└── rental/         - Rental Service contracts (Rental, Bike, BikeRental, Location)
```

---

## Available Contracts

### Auth Service Contracts (`org.clickenrent.contracts.auth`)

| DTO | Purpose | Fields |
|-----|---------|--------|
| `UserDTO` | User profile data | id, externalId, userName, email, firstName, lastName, phone, city, address, etc. |
| `CompanyDTO` | Company/organization data | id, externalId, name, description, website, logo, erpPartnerId, etc. |

**Owner**: auth-service  
**Consumers**: rental-service, payment-service, support-service

### Rental Service Contracts (`org.clickenrent.contracts.rental`)

| DTO | Purpose | Fields |
|-----|---------|--------|
| `RentalDTO` | Rental transaction | id, externalId, userId, companyId, rentalStatusId, erpRentalOrderId, etc. |
| `BikeDTO` | Bike/vehicle details | id, externalId, code, qrCodeUrl, frameNumber, bikeStatusId, etc. |
| `BikeRentalDTO` | Bike rental details | id, externalId, bikeId, locationId, rentalId, startDateTime, endDateTime, price |
| `LocationDTO` | Location/hub data | id, externalId, name, addressId, companyId, etc. |

**Owner**: rental-service  
**Consumers**: payment-service, support-service

---

## Usage

### 1. Add Dependency

Already configured in parent pom, no version needed:

```xml
<dependency>
    <groupId>org.clickenrent</groupId>
    <artifactId>shared-contracts</artifactId>
</dependency>
```

### 2. Import DTOs

```java
// Auth service contracts
import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.contracts.auth.CompanyDTO;

// Rental service contracts
import org.clickenrent.contracts.rental.RentalDTO;
import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.contracts.rental.LocationDTO;
```

### 3. Use in Feign Clients

```java
@FeignClient(name = "auth-service", path = "/api")
public interface AuthServiceClient {
    
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/companies/external/{externalId}")
    CompanyDTO getCompanyByExternalId(@PathVariable("externalId") String externalId);
}
```

### 4. Use in Services

```java
@Service
public class RentalFinTransactionService {
    
    private final RentalServiceClient rentalServiceClient;
    
    public void createTransaction(RentalFinTransactionDTO dto) {
        // Fetch rental using shared contract
        RentalDTO rental = rentalServiceClient.getRentalById(dto.getRentalId());
        
        // Use rental data
        transaction.setRentalExternalId(rental.getExternalId());
    }
}
```

---

## Development Guidelines

### Adding New Contracts

1. **Create DTO** in appropriate package (auth or rental)
2. **Add Javadoc** with owner and consumers
3. **Use Lombok** annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
4. **Include version** comment: `@version 1.0.0`
5. **Bump module version** if breaking change

### Modifying Existing Contracts

#### ✅ Safe Changes (No Version Bump)
- Add optional fields (with defaults)
- Add Javadoc
- Fix typos in comments

#### ⚠️ Minor Changes (Bump Minor Version: 1.X.0)
- Add new required fields
- Deprecate fields
- Add new DTOs

#### 🚫 Breaking Changes (Bump Major Version: X.0.0)
- Remove fields
- Rename fields
- Change field types
- Remove DTOs

---

## Versioning

Current version: **1.0.0**

### Version Format: MAJOR.MINOR.PATCH

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

### How to Update Version

1. Update `shared-contracts/pom.xml`:
```xml
<version>1.1.0</version>
```

2. Update parent `pom.xml` dependencyManagement:
```xml
<dependency>
    <groupId>org.clickenrent</groupId>
    <artifactId>shared-contracts</artifactId>
    <version>1.1.0</version>
</dependency>
```

3. Rebuild all services:
```bash
mvn clean install
```

---

## Building

### Build Only Shared Contracts
```bash
mvn clean install -pl shared-contracts
```

### Build All Modules
```bash
mvn clean install
```

---

## Best Practices

### DO ✅
- Keep DTOs simple (data only, no logic)
- Use Lombok annotations
- Include externalId field for cross-service references
- Add Javadoc with owner and consumers
- Version changes appropriately

### DON'T ❌
- Add business logic to DTOs
- Use service-specific annotations
- Include sensitive data (passwords, tokens)
- Make breaking changes without major version bump
- Create circular dependencies

---

## Contract Ownership

Each contract has an **owner** service that defines the DTO structure:

- **Auth Service** owns: UserDTO, CompanyDTO
- **Rental Service** owns: RentalDTO, BikeDTO, BikeRentalDTO, LocationDTO

**Owner responsibilities**:
1. Maintain DTO structure
2. Ensure backward compatibility
3. Communicate changes to consumers
4. Version updates appropriately

**Consumer responsibilities**:
1. Handle optional fields gracefully
2. Don't depend on undocumented behavior
3. Update when contract versions change
4. Report issues to owner

---

## Examples

### Full Feign Client Example

```java
package org.clickenrent.paymentservice.client;

import org.clickenrent.contracts.rental.RentalDTO;
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.contracts.rental.BikeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rental-service", path = "/api")
public interface RentalServiceClient {

    @GetMapping("/rentals/{id}")
    RentalDTO getRentalById(@PathVariable("id") Long rentalId);

    @GetMapping("/bike-rentals/{id}")
    BikeRentalDTO getBikeRentalById(@PathVariable("id") Long bikeRentalId);

    @GetMapping("/bikes/external/{externalId}")
    BikeDTO getBikeByExternalId(@PathVariable("externalId") String externalId);
}
```

### Service Usage Example

```java
package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.rental.RentalDTO;
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalFinTransactionService {

    private final RentalServiceClient rentalServiceClient;

    public void processTransaction(Long rentalId) {
        // Use shared contract
        RentalDTO rental = rentalServiceClient.getRentalById(rentalId);
        
        log.info("Processing rental: {}", rental.getExternalId());
        
        // Populate cross-service reference
        String rentalExternalId = rental.getExternalId();
        String userExternalId = rental.getUserExternalId();
        String companyExternalId = rental.getCompanyExternalId();
    }
}
```

---

## Troubleshooting

### Cannot Resolve Symbol 'contracts'

**Problem**: IDE cannot find shared-contracts package  
**Solution**: Build shared-contracts module first:
```bash
mvn clean install -pl shared-contracts
```
Then reload Maven project in IDE.

### Package Does Not Exist

**Problem**: Compilation error: "package org.clickenrent.contracts does not exist"  
**Solution**: 
1. Check shared-contracts is in parent pom modules
2. Check service pom.xml has shared-contracts dependency
3. Run `mvn clean install`

### ClassNotFoundException at Runtime

**Problem**: Runtime error when calling Feign client  
**Solution**: Ensure shared-contracts JAR is in classpath. Check Spring Boot Maven plugin includes it.

---

## Contributing

### Adding a New Contract

1. Create DTO in appropriate package
2. Add complete Javadoc
3. Test with consumers
4. Update this README
5. Bump version if needed

### Template for New DTO

```java
package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for [Entity] entity.
 * Used for cross-service communication.
 * 
 * Source: [owner-service]
 * Consumers: [consumer1-service], [consumer2-service]
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEntityDTO {

    private Long id;
    private String externalId;
    
    // Add fields here
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
```

---

## Support

For questions or issues:
1. Check this README
2. Check main project documentation
3. Contact service owner
4. Create an issue in project repository

---

**Module**: shared-contracts  
**Version**: 1.0.0  
**Last Updated**: December 18, 2024






