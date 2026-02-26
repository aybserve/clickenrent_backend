package org.clickenrent.rentalservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    private String name;
    private Long chargingStationBrandId;
    private String imageUrl;
    private BigDecimal b2bSalePrice;
    private BigDecimal b2bSubscriptionPrice;

    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private java.time.LocalDateTime dateCreated;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private java.time.LocalDateTime lastDateModified;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}
