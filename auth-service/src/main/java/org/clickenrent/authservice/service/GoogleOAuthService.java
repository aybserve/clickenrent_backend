package org.clickenrent.authservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.GoogleTokenResponse;
import org.clickenrent.authservice.dto.GoogleUserInfo;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.metrics.OAuthMetrics;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling Google OAuth authentication.
 * Manages the OAuth flow, token exchange, user creation/linking, and JWT generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {
    
    private final UserRepository userRepository;
    private final GlobalRoleRepository globalRoleRepository;
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final OAuthMetrics oAuthMetrics;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String PROVIDER_GOOGLE = "google";
    
    @Value("${oauth2.google.client-id}")
    private String clientId;
    
    @Value("${oauth2.google.client-secret}")
    private String clientSecret;
    
    @Value("${oauth2.google.token-uri}")
    private String tokenUri;
    
    @Value("${oauth2.google.user-info-uri}")
    private String userInfoUri;
    
    @Value("${oauth2.google.verify-id-token:true}")
    private boolean verifyIdToken;
    
    /**
     * Authenticate user with Google OAuth authorization code.
     * 
     * @param code Authorization code from Google
     * @param redirectUri Redirect URI used in OAuth flow
     * @return AuthResponse with JWT tokens
     */
    @Transactional
    public AuthResponse authenticateWithGoogle(String code, String redirectUri) {
        log.info("Starting Google OAuth authentication");
        
        // Start metrics timer
        Timer.Sample timerSample = oAuthMetrics.startFlowTimer(PROVIDER_GOOGLE);
        oAuthMetrics.recordLoginAttempt(PROVIDER_GOOGLE);
        
        try {
            // Step 1: Exchange authorization code for access token
            GoogleTokenResponse tokenResponse = exchangeCodeForToken(code, redirectUri);
            log.debug("Successfully exchanged code for access token");
            
            // Step 2: Verify ID token if available and verification is enabled
            if (verifyIdToken && tokenResponse.getIdToken() != null) {
                verifyGoogleIdToken(tokenResponse.getIdToken());
                log.debug("Successfully verified Google ID token");
            }
            
            // Step 3: Fetch user info from Google
            GoogleUserInfo googleUserInfo = fetchGoogleUserInfo(tokenResponse.getAccessToken());
            log.info("Fetched Google user info for email: {}", googleUserInfo.getEmail());
            
            // Step 4: Find or create user
            User user = findOrCreateUser(googleUserInfo);
            log.info("User processed: ID={}, email={}", user.getId(), user.getEmail());
            
            // Step 5: Generate JWT tokens
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());
            claims.put("roles", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            
            String accessToken = jwtService.generateToken(claims, userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            
            log.info("Successfully generated JWT tokens for user: {}", user.getEmail());
            
            // Record success metrics
            oAuthMetrics.recordLoginSuccess(PROVIDER_GOOGLE);
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_GOOGLE, "success");
            
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getExpirationTime(),
                    userMapper.toDto(user)
            );
            
        } catch (HttpClientErrorException e) {
            log.error("HTTP error during Google OAuth: {}", e.getMessage());
            oAuthMetrics.recordLoginFailure(PROVIDER_GOOGLE, "http_error");
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_GOOGLE, "failure");
            throw new UnauthorizedException("Failed to authenticate with Google: " + e.getMessage());
        } catch (UnauthorizedException e) {
            log.error("Unauthorized during Google OAuth: {}", e.getMessage());
            oAuthMetrics.recordLoginFailure(PROVIDER_GOOGLE, "unauthorized");
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_GOOGLE, "failure");
            throw e;
        } catch (Exception e) {
            log.error("Error during Google OAuth authentication", e);
            oAuthMetrics.recordLoginFailure(PROVIDER_GOOGLE, "internal_error");
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_GOOGLE, "failure");
            throw new UnauthorizedException("Failed to authenticate with Google: " + e.getMessage());
        }
    }
    
    /**
     * Verify Google ID token.
     * This adds an extra security layer by verifying the token signature and claims.
     * 
     * @param idTokenString ID token from Google
     * @throws UnauthorizedException if token is invalid
     */
    private void verifyGoogleIdToken(String idTokenString) {
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(idTokenString);
            if (idToken == null) {
                throw new UnauthorizedException("Invalid Google ID token");
            }
            
            GoogleIdToken.Payload payload = idToken.getPayload();
            
            // Verify the token is for our client
            if (!clientId.equals(payload.getAudience())) {
                throw new UnauthorizedException("ID token audience mismatch");
            }
            
            // Verify the issuer
            String issuer = payload.getIssuer();
            if (!("accounts.google.com".equals(issuer) || "https://accounts.google.com".equals(issuer))) {
                throw new UnauthorizedException("Invalid ID token issuer");
            }
            
            log.debug("ID token verified for user: {}", payload.getEmail());
            
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to verify Google ID token", e);
            throw new UnauthorizedException("Failed to verify Google ID token: " + e.getMessage());
        }
    }
    
    /**
     * Exchange authorization code for Google access token.
     */
    private GoogleTokenResponse exchangeCodeForToken(String code, String redirectUri) {
        log.debug("Exchanging authorization code for token");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                GoogleTokenResponse.class
        );
        
        if (response.getBody() == null) {
            throw new UnauthorizedException("Failed to get token from Google");
        }
        
        return response.getBody();
    }
    
    /**
     * Fetch user information from Google using access token.
     */
    private GoogleUserInfo fetchGoogleUserInfo(String accessToken) {
        log.debug("Fetching user info from Google");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                entity,
                GoogleUserInfo.class
        );
        
        if (response.getBody() == null) {
            throw new UnauthorizedException("Failed to get user info from Google");
        }
        
        return response.getBody();
    }
    
    /**
     * Find existing user or create new one based on Google user info.
     * Implements auto-linking: if user with same email exists, link Google account.
     */
    private User findOrCreateUser(GoogleUserInfo googleUserInfo) {
        String providerId = PROVIDER_GOOGLE;
        String providerUserId = googleUserInfo.getId();
        String email = googleUserInfo.getEmail();
        
        // First, check if this Google account is already linked
        Optional<User> existingByProvider = userRepository.findByProviderIdAndProviderUserId(providerId, providerUserId);
        if (existingByProvider.isPresent()) {
            log.debug("Found existing user by Google provider ID");
            return existingByProvider.get();
        }
        
        // Check if user with this email already exists (auto-linking)
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            User user = existingByEmail.get();
            
            // Security: Only auto-link if the existing user's email is verified
            // This prevents account takeover if someone registered with an unverified email
            if (!user.getIsEmailVerified()) {
                log.warn("Attempted to auto-link Google account to unverified email: {}", email);
                throw new UnauthorizedException(
                    "An account with this email already exists but is not verified. " +
                    "Please verify your email first or contact support."
                );
            }
            
            log.info("Auto-linking Google account to existing user: {}", email);
            
            // Link Google account to existing user
            user.setProviderId(providerId);
            user.setProviderUserId(providerUserId);
            user.setIsEmailVerified(true); // Google emails are verified
            
            // Update profile picture if not set
            if (user.getImageUrl() == null && googleUserInfo.getPicture() != null) {
                user.setImageUrl(googleUserInfo.getPicture());
            }
            
            // Record auto-linking metric
            oAuthMetrics.recordAutoLinking(PROVIDER_GOOGLE);
            
            return userRepository.save(user);
        }
        
        // Create new user
        log.info("Creating new user from Google account: {}", email);
        
        User newUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(generateUniqueUsername(email))
                .email(email)
                .password(null) // No password for social login users
                .firstName(googleUserInfo.getGivenName())
                .lastName(googleUserInfo.getFamilyName())
                .imageUrl(googleUserInfo.getPicture())
                .providerId(providerId)
                .providerUserId(providerUserId)
                .isActive(true)
                .isEmailVerified(true) // Google emails are verified
                .isDeleted(false)
                .build();
        
        User savedUser = userRepository.save(newUser);
        
        // Assign CUSTOMER role to new user
        assignDefaultRole(savedUser);
        
        // Record new user registration metric
        oAuthMetrics.recordNewUserRegistration(PROVIDER_GOOGLE);
        
        return savedUser;
    }
    
    /**
     * Generate unique username from email.
     * Format: username from email + random suffix if needed.
     */
    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", ".");
        
        // Check if username is available
        if (!userRepository.existsByUserName(baseUsername)) {
            return baseUsername;
        }
        
        // Add random suffix until unique
        String username;
        int attempts = 0;
        do {
            username = baseUsername + "." + UUID.randomUUID().toString().substring(0, 4);
            attempts++;
        } while (userRepository.existsByUserName(username) && attempts < 10);
        
        if (attempts >= 10) {
            // Fallback to UUID-based username
            username = "user." + UUID.randomUUID().toString().substring(0, 8);
        }
        
        return username;
    }
    
    /**
     * Assign default CUSTOMER role to new user.
     */
    private void assignDefaultRole(User user) {
        globalRoleRepository.findByNameIgnoreCase("CUSTOMER").ifPresent(customerRole -> {
            UserGlobalRole userGlobalRole = UserGlobalRole.builder()
                    .user(user)
                    .globalRole(customerRole)
                    .build();
            userGlobalRoleRepository.save(userGlobalRole);
            log.debug("Assigned CUSTOMER role to user: {}", user.getEmail());
        });
    }
}

