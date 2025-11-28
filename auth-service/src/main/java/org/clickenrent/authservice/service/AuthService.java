package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.DuplicateResourceException;
import org.clickenrent.authservice.exception.InvalidTokenException;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * Service for handling authentication operations.
 * Includes registration, login, token refresh, and logout.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;
    
    /**
     * Register a new user in the system.
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
        
        user = userRepository.save(user);
        
        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationTime(),
                userMapper.toDto(user)
        );
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
        
        // Generate tokens
        String accessToken = jwtService.generateToken(userDetails);
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
            String accessToken = jwtService.generateToken(userDetails);
            
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
}

