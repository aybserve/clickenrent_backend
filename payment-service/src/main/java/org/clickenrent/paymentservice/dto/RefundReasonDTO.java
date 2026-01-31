package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for RefundReason entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundReasonDTO {
    
    private Long id;
    
    private String externalId;
    
    private String code;
    
    private String name;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
