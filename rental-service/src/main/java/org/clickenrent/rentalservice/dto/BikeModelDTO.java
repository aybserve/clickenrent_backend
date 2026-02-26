package org.clickenrent.rentalservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for BikeModel entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeModelDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    private String name;
    private Long bikeBrandId;
    private String imageUrl;
    private Long bikeTypeId;
    private Long bikeEngineId;
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
