package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.exception.DuplicateResourceException;
import org.clickenrent.authservice.exception.InvalidTokenException;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling authentication operations.
 * Includes registration, login, token refresh, and logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final GlobalRoleRepository globalRoleRepository;
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailVerificationService emailVerificationService;
    private final UserPreferenceService userPreferenceService;
    
    /**
     * Build JWT claims with user information including company associations.
     * This method centralizes claim building to ensure consistency across all token generation points.
     * 
     * @param user The user entity
     * @param userDetails The Spring Security UserDetails
     * @return Map of JWT claims
     */
    private Map<String, Object> buildJwtClaims(User user, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("userExternalId", user.getExternalId());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        // Get user's companies
        List<UserCompany> userCompanies = userCompanyRepository.findByUserId(user.getId());
        claims.put("companyIds", userCompanies.stream()
                .map(uc -> uc.getCompany().getId())
                .collect(Collectors.toList()));
        claims.put("companyExternalIds", userCompanies.stream()
                .map(uc -> uc.getCompany().getExternalId())
                .collect(Collectors.toList()));
        
        return claims;
    }
    
    /**
     * Register a new user in the system.
     * Public endpoint - automatically assigns CUSTOMER role during registration.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new DuplicateResourceException("User", "username", request.getUserName());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .isDeleted(false)
                .build();
        
        // Set language if provided
        if (request.getLanguageId() != null) {
            Language language = languageRepository.findById(request.getLanguageId())
                    .orElse(null);
            user.setLanguage(language);
        }
        
        User savedUser = userRepository.save(user);
        
        // Automatically assign CUSTOMER role to new user
        globalRoleRepository.findByNameIgnoreCase("CUSTOMER").ifPresent(customerRole -> {
            UserGlobalRole userGlobalRole = UserGlobalRole.builder()
                    .user(savedUser)
                    .globalRole(customerRole)
                    .build();
            userGlobalRoleRepository.save(userGlobalRole);
        });
        
        // Create default preferences for the new user
        userPreferenceService.createDefaultPreferences(savedUser);
        log.info("Created default preferences for new user: {}", savedUser.getUserName());
        
        // Generate and send email verification code
        emailVerificationService.generateAndSendCode(savedUser);
        
        // Generate tokens with custom claims
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUserName());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", savedUser.getId());
        claims.put("email", savedUser.getEmail());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        String accessToken = jwtService.generateToken(claims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationTime(),
                userMapper.toDto(savedUser)
        );
    }
    
    /**
     * Register a new user with pre-assigned global roles.
     * This endpoint is restricted to SUPERADMIN only and is used to create privileged users (ADMIN, B2B, etc.).
     * 
     * @param request AdminRegisterRequest containing user details and role IDs
     * @return UserDTO of the created user (no tokens since SUPERADMIN is creating the account)
     */
    @Transactional
    public UserDTO registerAdmin(AdminRegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new DuplicateResourceException("User", "username", request.getUserName());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        
        // Validate that all provided role IDs exist
        for (Long roleId : request.getGlobalRoleIds()) {
            if (!globalRoleRepository.existsById(roleId)) {
                throw new ResourceNotFoundException("GlobalRole", "id", roleId);
            }
        }
        
        // Create new user
        User user = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .isDeleted(false)
                .build();
        
        // Set language if provided
        if (request.getLanguageId() != null) {
            Language language = languageRepository.findById(request.getLanguageId())
                    .orElse(null);
            user.setLanguage(language);
        }
        
        user = userRepository.save(user);
        
        // Assign global roles to the user
        for (Long roleId : request.getGlobalRoleIds()) {
            GlobalRole globalRole = globalRoleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("GlobalRole", "id", roleId));
            
            UserGlobalRole userGlobalRole = UserGlobalRole.builder()
                    .user(user)
                    .globalRole(globalRole)
                    .build();
            
            userGlobalRoleRepository.save(userGlobalRole);
        }
        
        // Create default preferences for the new admin user
        userPreferenceService.createDefaultPreferences(user);
        log.info("Created default preferences for new admin user: {}", user.getUserName());
        
        return userMapper.toDto(user);
    }
    
    /**
     * Authenticate user and generate JWT tokens.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
        
        // Load user details
        User user = userDetailsService.loadUserEntityByUsername(request.getUsernameOrEmail());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsernameOrEmail());
        
        // Generate tokens with custom claims (including company data)
        Map<String, Object> claims = buildJwtClaims(user, userDetails);
        
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
     * Refresh access token using refresh token.
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            String username = jwtService.extractUsername(request.getRefreshToken());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (!jwtService.validateToken(request.getRefreshToken(), userDetails)) {
                throw new InvalidTokenException("Invalid refresh token");
            }
            
            User user = userDetailsService.loadUserEntityByUsername(username);
            
            // Generate new access token with custom claims (including company data)
            Map<String, Object> claims = buildJwtClaims(user, userDetails);
            
            String accessToken = jwtService.generateToken(claims, userDetails);
            
            return new AuthResponse(
                    accessToken,
                    request.getRefreshToken(),
                    jwtService.getExpirationTime(),
                    userMapper.toDto(user)
            );
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }
    
    /**
     * Get current user profile.
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String username) {
        User user = userDetailsService.loadUserEntityByUsername(username);
        return userMapper.toDto(user);
    }
    
    /**
     * Logout user by blacklisting the access token.
     * @param token The JWT access token to blacklist
     */
    public void logout(String token) {
        try {
            Date expirationDate = jwtService.extractExpiration(token);
            tokenBlacklistService.blacklistToken(token, expirationDate);
        } catch (Exception e) {
            // If token is invalid, we don't need to blacklist it
            // Just log the attempt
        }
    }
    
    /**
     * Verify email and generate new tokens with updated user info.
     * 
     * @param email User's email address
     * @param code Verification code
     * @return VerifyEmailResponse with new tokens and updated user
     */
    @Transactional
    public VerifyEmailResponse verifyEmailAndGenerateTokens(String email, String code) {
        System.out.println("===========================================");
        System.out.println("=== AuthService.verifyEmailAndGenerateTokens() CALLED");
        System.out.println("=== Email: " + email);
        System.out.println("=== Code: " + code);
        System.out.println("===========================================");
        
        try {
            // Delegate to EmailVerificationService for verification
            System.out.println("=== About to call emailVerificationService.verifyEmail()");
            User user = emailVerificationService.verifyEmail(email, code);
            System.out.println("=== emailVerificationService.verifyEmail() returned successfully");
            log.debug("Email verified successfully for user ID: {}, username: {}", user.getId(), user.getUserName());
            
            // Generate new tokens with updated user info
            log.debug("Loading user details for username: {}", user.getUserName());
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            log.debug("User details loaded successfully, authorities count: {}", 
                    userDetails.getAuthorities() != null ? userDetails.getAuthorities().size() : 0);
            
            log.debug("Building JWT claims (including company data)");
            Map<String, Object> claims = buildJwtClaims(user, userDetails);
            log.debug("JWT claims built: userId={}, email={}, roles={}, companies={}", 
                    user.getId(), user.getEmail(), claims.get("roles"), claims.get("companyExternalIds"));
            
            log.debug("Generating access token");
            String accessToken = jwtService.generateToken(claims, userDetails);
            log.debug("Access token generated successfully");
            
            log.debug("Generating refresh token");
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            log.debug("Refresh token generated successfully");
            
            log.debug("Mapping user to DTO");
            UserDTO userDto = userMapper.toDto(user);
            log.debug("User mapped to DTO successfully");
            
            log.info("Email verification completed successfully for user: {} ({})", user.getUserName(), user.getEmail());
            
            return VerifyEmailResponse.builder()
                    .verified(true)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userDto)
                    .build();
        } catch (Exception e) {
            log.error("Error during email verification for email: {}", email, e);
            throw e;
        }
    }
}

