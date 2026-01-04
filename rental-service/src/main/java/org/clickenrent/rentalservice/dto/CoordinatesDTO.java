package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Coordinates entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDTO {

    private Long id;
    private String externalId;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








