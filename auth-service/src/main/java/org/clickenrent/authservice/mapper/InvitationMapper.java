package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.InvitationDTO;
import org.clickenrent.authservice.entity.Invitation;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Invitation entities and DTOs.
 */
@Component
public class InvitationMapper {
    
    // Base URL for invitation links - in production this should come from configuration
    private static final String FRONTEND_BASE_URL = "https://yourfrontend.com";
    
    /**
     * Convert Invitation entity to InvitationDTO.
     * @param invitation The invitation entity
     * @return InvitationDTO with all fields mapped
     */
    public InvitationDTO toDto(Invitation invitation) {
        if (invitation == null) {
            return null;
        }
        
        return InvitationDTO.builder()
                .id(invitation.getId())
                .email(invitation.getEmail())
                .token(invitation.getToken())
                .invitedByUserId(invitation.getInvitedBy().getId())
                .invitedByUserName(invitation.getInvitedBy().getUserName())
                .companyId(invitation.getCompany().getId())
                .companyName(invitation.getCompany().getName())
                .status(invitation.getStatus().name())
                .expiresAt(invitation.getExpiresAt())
                .createdAt(invitation.getCreatedAt())
                .acceptedAt(invitation.getAcceptedAt())
                .invitationLink(buildInvitationLink(invitation.getToken()))
                .build();
    }
    
    /**
     * Build the full invitation link URL.
     * @param token The invitation token
     * @return Full URL for the invitation
     */
    private String buildInvitationLink(String token) {
        return FRONTEND_BASE_URL + "/register?token=" + token;
    }
}

