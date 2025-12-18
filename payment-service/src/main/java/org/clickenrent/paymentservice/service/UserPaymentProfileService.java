package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.client.AuthServiceClient;
import org.clickenrent.paymentservice.dto.UserDTO;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.UserPaymentProfileMapper;
import org.clickenrent.paymentservice.repository.UserPaymentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for UserPaymentProfile management with Stripe customer integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPaymentProfileService {

    private final UserPaymentProfileRepository userPaymentProfileRepository;
    private final UserPaymentProfileMapper userPaymentProfileMapper;
    private final SecurityService securityService;
    private final StripeService stripeService;
    private final AuthServiceClient authServiceClient;

    @Transactional(readOnly = true)
    public List<UserPaymentProfileDTO> findAll() {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can view all payment profiles");
        }
        return userPaymentProfileMapper.toDTOList(userPaymentProfileRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserPaymentProfileDTO findById(Long id) {
        UserPaymentProfile profile = userPaymentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "id", id));
        
        checkProfileAccess(profile);
        
        return userPaymentProfileMapper.toDTO(profile);
    }

    @Transactional(readOnly = true)
    public UserPaymentProfileDTO findByExternalId(UUID externalId) {
        UserPaymentProfile profile = userPaymentProfileRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "externalId", externalId));
        
        checkProfileAccess(profile);
        
        return userPaymentProfileMapper.toDTO(profile);
    }

    @Transactional(readOnly = true)
    public UserPaymentProfileDTO findByUserId(Long userId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to access this profile");
        }
        
        UserPaymentProfile profile = userPaymentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "userId", userId));
        
        return userPaymentProfileMapper.toDTO(profile);
    }

    @Transactional
    public UserPaymentProfileDTO createOrGetProfile(Long userId) {
        // Check permission
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to create this profile");
        }
        
        // Check if profile already exists
        return userPaymentProfileRepository.findByUserId(userId)
                .map(userPaymentProfileMapper::toDTO)
                .orElseGet(() -> {
                    log.info("Creating payment profile for user: {}", userId);
                    
                    // Fetch user details from auth-service
                    UserDTO user = authServiceClient.getUserById(userId);
                    
                    // Create Stripe customer
                    String stripeCustomerId = stripeService.createCustomer(userId, user.getEmail());
                    
                    // Create profile
                    UserPaymentProfile profile = UserPaymentProfile.builder()
                            .userId(userId)
                            .stripeCustomerId(stripeCustomerId)
                            .isActive(true)
                            .build();
                    
                    UserPaymentProfile savedProfile = userPaymentProfileRepository.save(profile);
                    log.info("Created payment profile: {} with Stripe customer: {}", 
                            savedProfile.getId(), stripeCustomerId);
                    
                    return userPaymentProfileMapper.toDTO(savedProfile);
                });
    }

    @Transactional
    public UserPaymentProfileDTO create(UserPaymentProfileDTO dto) {
        // Validate user exists
        authServiceClient.getUserById(dto.getUserId());
        
        UserPaymentProfile profile = userPaymentProfileMapper.toEntity(dto);
        UserPaymentProfile savedProfile = userPaymentProfileRepository.save(profile);
        return userPaymentProfileMapper.toDTO(savedProfile);
    }

    @Transactional
    public UserPaymentProfileDTO update(Long id, UserPaymentProfileDTO dto) {
        UserPaymentProfile existingProfile = userPaymentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "id", id));
        
        checkProfileAccess(existingProfile);
        
        existingProfile.setIsActive(dto.getIsActive());
        
        UserPaymentProfile updatedProfile = userPaymentProfileRepository.save(existingProfile);
        return userPaymentProfileMapper.toDTO(updatedProfile);
    }

    @Transactional
    public void delete(Long id) {
        UserPaymentProfile profile = userPaymentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "id", id));
        
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete payment profiles");
        }
        
        userPaymentProfileRepository.deleteById(id);
    }

    private void checkProfileAccess(UserPaymentProfile profile) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(profile.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this profile");
        }
    }
}


