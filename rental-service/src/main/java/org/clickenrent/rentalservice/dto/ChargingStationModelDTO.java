package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for ChargingStationModel entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationModelDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long chargingStationBrandId;
    private String imageUrl;
    private BigDecimal b2bSalePrice;
    private BigDecimal b2bSubscriptionPrice;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
