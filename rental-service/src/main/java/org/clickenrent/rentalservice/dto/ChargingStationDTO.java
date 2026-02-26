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
 * Data Transfer Object for ChargingStation entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    private String code;
    private String qrCodeUrl;
    private BigDecimal vat;
    private Boolean isVatInclude;
    private Long chargingStationStatusId;
    private Long coordinatesId;
    private Boolean isActive;
    private Long hubId;
    private LocalDate inServiceDate;
    private Long chargingStationModelId;
    private Boolean isB2BRentable;

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
