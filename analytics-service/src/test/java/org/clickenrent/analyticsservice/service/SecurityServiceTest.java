package org.clickenrent.analyticsservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SecurityService.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SecurityServiceTest {

    @InjectMocks
    private SecurityService securityService;

    private SecurityContext securityContext;
    private Authentication authentication;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUserId_WithValidJwtInteger_ReturnsUserId() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Long result = securityService.getCurrentUserId();

        assertEquals(123L, result);
    }

    @Test
    void getCurrentUserId_WithLongType_ReturnsUserId() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123L);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Long result = securityService.getCurrentUserId();

        assertEquals(123L, result);
    }

    @Test
    void getCurrentUserId_WithoutAuthentication_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Long result = securityService.getCurrentUserId();

        assertNull(result);
    }

    @Test
    void getCurrentUserExternalId_WithValidJwt_ReturnsExternalId() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userExternalId", "usr-ext-001");
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        String result = securityService.getCurrentUserExternalId();

        assertEquals("usr-ext-001", result);
    }

    @Test
    void getCurrentUserExternalId_WithoutAuthentication_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        String result = securityService.getCurrentUserExternalId();

        assertNull(result);
    }

    @Test
    void getCurrentUserCompanyExternalIds_WithValidJwt_ReturnsCompanyExternalIds() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyExternalIds", Arrays.asList("company-1", "company-2"));
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        List<String> result = securityService.getCurrentUserCompanyExternalIds();

        assertEquals(2, result.size());
        assertTrue(result.contains("company-1"));
        assertTrue(result.contains("company-2"));
    }

    @Test
    void getCurrentUserCompanyExternalIds_WithoutAuthentication_ReturnsEmptyList() {
        when(securityContext.getAuthentication()).thenReturn(null);

        List<String> result = securityService.getCurrentUserCompanyExternalIds();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserRoles_WithAuthorities_ReturnsRoles() {
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_SUPERADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        List<String> result = securityService.getCurrentUserRoles();

        assertEquals(2, result.size());
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("SUPERADMIN"));
    }

    @Test
    void getCurrentUserRoles_WithoutAuthentication_ReturnsEmptyList() {
        when(securityContext.getAuthentication()).thenReturn(null);

        List<String> result = securityService.getCurrentUserRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void isAdmin_WithSuperadminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_SUPERADMIN")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.isAdmin());
    }

    @Test
    void isAdmin_WithAdminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.isAdmin());
    }

    @Test
    void isAdmin_WithCustomerRole_ReturnsFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertFalse(securityService.isAdmin());
    }

    @Test
    void isB2B_WithB2BRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_B2B")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.isB2B());
    }

    @Test
    void isB2B_WithoutB2BRole_ReturnsFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertFalse(securityService.isB2B());
    }

    @Test
    void isCustomer_WithCustomerRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.isCustomer());
    }

    @Test
    void isCustomer_WithoutCustomerRole_ReturnsFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertFalse(securityService.isCustomer());
    }

    @Test
    void hasAccessToCompanyByExternalId_WithAdminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.hasAccessToCompanyByExternalId("any-company"));
    }

    @Test
    void hasAccessToCompanyByExternalId_WithMatchingCompanyExternalId_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_B2B")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyExternalIds", Arrays.asList("company-1", "company-2"));
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        assertTrue(securityService.hasAccessToCompanyByExternalId("company-1"));
    }

    @Test
    void hasAccessToCompanyByExternalId_WithoutMatchingCompanyExternalId_ReturnsFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_B2B")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyExternalIds", Arrays.asList("company-2", "company-3"));
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        assertFalse(securityService.hasAccessToCompanyByExternalId("company-1"));
    }

    @Test
    void hasAccessToUserByExternalId_WithAdminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.hasAccessToUserByExternalId("any-user-ext"));
    }

    @Test
    void hasAccessToUserByExternalId_WithMatchingUserExternalId_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("userExternalId", "usr-ext-123");
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        assertTrue(securityService.hasAccessToUserByExternalId("usr-ext-123"));
    }

    @Test
    void hasAccessToUserByExternalId_WithoutMatchingUserExternalId_ReturnsFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("userExternalId", "usr-ext-123");
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"), claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        assertFalse(securityService.hasAccessToUserByExternalId("usr-ext-999"));
    }
}
