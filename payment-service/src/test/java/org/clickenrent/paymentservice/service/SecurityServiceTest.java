package org.clickenrent.paymentservice.service;

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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityService securityService;

    private Jwt testJwt;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("companyIds", Arrays.asList(1L, 2L));
        
        testJwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                claims
        );
    }

    @Test
    void getCurrentUserId_WithValidJwt_ReturnsUserId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testJwt);

        Long userId = securityService.getCurrentUserId();

        assertEquals(1L, userId);
    }

    @Test
    void getCurrentUserId_WithoutAuthentication_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Long userId = securityService.getCurrentUserId();

        assertNull(userId);
    }

    @Test
    void getCurrentUserCompanyIds_WithValidJwt_ReturnsCompanyIds() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testJwt);

        List<Long> companyIds = securityService.getCurrentUserCompanyIds();

        assertNotNull(companyIds);
        assertEquals(2, companyIds.size());
        assertTrue(companyIds.contains(1L));
        assertTrue(companyIds.contains(2L));
    }

    @Test
    void getCurrentUserRoles_WithAuthorities_ReturnsRoles() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        List<String> roles = securityService.getCurrentUserRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("CUSTOMER"));
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
    void isAdmin_WithSuperAdminRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_SUPERADMIN")
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
    void isCustomer_WithCustomerRole_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.isCustomer());
    }

    @Test
    void hasAccessToCompany_AsAdmin_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.hasAccessToCompany(999L));
    }

    @Test
    void hasAccessToCompany_WithMatchingCompanyId_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testJwt);

        assertTrue(securityService.hasAccessToCompany(1L));
    }

    @Test
    void hasAccessToUser_AsAdmin_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertTrue(securityService.hasAccessToUser(999L));
    }

    @Test
    void hasAccessToUser_WithMatchingUserId_ReturnsTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testJwt);

        assertTrue(securityService.hasAccessToUser(1L));
    }
}

