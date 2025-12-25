package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.CompleteInvitationRequest;
import org.clickenrent.authservice.dto.CreateInvitationRequest;
import org.clickenrent.authservice.dto.InvitationDTO;
import org.clickenrent.authservice.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing B2B user invitations.
 * Handles invitation creation, validation, and completion.
 */
@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "B2B user invitation management endpoints")
public class InvitationController {
    
    private final InvitationService invitationService;
    
    /**
     * Create a new invitation to join a company.
     * POST /api/invitations
     * 
     * Security: SUPERADMIN, ADMIN, or B2B users can create invitations.
     * Service layer validates that user can invite to the specified company.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(
            summary = "Create invitation",
            description = "Create a new invitation for a user to join a company as B2B with Staff role. " +
                         "SUPERADMIN/ADMIN can invite to any company. B2B users can invite to their own companies."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invitation created successfully",
                    content = @Content(schema = @Schema(implementation = InvitationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions to invite to this company"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "409", description = "Email already registered or pending invitation exists")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<InvitationDTO> createInvitation(@Valid @RequestBody CreateInvitationRequest request) {
        InvitationDTO invitation = invitationService.createInvitation(request);
        return new ResponseEntity<>(invitation, HttpStatus.CREATED);
    }
    
    /**
     * Validate an invitation token.
     * GET /api/invitations/validate/{token}
     * 
     * Security: Public endpoint (no authentication required).
     * Used by frontend to check if invitation is valid before showing registration form.
     */
    @GetMapping("/validate/{token}")
    @Operation(
            summary = "Validate invitation token",
            description = "Check if an invitation token is valid and not expired. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = InvitationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<InvitationDTO> validateToken(@PathVariable String token) {
        InvitationDTO invitation = invitationService.validateToken(token);
        return ResponseEntity.ok(invitation);
    }
    
    /**
     * Complete invitation by registering the user.
     * POST /api/invitations/complete
     * 
     * Security: Public endpoint (no authentication required).
     * Creates user account with B2B role and assigns to company with Staff role.
     */
    @PostMapping("/complete")
    @Operation(
            summary = "Complete invitation registration",
            description = "Complete the invitation by creating user account. " +
                         "User will be assigned B2B global role and Staff role in the company. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration completed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token or input data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AuthResponse> completeInvitation(@Valid @RequestBody CompleteInvitationRequest request) {
        AuthResponse response = invitationService.completeInvitation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get all invitations accessible to current user.
     * GET /api/invitations
     * 
     * Security: SUPERADMIN/ADMIN see all invitations, B2B users see only their own.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(
            summary = "Get invitations",
            description = "Get all invitations. SUPERADMIN/ADMIN see all, B2B users see only invitations they created."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<InvitationDTO>> getAllInvitations() {
        List<InvitationDTO> invitations = invitationService.getAllInvitations();
        return ResponseEntity.ok(invitations);
    }
    
    /**
     * Cancel an invitation.
     * DELETE /api/invitations/{id}
     * 
     * Security: SUPERADMIN/ADMIN can cancel any, B2B users can cancel their own.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(
            summary = "Cancel invitation",
            description = "Cancel a pending invitation. SUPERADMIN/ADMIN can cancel any, B2B users can cancel their own."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Invitation cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions to cancel this invitation"),
            @ApiResponse(responseCode = "404", description = "Invitation not found"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel invitation with current status")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> cancelInvitation(@PathVariable Long id) {
        invitationService.cancelInvitation(id);
        return ResponseEntity.noContent().build();
    }
}







