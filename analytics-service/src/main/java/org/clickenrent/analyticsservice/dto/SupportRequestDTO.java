package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Simplified DTO for support request data from support-service.
 * Contains only the fields needed for analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportRequestDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private String supportRequestStatusName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    
    // Additional fields for completeness (not used in analytics)
    private Boolean isNearLocation;
    private String photoUrl;
    private Long errorCodeId;
    private String errorCodeName;
    private Long supportRequestStatusId;
    private String userExternalId;
    private String bikeExternalId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}
