package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for ChargingStation entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationDTO {

    private Long id;
    private String externalId;
    private Long chargingStationStatusId;
    private Long coordinatesId;
    private Boolean isActive;
    private Long hubId;
    private LocalDate inServiceDate;
    private Long chargingStationModelId;
    private Boolean isB2BRentable;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
