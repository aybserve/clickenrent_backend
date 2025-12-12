package org.clickenrent.authservice.dto;

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
    
    private Long id;
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
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

