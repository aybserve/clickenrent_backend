package org.clickenrent.authservice.entity;

/**
 * Enum representing the status of a user invitation.
 * Used to track the lifecycle of invitations from creation to completion or cancellation.
 */
public enum InvitationStatus {
    
    /**
     * Invitation has been created and sent, waiting for user to accept.
     */
    PENDING,
    
    /**
     * User has completed registration using the invitation token.
     */
    ACCEPTED,
    
    /**
     * Invitation token has expired (after 7 days by default).
     */
    EXPIRED,
    
    /**
     * Invitation has been cancelled by the inviter or admin.
     */
    CANCELLED
}








