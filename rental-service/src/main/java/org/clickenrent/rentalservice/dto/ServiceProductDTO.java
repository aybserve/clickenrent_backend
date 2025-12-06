package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ServiceProduct entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductDTO {

    private Long id;
    private String externalId;
    private Long serviceId;
    private Long productId;
    private Boolean isB2BRentable;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
