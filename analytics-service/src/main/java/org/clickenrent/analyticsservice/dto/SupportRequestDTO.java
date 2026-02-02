package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private Long id;
    private String externalId;
    private String supportRequestStatusName;
    private LocalDateTime dateCreated;
    
    // Additional fields for completeness (not used in analytics)
    private Boolean isNearLocation;
    private String photoUrl;
    private Long errorCodeId;
    private String errorCodeName;
    private Long supportRequestStatusId;
    private String userExternalId;
    private String bikeExternalId;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
