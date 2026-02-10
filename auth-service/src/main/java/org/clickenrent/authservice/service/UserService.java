package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.client.SearchServiceClient;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing User entities with role-based access control.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final UserCompanyRepository userCompanyRepository;
    private final SearchServiceClient searchServiceClient;
    private final UserPreferenceService userPreferenceService;
    
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        // Admin can see all users
        if (securityService.isAdmin()) {
            return userRepository.findAll(pageable)
                    .map(userMapper::toDto);
        }
        
        // B2B can see users in their companies
        if (securityService.isB2B()) {
            List<Long> accessibleCompanyIds = securityService.getAccessibleCompanyIds();
            if (accessibleCompanyIds == null || accessibleCompanyIds.isEmpty()) {
                return Page.empty(pageable);
            }
            
            // Get all users in the accessible companies
            Set<Long> userIds = userCompanyRepository.findAll().stream()
                    .filter(uc -> accessibleCompanyIds.contains(uc.getCompany().getId()))
                    .map(uc -> uc.getUser().getId())
                    .collect(Collectors.toSet());
            
            List<UserDTO> users = userRepository.findAll().stream()
                    .filter(user -> userIds.contains(user.getId()))
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
            
            // Apply pagination manually
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), users.size());
            List<UserDTO> pageContent = users.subList(start, end);
            
            return new PageImpl<>(pageContent, pageable, users.size());
        }
        
        // Customer can only see themselves
        if (securityService.isCustomer()) {
            Long currentUserId = securityService.getCurrentUserId();
            if (currentUserId == null) {
                return Page.empty(pageable);
            }
            
            User user = userRepository.findById(currentUserId)
                    .orElse(null);
            if (user == null) {
                return Page.empty(pageable);
            }
            
            return new PageImpl<>(List.of(userMapper.toDto(user)), pageable, 1);
        }
        
        throw new UnauthorizedException("You don't have permission to view users");
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        // Check if user has access to view this user
        if (!securityService.hasAccessToUser(id)) {
            throw new UnauthorizedException("You don't have permission to view this user");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDto(user);
    }

    /**
     * Find user by externalId for cross-service communication
     */
    @Transactional(readOnly = true)
    public UserDTO findByExternalId(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "externalId", externalId));
        return userMapper.toDto(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByExternalId(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "externalId", externalId));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO createUser(UserDTO userDTO, String password) {
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(password));
        user.sanitizeForCreate();
        
        if (userDTO.getLanguageId() != null) {
            Language language = languageRepository.findById(userDTO.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", userDTO.getLanguageId()));
            user.setLanguage(language);
        }
        
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getIsDeleted() == null) {
            user.setIsDeleted(false);
        }
        
        user = userRepository.save(user);
        
        // Create default preferences for the new user
        userPreferenceService.createDefaultPreferences(user);
        log.info("Created default preferences for new user: {}", user.getUserName());
        
        // Notify search-service for indexing
        notifySearchService("user", user.getExternalId(), "CREATE");
        
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // Admin can update anyone, others can only update themselves
        if (!securityService.isAdmin() && !securityService.getCurrentUserId().equals(id)) {
            throw new UnauthorizedException("You can only update your own profile");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userMapper.updateEntityFromDto(userDTO, user);
        
        if (userDTO.getLanguageId() != null) {
            Language language = languageRepository.findById(userDTO.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", userDTO.getLanguageId()));
            user.setLanguage(language);
        }
        
        user = userRepository.save(user);
        
        // Notify search-service for indexing
        notifySearchService("user", user.getExternalId(), "UPDATE");
        
        return userMapper.toDto(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        String externalId = user.getExternalId();
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
        
        // Notify search-service for indexing
        notifySearchService("user", externalId, "DELETE");
    }
    
    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(true);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
    
    /**
     * Notify search-service of entity changes (async, fail-safe)
     * This method never throws exceptions to prevent search failures from breaking user operations
     */
    private void notifySearchService(String entityType, String entityId, String operation) {
        try {
            searchServiceClient.notifyIndexEvent(
                IndexEventRequest.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .operation(IndexEventRequest.IndexOperation.valueOf(operation))
                    .build()
            );
            log.debug("Notified search-service: {} {} {}", operation, entityType, entityId);
        } catch (Exception e) {
            // Don't fail the main operation if search indexing fails
            log.warn("Failed to notify search-service for {} {} {}: {}", 
                     operation, entityType, entityId, e.getMessage());
        }
    }
}

