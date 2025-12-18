package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Invitation;
import org.clickenrent.authservice.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Invitation entity.
 * Provides standard CRUD operations and custom queries for managing user invitations.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    /**
     * Find an invitation by its unique token.
     * @param token The invitation token
     * @return Optional containing the invitation if found
     */
    Optional<Invitation> findByToken(String token);
    
    /**
     * Find invitations by email and status.
     * Useful for checking if a pending invitation already exists for an email.
     * @param email The email address
     * @param status The invitation status
     * @return List of matching invitations
     */
    List<Invitation> findByEmailAndStatus(String email, InvitationStatus status);
    
    /**
     * Find all invitations created by a specific user with a given status.
     * @param userId The ID of the user who created the invitations
     * @param status The invitation status
     * @return List of invitations
     */
    List<Invitation> findByInvitedByIdAndStatus(Long userId, InvitationStatus status);
    
    /**
     * Find all invitations for a specific company with a given status.
     * @param companyId The company ID
     * @param status The invitation status
     * @return List of invitations
     */
    List<Invitation> findByCompanyIdAndStatus(Long companyId, InvitationStatus status);
    
    /**
     * Find all invitations created by a specific user.
     * @param userId The ID of the user who created the invitations
     * @return List of all invitations created by the user
     */
    List<Invitation> findByInvitedById(Long userId);
    
    /**
     * Find pending invitation for a specific email and company combination.
     * @param email The email address
     * @param companyId The company ID
     * @param status The invitation status
     * @return Optional containing the invitation if found
     */
    Optional<Invitation> findByEmailAndCompanyIdAndStatus(String email, Long companyId, InvitationStatus status);
}



