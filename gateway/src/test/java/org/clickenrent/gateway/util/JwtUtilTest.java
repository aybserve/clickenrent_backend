package org.clickenrent.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtUtil.
 */
class JwtUtilTest {

    private static final String TEST_SECRET_BASE64 =
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLW9ubHktMjU2LWJpdA==";

    private JwtUtil jwtUtil;
    private String validToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET_BASE64);
        validToken = createValidToken(100L, "user@example.com", List.of("ADMIN", "USER"),
                "ext-user-123", List.of("comp-1", "comp-2"), System.currentTimeMillis() + 3600_000);
    }

    private String createValidToken(Long userId, String email, List<String> roles,
                                    String userExternalId, List<String> companyExternalIds,
                                    long expirationMs) {
        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(TEST_SECRET_BASE64), "HmacSHA256");
        return Jwts.builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .claim("userExternalId", userExternalId)
                .claim("companyExternalIds", companyExternalIds)
                .subject("user@example.com")
                .issuedAt(new Date())
                .expiration(new Date(expirationMs))
                .signWith(key)
                .compact();
    }

    private String createExpiredToken() {
        return createValidToken(1L, "a@b.com", List.of("USER"), null, null,
                System.currentTimeMillis() - 3600_000);
    }

    // --- extractTokenFromHeader ---

    @Test
    void extractTokenFromHeader_withBearerPrefix_returnsToken() {
        assertThat(jwtUtil.extractTokenFromHeader("Bearer " + validToken)).isEqualTo(validToken);
        assertThat(jwtUtil.extractTokenFromHeader("Bearer abc")).isEqualTo("abc");
    }

    @Test
    void extractTokenFromHeader_whenNull_returnsNull() {
        assertThat(jwtUtil.extractTokenFromHeader(null)).isNull();
    }

    @Test
    void extractTokenFromHeader_whenEmpty_returnsNull() {
        assertThat(jwtUtil.extractTokenFromHeader("")).isNull();
    }

    @Test
    void extractTokenFromHeader_withoutBearerPrefix_returnsNull() {
        assertThat(jwtUtil.extractTokenFromHeader("Basic xyz")).isNull();
        assertThat(jwtUtil.extractTokenFromHeader("token-only")).isNull();
    }

    // --- extractUserId, extractEmail, extractRoles, extractUserExternalId, extractCompanyExternalIds ---

    @Test
    void extractUserId_withValidToken_returnsUserId() {
        assertThat(jwtUtil.extractUserId(validToken)).isEqualTo(100L);
    }

    @Test
    void extractEmail_withValidToken_returnsEmail() {
        assertThat(jwtUtil.extractEmail(validToken)).isEqualTo("user@example.com");
    }

    @Test
    void extractRoles_withValidToken_returnsRoles() {
        assertThat(jwtUtil.extractRoles(validToken)).containsExactly("ADMIN", "USER");
    }

    @Test
    void extractUserExternalId_withValidToken_returnsUserExternalId() {
        assertThat(jwtUtil.extractUserExternalId(validToken)).isEqualTo("ext-user-123");
    }

    @Test
    void extractCompanyExternalIds_withValidToken_returnsCompanyExternalIds() {
        assertThat(jwtUtil.extractCompanyExternalIds(validToken)).containsExactly("comp-1", "comp-2");
    }

    @Test
    void extractExpiration_withValidToken_returnsFutureDate() {
        Date exp = jwtUtil.extractExpiration(validToken);
        assertThat(exp).isNotNull();
        assertThat(exp).isAfter(new Date());
    }

    // --- isTokenExpired ---

    @Test
    void isTokenExpired_whenNotExpired_returnsFalse() {
        assertThat(jwtUtil.isTokenExpired(validToken)).isFalse();
    }

    @Test
    void isTokenExpired_whenExpired_returnsTrue() {
        String expired = createExpiredToken();
        assertThat(jwtUtil.isTokenExpired(expired)).isTrue();
    }

    @Test
    void isTokenExpired_whenInvalidToken_returnsTrue() {
        assertThat(jwtUtil.isTokenExpired("invalid.token.here")).isTrue();
    }

    // --- validateToken ---

    @Test
    void validateToken_whenValid_returnsTrue() {
        assertThat(jwtUtil.validateToken(validToken)).isTrue();
    }

    @Test
    void validateToken_whenExpired_returnsFalse() {
        assertThat(jwtUtil.validateToken(createExpiredToken())).isFalse();
    }

    @Test
    void validateToken_whenInvalidSignature_returnsFalse() {
        String wrongSecret = "b3RoZXItc2VjcmV0LWtleS0yNTYtYml0cy1mb3ItdGVzdGluZw==";
        SecretKey otherKey = new SecretKeySpec(Base64.getDecoder().decode(wrongSecret), "HmacSHA256");
        String badToken = Jwts.builder()
                .claim("userId", 1L)
                .subject("a@b.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(otherKey)
                .compact();
        assertThat(jwtUtil.validateToken(badToken)).isFalse();
    }

    @Test
    void validateToken_whenMalformed_returnsFalse() {
        assertThat(jwtUtil.validateToken("not.a.jwt")).isFalse();
        assertThat(jwtUtil.validateToken("")).isFalse();
    }

    // --- extractAllClaims ---

    @Test
    void extractAllClaims_withValidToken_returnsClaims() {
        Claims claims = jwtUtil.extractAllClaims(validToken);
        assertThat(claims.get("userId", Long.class)).isEqualTo(100L);
        assertThat(claims.get("email", String.class)).isEqualTo("user@example.com");
    }

    @Test
    void extractAllClaims_withInvalidToken_throws() {
        assertThatThrownBy(() -> jwtUtil.extractAllClaims("invalid"))
                .isInstanceOf(Exception.class);
    }
}
