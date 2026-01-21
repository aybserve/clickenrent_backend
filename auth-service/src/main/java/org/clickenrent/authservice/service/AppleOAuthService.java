package org.clickenrent.authservice.service;

import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.resilience4j.retry.Retry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.config.AppleOAuthConfig;
import org.clickenrent.authservice.dto.AppleTokenResponse;
import org.clickenrent.authservice.dto.AppleUserInfo;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.metrics.OAuthMetrics;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling Apple OAuth authentication.
 * Manages the OAuth flow, token exchange, user creation/linking, and JWT generation.
 */
@Service
@Slf4j
public class AppleOAuthService {
    
    private final UserRepository userRepository;
    private final GlobalRoleRepository globalRoleRepository;
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final AppleOAuthConfig appleOAuthConfig;
    private final OAuthMetrics oAuthMetrics;
    private final RestTemplate restTemplate;
    private final Retry tokenExchangeRetry;
    private final JWSSigner appleJwtSigner;
    private final ConfigurableJWTProcessor<SecurityContext> appleJwtProcessor;
    
    private static final String PROVIDER_APPLE = "apple";
    
    @Value("${oauth2.apple.client-id}")
    private String clientId;
    
    @Value("${oauth2.apple.token-uri}")
    private String tokenUri;
    
    @Value("${oauth2.apple.verify-id-token:true}")
    private boolean verifyIdToken;
    
    /**
     * Constructor with dependency injection.
     */
    public AppleOAuthService(
            UserRepository userRepository,
            GlobalRoleRepository globalRoleRepository,
            UserGlobalRoleRepository userGlobalRoleRepository,
            UserCompanyRepository userCompanyRepository,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserMapper userMapper,
            AppleOAuthConfig appleOAuthConfig,
            OAuthMetrics oAuthMetrics,
            RestTemplate restTemplate,
            @Qualifier("appleTokenExchangeRetry") Retry tokenExchangeRetry,
            ObjectProvider<JWSSigner> appleJwtSignerProvider,
            ObjectProvider<ConfigurableJWTProcessor<SecurityContext>> appleJwtProcessorProvider) {
        this.userRepository = userRepository;
        this.globalRoleRepository = globalRoleRepository;
        this.userGlobalRoleRepository = userGlobalRoleRepository;
        this.userCompanyRepository = userCompanyRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.appleOAuthConfig = appleOAuthConfig;
        this.oAuthMetrics = oAuthMetrics;
        this.restTemplate = restTemplate;
        this.tokenExchangeRetry = tokenExchangeRetry;
        this.appleJwtSigner = appleJwtSignerProvider.getIfAvailable();
        this.appleJwtProcessor = appleJwtProcessorProvider.getIfAvailable();
    }
    
    /**
     * Build JWT claims with user information including company associations.
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
     * Authenticate user with Apple OAuth authorization code.
     */
    @Transactional
    public AuthResponse authenticateWithApple(String code, String redirectUri) {
        log.info("Starting Apple OAuth authentication");
        
        // Start metrics timer
        Timer.Sample timerSample = oAuthMetrics.startFlowTimer(PROVIDER_APPLE);
        oAuthMetrics.recordLoginAttempt(PROVIDER_APPLE);
        
        try {
            // Check if Apple OAuth is properly configured
            if (appleJwtSigner == null) {
                log.error("Apple OAuth is not properly configured - private key is missing");
                throw new UnauthorizedException("Apple Sign In is not configured on this server");
            }
            
            // Step 1: Generate client_secret JWT
            String clientSecret = appleOAuthConfig.generateClientSecret(appleJwtSigner);
            log.debug("Generated client_secret JWT for Apple");
            
            // Step 2: Exchange authorization code for access token
            AppleTokenResponse tokenResponse = exchangeCodeForToken(code, redirectUri, clientSecret);
            log.debug("Successfully exchanged code for access token");
            
            // Step 3: Verify and parse ID token
            AppleUserInfo appleUserInfo = extractUserInfoFromIdToken(tokenResponse.getIdToken());
            log.info("Extracted Apple user info for email: {}", appleUserInfo.getEmail());
            
            // Step 4: Find or create user
            User user = findOrCreateUser(appleUserInfo);
            log.info("User processed: ID={}, email={}", user.getId(), user.getEmail());
            
            // Step 5: Generate JWT tokens (including company data)
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            
            Map<String, Object> claims = buildJwtClaims(user, userDetails);
            
            String accessToken = jwtService.generateToken(claims, userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            
            log.info("Successfully generated JWT tokens for user: {}", user.getEmail());
            
            // Record success metrics
            oAuthMetrics.recordLoginSuccess(PROVIDER_APPLE);
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_APPLE, "success");
            
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getExpirationTime(),
                    userMapper.toDto(user)
            );
            
        } catch (HttpClientErrorException e) {
            log.error("HTTP error during Apple OAuth: {}", e.getMessage());
            oAuthMetrics.recordLoginFailure(PROVIDER_APPLE, "http_error");
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_APPLE, "failure");
            throw new UnauthorizedException("Failed to authenticate with Apple: " + e.getMessage());
        } catch (UnauthorizedException e) {
            log.error("Unauthorized during Apple OAuth: {}", e.getMessage());
            oAuthMetrics.recordLoginFailure(PROVIDER_APPLE, "unauthorized");
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_APPLE, "failure");
            throw e;
        } catch (Exception e) {
            log.error("Error during Apple OAuth authentication", e);
            oAuthMetrics.recordLoginFailure(PROVIDER_APPLE, "internal_error");
            oAuthMetrics.recordFlowDuration(timerSample, PROVIDER_APPLE, "failure");
            throw new UnauthorizedException("Failed to authenticate with Apple: " + e.getMessage());
        }
    }
    
    /**
     * Exchange authorization code for Apple access token.
     * Uses retry mechanism with exponential backoff for resilience.
     */
    private AppleTokenResponse exchangeCodeForToken(String code, String redirectUri, String clientSecret) {
        log.debug("Exchanging authorization code for token");
        
        // Wrap the API call with retry logic
        return tokenExchangeRetry.executeSupplier(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<AppleTokenResponse> response = restTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    request,
                    AppleTokenResponse.class
            );
            
            if (response.getBody() == null) {
                throw new UnauthorizedException("Failed to get token from Apple");
            }
            
            return response.getBody();
        });
    }
    
    /**
     * Extract user information from Apple ID token.
     * Verifies token signature and claims, then extracts user data.
     */
    private AppleUserInfo extractUserInfoFromIdToken(String idToken) {
        log.debug("Extracting user info from Apple ID token");
        
        try {
            // Verify and parse ID token
            JWTClaimsSet claimsSet;
            if (verifyIdToken) {
                if (appleJwtProcessor == null) {
                    log.error("Apple JWT processor is not configured - cannot verify ID token");
                    throw new UnauthorizedException("Apple Sign In is not properly configured");
                }
                claimsSet = appleOAuthConfig.verifyIdToken(idToken, appleJwtProcessor);
                log.debug("Successfully verified Apple ID token");
            } else {
                // Parse without verification (only for development/testing)
                log.warn("Apple ID token verification is disabled - this should only be used for testing");
                com.nimbusds.jwt.SignedJWT signedJWT = com.nimbusds.jwt.SignedJWT.parse(idToken);
                claimsSet = signedJWT.getJWTClaimsSet();
            }
            
            // Extract user information from claims
            String sub = claimsSet.getSubject();
            String email = claimsSet.getStringClaim("email");
            Boolean emailVerified = claimsSet.getBooleanClaim("email_verified");
            Boolean isPrivateEmail = claimsSet.getBooleanClaim("is_private_email");
            
            if (sub == null || email == null) {
                throw new UnauthorizedException("ID token missing required claims (sub or email)");
            }
            
            // Check if this is a private relay email
            if (isPrivateEmail == null) {
                isPrivateEmail = email.endsWith("@privaterelay.appleid.com");
            }
            
            log.debug("Extracted user info: sub={}, email={}, isPrivateEmail={}", sub, email, isPrivateEmail);
            
            return AppleUserInfo.builder()
                    .sub(sub)
                    .email(email)
                    .emailVerified(emailVerified != null ? emailVerified : true) // Apple emails are verified
                    .isPrivateEmail(isPrivateEmail)
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to extract user info from ID token", e);
            throw new UnauthorizedException("Failed to parse Apple ID token: " + e.getMessage());
        }
    }
    
    /**
     * Find existing user or create new one based on Apple user info.
     * Implements auto-linking: if user with same email exists, link Apple account.
     */
    private User findOrCreateUser(AppleUserInfo appleUserInfo) {
        String providerId = PROVIDER_APPLE;
        String providerUserId = appleUserInfo.getSub();
        String email = appleUserInfo.getEmail();
        
        // First, check if this Apple account is already linked
        Optional<User> existingByProvider = userRepository.findByProviderIdAndProviderUserId(providerId, providerUserId);
        if (existingByProvider.isPresent()) {
            log.debug("Found existing user by Apple provider ID");
            return existingByProvider.get();
        }
        
        // Check if user with this email already exists (auto-linking)
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            User user = existingByEmail.get();
            
            // Security: Only auto-link if the existing user's email is verified
            if (!user.getIsEmailVerified()) {
                log.warn("Attempted to auto-link Apple account to unverified email: {}", email);
                throw new UnauthorizedException(
                    "An account with this email already exists but is not verified. " +
                    "Please verify your email first or contact support."
                );
            }
            
            log.info("Auto-linking Apple account to existing user: {}", email);
            
            // Link Apple account to existing user
            user.setProviderId(providerId);
            user.setProviderUserId(providerUserId);
            user.setIsEmailVerified(true); // Apple emails are verified
            
            // Record auto-linking metric
            oAuthMetrics.recordAutoLinking(PROVIDER_APPLE);
            
            // Track private relay email usage
            if (Boolean.TRUE.equals(appleUserInfo.getIsPrivateEmail())) {
                log.info("User is using Apple private relay email: {}", email);
            }
            
            return userRepository.save(user);
        }
        
        // Create new user
        log.info("Creating new user from Apple account: {}", email);
        
        User newUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(generateUniqueUsername(email))
                .email(email)
                .password(null) // No password for social login users
                .firstName(appleUserInfo.getFirstName())
                .lastName(appleUserInfo.getLastName())
                .imageUrl(null) // Apple doesn't provide profile pictures
                .providerId(providerId)
                .providerUserId(providerUserId)
                .isActive(true)
                .isEmailVerified(true) // Apple emails are verified
                .isDeleted(false)
                .build();
        
        User savedUser = userRepository.save(newUser);
        
        // Assign CUSTOMER role to new user
        assignDefaultRole(savedUser);
        
        // Record new user registration metric
        oAuthMetrics.recordNewUserRegistration(PROVIDER_APPLE);
        
        // Track private relay email usage
        if (Boolean.TRUE.equals(appleUserInfo.getIsPrivateEmail())) {
            log.info("New user registered with Apple private relay email: {}", email);
        }
        
        return savedUser;
    }
    
    /**
     * Generate unique username from email.
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
