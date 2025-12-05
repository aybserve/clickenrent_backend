package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Bike entity.
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
    private Long batteryChargeStatusId;
    private Long lockId;
    private BigDecimal vat;
    private Boolean isVatInclude;
    private Long hubId;
    private Long coordinatesId;
    private Long bikeTypeId;
    private Long currencyId;
    private BigDecimal costPerDay;
    private BigDecimal costPerHour;
    private BigDecimal costPerWeek;
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
