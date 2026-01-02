package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Shared contract DTO for Bike entity.
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
public class BikeDTO {

    private Long id;
    private String externalId;
    private String code;
    private String qrCodeUrl;
    private String frameNumber;
    private Long bikeStatusId;
    private Integer batteryLevel;
    private Long lockId;
    private BigDecimal vat;
    private Boolean isVatInclude;
    private Long hubId;
    private Long coordinatesId;
    private Long bikeTypeId;
    private String currencyExternalId;
    private LocalDate inServiceDate;
    private Long bikeModelId;
    private Boolean isB2BRentable;
    private BigDecimal revenueSharePercent;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}







