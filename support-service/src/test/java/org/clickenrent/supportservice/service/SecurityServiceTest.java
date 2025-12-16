package org.clickenrent.supportservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @InjectMocks
    private SecurityService securityService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Jwt createMockJwt(Map<String, Object> claims) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );
    }

    @Test
    void getCurrentUserId_WithValidJwt_ReturnsUserId() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1);
        jwt = createMockJwt(claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Long result = securityService.getCurrentUserId();

        assertEquals(1L, result);
    }

    @Test
    void getCurrentUserId_WithLongUserId_ReturnsUserId() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        jwt = createMockJwt(claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        Long result = securityService.getCurrentUserId();

        assertEquals(1L, result);
    }

    @Test
    void getCurrentUserId_WithNoAuthentication_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Long result = securityService.getCurrentUserId();

        assertNull(result);
    }

    @Test
    void getCurrentUserCompanyIds_WithValidJwt_ReturnsCompanyIds() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyIds", Arrays.asList(1, 2, 3));
        jwt = createMockJwt(claims);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        List<Long> result = securityService.getCurrentUserCompanyIds();

        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void getCurrentUserCompanyIds_WithNoAuthentication_ReturnsEmptyList() {
        when(securityContext.getAuthentication()).thenReturn(null);

        List<Long> result = securityService.getCurrentUserCompanyIds();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserRoles_WithAuthorities_ReturnsRoles() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        List<String> result = securityService.getCurrentUserRoles();

        assertEquals(2, result.size());
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("CUSTOMER"));
    }

    @Test
    void isAdmin_WithAdminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.isAdmin();

        assertTrue(result);
    }

    @Test
    void isAdmin_WithSuperAdminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_SUPERADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.isAdmin();

        assertTrue(result);
    }

    @Test
    void isAdmin_WithCustomerRole_ReturnsFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.isAdmin();

        assertFalse(result);
    }

    @Test
    void isB2B_WithB2BRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_B2B")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.isB2B();

        assertTrue(result);
    }

    @Test
    void isCustomer_WithCustomerRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.isCustomer();

        assertTrue(result);
    }

    @Test
    void hasAccessToCompany_AsAdmin_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.hasAccessToCompany(999L);

        assertTrue(result);
    }

    @Test
    void hasAccessToCompany_WithMatchingCompanyId_ReturnsTrue() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyIds", Arrays.asList(1, 2, 3));
        jwt = createMockJwt(claims);

        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_B2B")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.hasAccessToCompany(2L);

        assertTrue(result);
    }

    @Test
    void hasAccessToUser_AsAdmin_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.hasAccessToUser(999L);

        assertTrue(result);
    }

    @Test
    void hasAccessToUser_WithMatchingUserId_ReturnsTrue() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1);
        jwt = createMockJwt(claims);

        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.hasAccessToUser(1L);

        assertTrue(result);
    }

    @Test
    void hasAccessToUser_WithDifferentUserId_ReturnsFalse() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1);
        jwt = createMockJwt(claims);

        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean result = securityService.hasAccessToUser(999L);

        assertFalse(result);
    }
}
