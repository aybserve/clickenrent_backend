package org.clickenrent.notificationservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @InjectMocks
    private SecurityService securityService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserExternalId_whenJwtHasUserExternalIdClaim_returnsClaim() {
        Jwt jwt = Jwt.withTokenValue("token")
                .claim("userExternalId", "ext-123")
                .subject("sub")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ROLE_USER"));

        String result = securityService.getCurrentUserExternalId();

        assertThat(result).isEqualTo("ext-123");
    }

    @Test
    void getCurrentUserExternalId_whenJwtHasNoUserExternalIdFallbackToSubject_returnsSubject() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("subject-id")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ROLE_USER"));

        String result = securityService.getCurrentUserExternalId();

        assertThat(result).isEqualTo("subject-id");
    }

    @Test
    void getCurrentUserExternalId_whenNotAuthenticated_throwsIllegalStateException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> securityService.getCurrentUserExternalId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User not authenticated");
    }

    @Test
    void getCurrentUsername_whenAuthenticated_returnsName() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("user@test.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of());

        String result = securityService.getCurrentUsername();

        assertThat(result).isEqualTo("user@test.com");
    }

    @Test
    void getCurrentUsername_whenNotAuthenticated_returnsAnonymous() {
        SecurityContextHolder.clearContext();

        String result = securityService.getCurrentUsername();

        assertThat(result).isEqualTo("anonymous");
    }

    @Test
    void getCurrentUserCompanyExternalIds_whenJwtHasClaim_returnsList() {
        Jwt jwt = Jwt.withTokenValue("token")
                .claim("companyExternalIds", List.of("co-1", "co-2"))
                .subject("sub")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of());

        List<String> result = securityService.getCurrentUserCompanyExternalIds();

        assertThat(result).containsExactly("co-1", "co-2");
    }

    @Test
    void getCurrentUserCompanyExternalIds_whenNoClaim_returnsEmptyList() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("sub")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of());

        List<String> result = securityService.getCurrentUserCompanyExternalIds();

        assertThat(result).isEmpty();
    }

    @Test
    void isAdmin_whenUserHasAdminRole_returnsTrue() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("admin")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ROLE_ADMIN"));

        boolean result = securityService.isAdmin();

        assertThat(result).isTrue();
    }

    @Test
    void isAdmin_whenUserHasAdminAuthorityWithoutPrefix_returnsTrue() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("admin")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ADMIN"));

        boolean result = securityService.isAdmin();

        assertThat(result).isTrue();
    }

    @Test
    void isAdmin_whenUserHasNoAdminRole_returnsFalse() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ROLE_USER"));

        boolean result = securityService.isAdmin();

        assertThat(result).isFalse();
    }

    @Test
    void isB2B_whenUserHasB2BRole_returnsTrue() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("b2b")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ROLE_B2B"));

        boolean result = securityService.isB2B();

        assertThat(result).isTrue();
    }

    @Test
    void isB2B_whenUserHasNoB2BRole_returnsFalse() {
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        setAuthentication(jwt, List.of("ROLE_USER"));

        boolean result = securityService.isB2B();

        assertThat(result).isFalse();
    }

    private void setAuthentication(Jwt jwt, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        Authentication auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(
                jwt, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
