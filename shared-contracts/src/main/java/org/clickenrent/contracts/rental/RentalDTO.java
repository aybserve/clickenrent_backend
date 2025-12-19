package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for Rental entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: payment-service, support-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {

    private Long id;
    private String externalId;
    private Long userId;
    private Long companyId;
    private Long rentalStatusId;
    private String erpRentalOrderId;

    // Cross-service externalId references
    private String userExternalId;
    private String companyExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
