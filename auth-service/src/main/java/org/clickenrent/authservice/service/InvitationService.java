package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.CompleteInvitationRequest;
import org.clickenrent.authservice.dto.CreateInvitationRequest;
import org.clickenrent.authservice.dto.InvitationDTO;
import org.clickenrent.authservice.entity.*;
import org.clickenrent.authservice.exception.DuplicateResourceException;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.InvitationMapper;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing B2B user invitations.
 * Handles creation, validation, and completion of invitations.
 */
@Service
@RequiredArgsConstructor
public class InvitationService {
    
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final GlobalRoleRepository globalRoleRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    private final SecurityService securityService;
    private final InvitationMapper invitationMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    
    // Default invitation expiration: 7 days
    private static final int INVITATION_EXPIRATION_DAYS = 7;
    
    /**
     * Create a new invitation to join a company.
     * @param request The invitation request with email and company ID
     * @return InvitationDTO with invitation details and shareable link
     */
    @Transactional
    public InvitationDTO createInvitation(CreateInvitationRequest request) {
        // Check if current user can invite to this company
        if (!securityService.canInviteToCompany(request.getCompanyId())) {
            throw new AccessDeniedException("You don't have permission to invite users to this company");
        }
        
        // Check if email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        
        // Check if company exists
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
        
        // Check if pending invitation already exists for this email and company
        invitationRepository.findByEmailAndCompanyIdAndStatus(
                request.getEmail(), 
                request.getCompanyId(), 
                InvitationStatus.PENDING
        ).ifPresent(existing -> {
            throw new DuplicateResourceException(
                    "Invitation", 
                    "email and company", 
                    request.getEmail() + " to " + company.getName()
            );
        });
        
        // Get current user as inviter
        User inviter = securityService.getCurrentUser();
        if (inviter == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        // Create invitation
        Invitation invitation = Invitation.builder()
                .email(request.getEmail())
                .token(UUID.randomUUID().toString())
                .invitedBy(inviter)
                .company(company)
                .status(InvitationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(INVITATION_EXPIRATION_DAYS))
                .build();
        
        invitation = invitationRepository.save(invitation);
        
        return invitationMapper.toDto(invitation);
    }
    
    /**
     * Validate an invitation token.
     * @param token The invitation token
     * @return InvitationDTO if valid
     * @throws IllegalArgumentException if token is invalid or expired
     */
    @Transactional(readOnly = true)
    public InvitationDTO validateToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));
        
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation has already been " + invitation.getStatus().name().toLowerCase());
        }
        
        if (invitation.isExpired()) {
            throw new IllegalArgumentException("Invitation has expired");
        }
        
        return invitationMapper.toDto(invitation);
    }
    
    /**
     * Complete the invitation by creating the user account.
     * @param request The registration details with token
     * @return AuthResponse with JWT tokens
     */
    @Transactional
    public AuthResponse completeInvitation(CompleteInvitationRequest request) {
        // Validate token
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));
        
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation has already been " + invitation.getStatus().name().toLowerCase());
        }
        
        if (invitation.isExpired()) {
            throw new IllegalArgumentException("Invitation has expired");
        }
        
        // Check if username already exists
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new DuplicateResourceException("User", "username", request.getUserName());
        }
        
        // Check if email is already registered (shouldn't happen, but double-check)
        if (userRepository.findByEmail(invitation.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", invitation.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(request.getUserName())
                .email(invitation.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .isEmailVerified(true)  // Email verified via invitation token
                .build();
        
        // Set language if provided
        if (request.getLanguageId() != null) {
            Language language = languageRepository.findById(request.getLanguageId())
                    .orElse(null);
            user.setLanguage(language);
        }
        
        user = userRepository.save(user);
        
        // Assign B2B global role
        GlobalRole b2bRole = globalRoleRepository.findByNameIgnoreCase("B2B")
                .orElseThrow(() -> new ResourceNotFoundException("GlobalRole", "name", "B2B"));
        
        UserGlobalRole userGlobalRole = UserGlobalRole.builder()
                .user(user)
                .globalRole(b2bRole)
                .build();
        userGlobalRoleRepository.save(userGlobalRole);
        
        // Assign to company with Staff role
        CompanyRole staffRole = companyRoleRepository.findByNameIgnoreCase("Staff")
                .orElseThrow(() -> new ResourceNotFoundException("CompanyRole", "name", "Staff"));
        
        UserCompany userCompany = UserCompany.builder()
                .user(user)
                .company(invitation.getCompany())
                .companyRole(staffRole)
                .build();
        userCompanyRepository.save(userCompany);
        
        // Mark invitation as accepted
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        
        // Generate JWT tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        String accessToken = jwtService.generateToken(claims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationTime(),
                userMapper.toDto(user)
        );
    }
    
    /**
     * Get all invitations accessible to the current user.
     * @return List of invitations
     */
    @Transactional(readOnly = true)
    public List<InvitationDTO> getAllInvitations() {
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        List<Invitation> invitations;
        
        if (securityService.isAdmin()) {
            // SUPERADMIN/ADMIN can see all invitations
            invitations = invitationRepository.findAll();
        } else {
            // B2B users can only see invitations they created
            invitations = invitationRepository.findByInvitedById(currentUser.getId());
        }
        
        return invitations.stream()
                .map(invitationMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Cancel an invitation.
     * @param invitationId The invitation ID to cancel
     */
    @Transactional
    public void cancelInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", invitationId));
        
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        // Check permission: SUPERADMIN/ADMIN or original inviter
        if (!securityService.isAdmin() && !invitation.getInvitedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to cancel this invitation");
        }
        
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel invitation with status: " + invitation.getStatus());
        }
        
        invitation.setStatus(InvitationStatus.CANCELLED);
        invitationRepository.save(invitation);
    }
    
    private final LanguageRepository languageRepository;
}

