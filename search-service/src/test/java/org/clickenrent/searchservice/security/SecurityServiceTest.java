package org.clickenrent.searchservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityService.
 * 
 * @author Vitaliy Shvetsov
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserExternalId_WithValidJwt() {
        // Given
        Jwt jwt = createMockJwt(Map.of("userExternalId", "user-123"));
        setupSecurityContext(jwt);

        // When
        String externalId = securityService.getCurrentUserExternalId();

        // Then
        assertEquals("user-123", externalId);
    }

    @Test
    void testGetCurrentUserCompanyExternalIds_WithValidJwt() {
        // Given
        Jwt jwt = createMockJwt(Map.of("companyExternalIds", List.of("company-1", "company-2")));
        setupSecurityContext(jwt);

        // When
        List<String> companyIds = securityService.getCurrentUserCompanyExternalIds();

        // Then
        assertEquals(2, companyIds.size());
        assertTrue(companyIds.contains("company-1"));
        assertTrue(companyIds.contains("company-2"));
    }

    @Test
    void testIsAdmin_WithAdminRole() {
        // Given
        Jwt jwt = createMockJwt(Map.of("roles", List.of("ADMIN")));
        setupSecurityContext(jwt, List.of(new SimpleGrantedAuthority("ADMIN")));

        // When
        boolean isAdmin = securityService.isAdmin();

        // Then
        assertTrue(isAdmin);
    }

    @Test
    void testIsAdmin_WithSuperAdminRole() {
        // Given
        Jwt jwt = createMockJwt(Map.of("roles", List.of("SUPERADMIN")));
        setupSecurityContext(jwt, List.of(new SimpleGrantedAuthority("SUPERADMIN")));

        // When
        boolean isAdmin = securityService.isAdmin();

        // Then
        assertTrue(isAdmin);
    }

    @Test
    void testIsAdmin_WithoutAdminRole() {
        // Given
        Jwt jwt = createMockJwt(Map.of("roles", List.of("CUSTOMER")));
        setupSecurityContext(jwt, List.of(new SimpleGrantedAuthority("CUSTOMER")));

        // When
        boolean isAdmin = securityService.isAdmin();

        // Then
        assertFalse(isAdmin);
    }

    @Test
    void testHasAccessToCompanyByExternalId_AsAdmin() {
        // Given
        Jwt jwt = createMockJwt(Map.of("roles", List.of("ADMIN")));
        setupSecurityContext(jwt, List.of(new SimpleGrantedAuthority("ADMIN")));

        // When
        boolean hasAccess = securityService.hasAccessToCompanyByExternalId("any-company");

        // Then
        assertTrue(hasAccess);
    }

    @Test
    void testHasAccessToCompanyByExternalId_WithMatchingCompany() {
        // Given
        Jwt jwt = createMockJwt(Map.of("companyExternalIds", List.of("company-1", "company-2")));
        setupSecurityContext(jwt, List.of(new SimpleGrantedAuthority("B2B")));

        // When
        boolean hasAccess = securityService.hasAccessToCompanyByExternalId("company-1");

        // Then
        assertTrue(hasAccess);
    }

    @Test
    void testHasAccessToCompanyByExternalId_WithoutMatchingCompany() {
        // Given
        Jwt jwt = createMockJwt(Map.of("companyExternalIds", List.of("company-1", "company-2")));
        setupSecurityContext(jwt, List.of(new SimpleGrantedAuthority("B2B")));

        // When
        boolean hasAccess = securityService.hasAccessToCompanyByExternalId("company-3");

        // Then
        assertFalse(hasAccess);
    }

    @Test
    void testGetCurrentUserId_WithLongClaim() {
        Jwt jwt = createMockJwt(Map.of("userId", 1001L));
        setupSecurityContext(jwt);

        Long userId = securityService.getCurrentUserId();

        assertEquals(1001L, userId);
    }

    @Test
    void testGetCurrentUserId_WithIntegerClaim() {
        Jwt jwt = createMockJwt(Map.of("userId", 42));
        setupSecurityContext(jwt);

        Long userId = securityService.getCurrentUserId();

        assertEquals(42L, userId);
    }

    @Test
    void testGetCurrentUserId_WhenNotAuthenticated() {
        SecurityContextHolder.clearContext();

        Long userId = securityService.getCurrentUserId();

        assertNull(userId);
    }

    @Test
    void testIsB2B_WithB2BRole() {
        setupSecurityContext(createMockJwt(Map.of("sub", "test")), List.of(new SimpleGrantedAuthority("B2B")));

        assertTrue(securityService.isB2B());
    }

    @Test
    void testIsB2B_WithoutB2BRole() {
        setupSecurityContext(createMockJwt(Map.of("sub", "test")), List.of(new SimpleGrantedAuthority("CUSTOMER")));

        assertFalse(securityService.isB2B());
    }

    @Test
    void testIsCustomer_WithCustomerRole() {
        setupSecurityContext(createMockJwt(Map.of("sub", "test")), List.of(new SimpleGrantedAuthority("CUSTOMER")));

        assertTrue(securityService.isCustomer());
    }

    @Test
    void testIsCustomer_WithoutCustomerRole() {
        setupSecurityContext(createMockJwt(Map.of("sub", "test")), List.of(new SimpleGrantedAuthority("B2B")));

        assertFalse(securityService.isCustomer());
    }

    @Test
    void testHasAccessToUserByExternalId_AsAdmin() {
        setupSecurityContext(createMockJwt(Map.of("roles", List.of("ADMIN"))), List.of(new SimpleGrantedAuthority("ADMIN")));

        assertTrue(securityService.hasAccessToUserByExternalId("any-user-id"));
    }

    @Test
    void testHasAccessToUserByExternalId_WithMatchingUser() {
        Jwt jwt = createMockJwt(Map.of("userExternalId", "user-123"));
        setupSecurityContext(jwt);

        assertTrue(securityService.hasAccessToUserByExternalId("user-123"));
    }

    @Test
    void testHasAccessToUserByExternalId_WithoutMatchingUser() {
        Jwt jwt = createMockJwt(Map.of("userExternalId", "user-123"));
        setupSecurityContext(jwt);

        assertFalse(securityService.hasAccessToUserByExternalId("other-user"));
    }

    // Helper methods
    private Jwt createMockJwt(Map<String, Object> claims) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                claims
        );
    }

    private void setupSecurityContext(Jwt jwt) {
        setupSecurityContext(jwt, List.of());
    }

    private void setupSecurityContext(Jwt jwt, List<SimpleGrantedAuthority> authorities) {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getAuthorities()).thenReturn((List) authorities);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
