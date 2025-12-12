package org.clickenrent.authservice.service;

import io.jsonwebtoken.Claims;
import org.clickenrent.authservice.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtService.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtService, "secret", 
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLW9ubHktMjU2LWJpdA==");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 7200000L); // 2 hours

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void generateToken_Success() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void generateToken_WithExtraClaims_Success() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("department", "IT");

        // When
        String token = jwtService.generateToken(claims, userDetails);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void generateRefreshToken_Success() {
        // When
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
    }

    @Test
    void extractUsername_Success() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void extractExpiration_Success() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        Boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_WrongUsername_ReturnsFalse() {
        // Given
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // When
        Boolean isValid = jwtService.validateToken(token, differentUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Given
        JwtService shortLivedJwtService = new JwtService();
        ReflectionTestUtils.setField(shortLivedJwtService, "secret", 
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLW9ubHktMjU2LWJpdA==");
        ReflectionTestUtils.setField(shortLivedJwtService, "expiration", -1000L); // Negative expiration

        String token = shortLivedJwtService.generateToken(userDetails);

        // When & Then
        // The token is immediately expired, so validation should fail
        assertThat(shortLivedJwtService.validateToken(token, userDetails)).isFalse();
    }

    @Test
    void extractAllClaims_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid or expired JWT token");
    }

    @Test
    void getExpirationTime_ReturnsCorrectValue() {
        // When
        Long expiration = jwtService.getExpirationTime();

        // Then
        assertThat(expiration).isEqualTo(3600000L);
    }

    @Test
    void getRefreshExpirationTime_ReturnsCorrectValue() {
        // When
        Long refreshExpiration = jwtService.getRefreshExpirationTime();

        // Then
        assertThat(refreshExpiration).isEqualTo(7200000L);
    }

    @Test
    void extractClaim_CustomClaim_Success() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("customClaim", "customValue");
        String token = jwtService.generateToken(claims, userDetails);

        // When
        String customClaim = jwtService.extractClaim(token, 
            (Claims c) -> c.get("customClaim", String.class));

        // Then
        assertThat(customClaim).isEqualTo("customValue");
    }

    @Test
    void tokenLifecycle_GenerateValidateExtract_Success() {
        // Generate token
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Validate tokens
        assertThat(jwtService.validateToken(accessToken, userDetails)).isTrue();
        assertThat(jwtService.validateToken(refreshToken, userDetails)).isTrue();

        // Extract username from both tokens
        assertThat(jwtService.extractUsername(accessToken)).isEqualTo("testuser");
        assertThat(jwtService.extractUsername(refreshToken)).isEqualTo("testuser");

        // Check expiration dates
        Date accessExpiration = jwtService.extractExpiration(accessToken);
        Date refreshExpiration = jwtService.extractExpiration(refreshToken);
        assertThat(refreshExpiration).isAfter(accessExpiration);
    }
}


