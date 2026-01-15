package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.UserPaymentMethodDTO;
import org.clickenrent.paymentservice.entity.UserPaymentMethod;
import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.UserPaymentMethodMapper;
import org.clickenrent.paymentservice.repository.UserPaymentMethodRepository;
import org.clickenrent.paymentservice.repository.UserPaymentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for UserPaymentMethod management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPaymentMethodService {

    private final UserPaymentMethodRepository userPaymentMethodRepository;
    private final UserPaymentProfileRepository userPaymentProfileRepository;
    private final UserPaymentMethodMapper userPaymentMethodMapper;
    private final SecurityService securityService;
    private final PaymentProviderService paymentProviderService;

    @Transactional(readOnly = true)
    public List<UserPaymentMethodDTO> findAll() {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can view all payment methods");
        }
        return userPaymentMethodMapper.toDTOList(userPaymentMethodRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserPaymentMethodDTO findById(Long id) {
        UserPaymentMethod method = userPaymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentMethod", "id", id));
        
        checkMethodAccess(method);
        
        return userPaymentMethodMapper.toDTO(method);
    }

    @Transactional(readOnly = true)
    public UserPaymentMethodDTO findByExternalId(String externalId) {
        UserPaymentMethod method = userPaymentMethodRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentMethod", "externalId", externalId));
        
        checkMethodAccess(method);
        
        return userPaymentMethodMapper.toDTO(method);
    }

    @Transactional(readOnly = true)
    public List<UserPaymentMethodDTO> findByUserExternalId(String userExternalId) {
        // Only admins can access payment methods for now
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to access these payment methods");
        }
        
        UserPaymentProfile profile = userPaymentProfileRepository.findByUserExternalId(userExternalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "userExternalId", userExternalId));
        
        List<UserPaymentMethod> methods = userPaymentMethodRepository.findByUserPaymentProfileId(profile.getId());
        return userPaymentMethodMapper.toDTOList(methods);
    }

    @Transactional
    public UserPaymentMethodDTO attachPaymentMethod(Long profileId, String stripePaymentMethodId) {
        UserPaymentProfile profile = userPaymentProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentProfile", "id", profileId));
        
        // Only admins can attach payment methods for now
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to attach payment methods to this profile");
        }
        
        log.info("Attaching payment method: {} to profile: {}", stripePaymentMethodId, profileId);
        
        // Get customer ID based on active provider
        String customerId = paymentProviderService.isStripeActive() 
                ? profile.getStripeCustomerId() 
                : profile.getMultiSafepayCustomerId();
        
        // Attach payment method to customer
        paymentProviderService.attachPaymentMethod(stripePaymentMethodId, customerId);
        
        // Create payment method record
        UserPaymentMethod.UserPaymentMethodBuilder methodBuilder = UserPaymentMethod.builder()
                .userPaymentProfile(profile)
                .isDefault(false)
                .isActive(true);
        
        // Set provider-specific payment method ID
        if (paymentProviderService.isStripeActive()) {
            methodBuilder.stripePaymentMethodId(stripePaymentMethodId);
        } else if (paymentProviderService.isMultiSafepayActive()) {
            methodBuilder.multiSafepayTokenId(stripePaymentMethodId);
        }
        
        UserPaymentMethod method = methodBuilder.build();
        
        UserPaymentMethod savedMethod = userPaymentMethodRepository.save(method);
        log.info("Payment method attached successfully: {}", savedMethod.getId());
        
        return userPaymentMethodMapper.toDTO(savedMethod);
    }

    @Transactional
    public UserPaymentMethodDTO setDefaultPaymentMethod(Long id) {
        UserPaymentMethod method = userPaymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentMethod", "id", id));
        
        checkMethodAccess(method);
        
        // Remove default flag from other methods
        List<UserPaymentMethod> profileMethods = userPaymentMethodRepository
                .findByUserPaymentProfileId(method.getUserPaymentProfile().getId());
        
        profileMethods.forEach(m -> {
            if (!m.getId().equals(id)) {
                m.setIsDefault(false);
                userPaymentMethodRepository.save(m);
            }
        });
        
        // Set this method as default
        method.setIsDefault(true);
        UserPaymentMethod updatedMethod = userPaymentMethodRepository.save(method);
        
        log.info("Set payment method: {} as default", id);
        
        return userPaymentMethodMapper.toDTO(updatedMethod);
    }

    @Transactional
    public UserPaymentMethodDTO create(UserPaymentMethodDTO dto) {
        UserPaymentMethod method = userPaymentMethodMapper.toEntity(dto);
        UserPaymentMethod savedMethod = userPaymentMethodRepository.save(method);
        return userPaymentMethodMapper.toDTO(savedMethod);
    }

    @Transactional
    public UserPaymentMethodDTO update(Long id, UserPaymentMethodDTO dto) {
        UserPaymentMethod existingMethod = userPaymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentMethod", "id", id));
        
        checkMethodAccess(existingMethod);
        
        existingMethod.setIsActive(dto.getIsActive());
        
        UserPaymentMethod updatedMethod = userPaymentMethodRepository.save(existingMethod);
        return userPaymentMethodMapper.toDTO(updatedMethod);
    }

    @Transactional
    public void delete(Long id) {
        UserPaymentMethod method = userPaymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPaymentMethod", "id", id));
        
        checkMethodAccess(method);
        
        userPaymentMethodRepository.deleteById(id);
    }

    private void checkMethodAccess(UserPaymentMethod method) {
        // Only admins can access payment methods for now
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to access this payment method");
        }
    }
}




