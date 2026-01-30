package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Simplified user DTO for analytics purposes.
 * Ignores extra fields from the full UserDTO when deserializing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSimpleDTO {
    
    private Long id;
    private String externalId;
    private Boolean isActive;
    private Long languageId;
    private String languageCode;
    private LocalDateTime dateCreated;
}
