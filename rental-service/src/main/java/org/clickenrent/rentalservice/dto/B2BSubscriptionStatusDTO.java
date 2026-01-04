package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for B2BSubscriptionStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionStatusDTO {

    private Long id;
    private String externalId;
    private String name;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








