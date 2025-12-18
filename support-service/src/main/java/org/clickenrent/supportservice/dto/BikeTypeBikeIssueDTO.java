package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BikeTypeBikeIssue junction entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeTypeBikeIssueDTO {

    private Long id;
    private Long bikeTypeId;
    private Long bikeIssueId;
    private String bikeIssueName;
}


