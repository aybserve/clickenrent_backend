package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.dto.SwaggerAccessRequest;
import org.clickenrent.authservice.dto.SwaggerAccessResponse;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for validating Swagger access permissions.
 * Validates user credentials and checks if they have required global roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerAccessService {
    
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    
    /**
     * Roles that are allowed to access Swagger documentation.
     */
    private static final Set<String> ALLOWED_ROLES = Set.of("SUPERADMIN", "ADMIN", "DEV");
    
    /**
     * Validate user credentials and check if they have access to Swagger.
     * 
     * @param request SwaggerAccessRequest containing username/email and password
     * @return SwaggerAccessResponse indicating access status and roles
     */
    @Transactional(readOnly = true)
    public SwaggerAccessResponse validateSwaggerAccess(SwaggerAccessRequest request) {
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            
            // Load user entity to get global roles
            User user = userDetailsService.loadUserEntityByUsername(request.getUsernameOrEmail());
            
            // Get user's global roles
            List<UserGlobalRole> userGlobalRoles = userGlobalRoleRepository.findByUser(user);
            List<String> globalRoles = userGlobalRoles.stream()
                    .map(ugr -> ugr.getGlobalRole().getName())
                    .collect(Collectors.toList());
            
            // Check if user has any of the allowed roles
            boolean hasAccess = globalRoles.stream()
                    .anyMatch(role -> ALLOWED_ROLES.contains(role.toUpperCase()));
            
            log.info("Swagger access validation for user '{}': hasAccess={}, roles={}", 
                    user.getUserName(), hasAccess, globalRoles);
            
            return SwaggerAccessResponse.builder()
                    .hasAccess(hasAccess)
                    .roles(globalRoles)
                    .username(user.getUserName())
                    .build();
                    
        } catch (BadCredentialsException e) {
            log.warn("Failed Swagger access attempt with invalid credentials for: {}", 
                    request.getUsernameOrEmail());
            
            return SwaggerAccessResponse.builder()
                    .hasAccess(false)
                    .roles(List.of())
                    .username(null)
                    .build();
        }
    }
}
