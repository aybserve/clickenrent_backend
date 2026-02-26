package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for invitation information.
 * Used to return invitation details to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    private String email;
    private String token;
    private Long invitedByUserId;
    private String invitedByUserName;
    private Long companyId;
    private String companyName;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;
    private String invitationLink;

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

