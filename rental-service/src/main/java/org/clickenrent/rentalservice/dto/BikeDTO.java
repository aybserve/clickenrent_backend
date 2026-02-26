package org.clickenrent.rentalservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String bikeModelImageUrl;
    
    private Boolean isB2BRentable;
    private BigDecimal revenueSharePercent;

    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}
