package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.dto.UpdateUserPreferenceRequest;
import org.clickenrent.authservice.dto.UserPreferenceDTO;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserPreference;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserPreferenceMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserPreferenceRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing user preferences with automatic default creation.
 * Handles CRUD operations and default preference generation based on user roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceService {
    
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final UserPreferenceMapper userPreferenceMapper;
    private final LanguageRepository languageRepository;
    
    /**
     * Get user preferences by user ID.
     * Automatically creates default preferences if they don't exist.
     *
     * @param userId the user ID
     * @return UserPreferenceDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserPreferenceDTO getUserPreferences(Long userId) {
        log.debug("Getting preferences for user ID: {}", userId);
        
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Get or create preferences
        UserPreference preferences = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating default preferences for user ID: {}", userId);
                    return createDefaultPreferences(user);
                });
        
        return userPreferenceMapper.toDto(preferences);
    }
    
    /**
     * Get user preferences by user's external ID.
     * Used for cross-service communication.
     *
     * @param externalId the user's external ID
     * @return UserPreferenceDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserPreferenceDTO getUserPreferencesByExternalId(String externalId) {
        log.debug("Getting preferences for user external ID: {}", externalId);
        
        // Check if user exists
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with external ID: " + externalId));
        
        // Get or create preferences
        UserPreference preferences = userPreferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    log.info("Creating default preferences for user external ID: {}", externalId);
                    return createDefaultPreferences(user);
                });
        
        return userPreferenceMapper.toDto(preferences);
    }
    
    /**
     * Update user preferences.
     * Only updates fields that are not null in the request.
     *
     * @param userId the user ID
     * @param request the update request
     * @return updated UserPreferenceDTO
     * @throws ResourceNotFoundException if user or preferences not found
     */
    @Transactional
    public UserPreferenceDTO updateUserPreferences(Long userId, UpdateUserPreferenceRequest request) {
        log.debug("Updating preferences for user ID: {}", userId);
        
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Get or create preferences
        UserPreference preferences = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(user));
        
        // Update only non-null fields
        userPreferenceMapper.updateEntityFromRequest(request, preferences);
        
        // Handle language update separately with validation
        if (request.getLanguageId() != null) {
            Language language = languageRepository.findById(request.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language not found with ID: " + request.getLanguageId()));
            preferences.setLanguage(language);
        }
        
        // Save and return
        UserPreference savedPreferences = userPreferenceRepository.save(preferences);
        log.info("Updated preferences for user ID: {}", userId);
        
        return userPreferenceMapper.toDto(savedPreferences);
    }
    
    /**
     * Reset user preferences to default values.
     *
     * @param userId the user ID
     * @return UserPreferenceDTO with default values
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserPreferenceDTO resetToDefaults(Long userId) {
        log.debug("Resetting preferences to defaults for user ID: {}", userId);
        
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Delete existing preferences if any
        userPreferenceRepository.findByUserId(userId)
                .ifPresent(userPreferenceRepository::delete);
        
        // Create new default preferences
        UserPreference defaultPreferences = createDefaultPreferences(user);
        log.info("Reset preferences to defaults for user ID: {}", userId);
        
        return userPreferenceMapper.toDto(defaultPreferences);
    }
    
    /**
     * Create default preferences for a user.
     * This method is called automatically on user registration or first access.
     *
     * @param user the user entity
     * @return the created UserPreference entity
     */
    @Transactional
    public UserPreference createDefaultPreferences(User user) {
        log.debug("Creating default preferences for user: {}", user.getUserName());
        
        // Build default navigation order based on user roles
        // Note: In a real scenario, you might want to query user roles here
        Map<String, List<String>> defaultNavigationOrder = buildDefaultNavigationOrder();
        
        // Get default language (use user's language or default to English)
        Language defaultLanguage = user.getLanguage();
        if (defaultLanguage == null) {
            defaultLanguage = languageRepository.findById(1L).orElse(null); // Default to English (ID=1)
        }
        
        UserPreference preferences = UserPreference.builder()
                .user(user)
                .navigationOrder(defaultNavigationOrder)
                .theme(UserPreference.Theme.SYSTEM)
                .language(defaultLanguage)
                .timezone("UTC")
                .dateFormat("YYYY-MM-DD")
                .timeFormat(UserPreference.TimeFormat.TWENTY_FOUR_HOUR)
                .currencyExternalId("550e8400-e29b-41d4-a716-446655440021") // USD
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .notificationFrequency(UserPreference.NotificationFrequency.IMMEDIATE)
                .itemsPerPage(20)
                .dashboardLayout(new HashMap<>())
                .tablePreferences(new HashMap<>())
                .defaultFilters(new HashMap<>())
                .build();
        
        UserPreference savedPreferences = userPreferenceRepository.save(preferences);
        log.info("Created default preferences for user: {}", user.getUserName());
        
        return savedPreferences;
    }
    
    /**
     * Check if preferences exist for a user.
     *
     * @param userId the user ID
     * @return true if preferences exist, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean preferencesExist(Long userId) {
        return userPreferenceRepository.existsByUserId(userId);
    }
    
    /**
     * Build default navigation order based on common role structures.
     * This provides a sensible default that covers all role types.
     *
     * @return Map of role names to navigation item lists
     */
    private Map<String, List<String>> buildDefaultNavigationOrder() {
        Map<String, List<String>> navigationOrder = new HashMap<>();
        
        // SUPERADMIN navigation
        navigationOrder.put("superadmin", List.of(
                "dashboard",
                "companies",
                "locations",
                "bikes",
                "users",
                "bikeRentals",
                "analytics",
                "push-notifications",
                "legal-documents"
        ));
        
        // ADMIN navigation
        navigationOrder.put("admin", List.of(
                "dashboard",
                "users",
                "locations",
                "bikes",
                "bikeRentals",
                "analytics"
        ));
        
        // B2B navigation
        navigationOrder.put("b2b", List.of(
                "dashboard",
                "bikes",
                "bikeRentals",
                "analytics"
        ));
        
        // CUSTOMER navigation
        navigationOrder.put("customer", List.of(
                "rentals",
                "bikes",
                "profile",
                "payment-methods"
        ));
        
        return navigationOrder;
    }
}
