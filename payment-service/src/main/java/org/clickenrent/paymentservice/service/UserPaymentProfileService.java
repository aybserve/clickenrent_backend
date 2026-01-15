package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.UserPaymentProfileMapper;
import org.clickenrent.paymentservice.repository.UserPaymentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final PaymentProviderService paymentProviderService;

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
    public UserPaymentProfileDTO findByExternalId(String externalId) {
        UserPaymentProfile profile = userPaymentProfileRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "externalId", externalId));
        
        checkProfileAccess(profile);
        
        return userPaymentProfileMapper.toDTO(profile);
    }

    @Transactional(readOnly = true)
    public UserPaymentProfileDTO findByUserExternalId(String userExternalId) {
        // Check permission - only admins for now
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to access this profile");
        }
        
        UserPaymentProfile profile = userPaymentProfileRepository.findByUserExternalId(userExternalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "userExternalId", userExternalId));
        
        return userPaymentProfileMapper.toDTO(profile);
    }

    @Transactional
    public UserPaymentProfileDTO createOrGetProfile(String userExternalId, String userEmail) {
        // Check permission - only admins for now
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create this profile");
        }
        
        // Check if profile already exists
        return userPaymentProfileRepository.findByUserExternalId(userExternalId)
                .map(userPaymentProfileMapper::toDTO)
                .orElseGet(() -> {
                    log.info("Creating payment profile for user with externalId: {}", userExternalId);
                    
                    // Create customer in active payment provider
                    String customerId = paymentProviderService.createCustomer(userExternalId, userEmail);
                    
                    // Create profile with provider-specific customer ID
                    UserPaymentProfile.UserPaymentProfileBuilder profileBuilder = UserPaymentProfile.builder()
                            .userExternalId(userExternalId)
                            .isActive(true);
                    
                    // Set the appropriate customer ID based on active provider
                    if (paymentProviderService.isStripeActive()) {
                        profileBuilder.stripeCustomerId(customerId);
                    } else if (paymentProviderService.isMultiSafepayActive()) {
                        profileBuilder.multiSafepayCustomerId(customerId);
                    }
                    
                    UserPaymentProfile profile = profileBuilder.build();
                    UserPaymentProfile savedProfile = userPaymentProfileRepository.save(profile);
                    
                    log.info("Created payment profile: {} with {} customer: {}", 
                            savedProfile.getId(), paymentProviderService.getActiveProvider(), customerId);
                    
                    return userPaymentProfileMapper.toDTO(savedProfile);
                });
    }

    @Transactional
    public UserPaymentProfileDTO create(UserPaymentProfileDTO dto) {
        UserPaymentProfile profile = userPaymentProfileMapper.toEntity(dto);
        
        // External ID is provided directly in the DTO
        log.debug("Creating payment profile with userExternalId: {}", dto.getUserExternalId());
        
        if (dto.getUserExternalId() == null) {
            throw new IllegalArgumentException("User external ID is required");
        }
        
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
        // Only admins can access profiles for now (since we're using externalIds)
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to access this profile");
        }
    }
}




