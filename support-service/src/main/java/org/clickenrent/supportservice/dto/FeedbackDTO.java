package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Feedback entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    private Long id;
    private String externalId;
    private Long userId;
    private Integer rate;
    private String comment;
    private LocalDateTime dateTime;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
