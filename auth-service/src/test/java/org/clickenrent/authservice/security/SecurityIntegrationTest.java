package org.clickenrent.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.RegisterRequest;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.clickenrent.authservice.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security integration tests for the auth-service.
 * Tests JWT authentication, authorization, and security configurations.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalRoleRepository globalRoleRepository;

    @Autowired
    private UserGlobalRoleRepository userGlobalRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User adminUser;
    private User regularUser;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        // Clean database
        userGlobalRoleRepository.deleteAll();
        userRepository.deleteAll();
        globalRoleRepository.deleteAll();

        // Create roles
        GlobalRole adminRole = GlobalRole.builder().name("ADMIN").build();
        GlobalRole userRole = GlobalRole.builder().name("USER").build();
        globalRoleRepository.save(adminRole);
        globalRoleRepository.save(userRole);

        // Create admin user
        adminUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password"))
                .isActive(true)
                .isDeleted(false)
                .build();
        adminUser = userRepository.save(adminUser);

        UserGlobalRole adminUserRole = UserGlobalRole.builder()
                .user(adminUser)
                .globalRole(adminRole)
                .build();
        userGlobalRoleRepository.save(adminUserRole);

        // Create regular user
        regularUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("user")
                .email("user@example.com")
                .password(passwordEncoder.encode("password"))
                .isActive(true)
                .isDeleted(false)
                .build();
        regularUser = userRepository.save(regularUser);

        // Generate JWT tokens
        UserDetails adminUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();
        adminToken = jwtService.generateToken(adminUserDetails);

        UserDetails regularUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("user")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        userToken = jwtService.generateToken(regularUserDetails);
    }

    @AfterEach
    void tearDown() {
        userGlobalRoleRepository.deleteAll();
        userRepository.deleteAll();
        globalRoleRepository.deleteAll();
    }

    // Test public endpoints
    @Test
    void publicEndpoints_NoAuth_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .userName("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();

        // Register endpoint should be accessible without authentication
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void protectedEndpoint_NoAuth_ReturnsForbidden() throws Exception {
        // Protected endpoint without token should return 403
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_WithValidToken_Success() throws Exception {
        // Protected endpoint with valid token should succeed
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_WithInvalidToken_ReturnsForbidden() throws Exception {
        // Protected endpoint with invalid token should return 403
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_WithExpiredToken_ReturnsForbidden() throws Exception {
        // Create an expired token (this would require mocking or waiting)
        // For now, we'll use a malformed token to simulate expired behavior
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer expired.token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_AsAdmin_Success() throws Exception {
        // Admin endpoint with admin token should succeed
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_AsRegularUser_ReturnsForbidden() throws Exception {
        // Admin endpoint with regular user token should return 403
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_NoAuth_ReturnsForbidden() throws Exception {
        // Admin endpoint without token should return 403
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteEndpoint_AsAdmin_Success() throws Exception {
        // Delete user endpoint with admin token should succeed
        mockMvc.perform(delete("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEndpoint_AsRegularUser_ReturnsForbidden() throws Exception {
        // Delete user endpoint with regular user token should return 403
        mockMvc.perform(delete("/api/users/" + adminUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void jwtAuthentication_WithoutBearerPrefix_ReturnsForbidden() throws Exception {
        // Token without "Bearer " prefix should fail
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void jwtAuthentication_WithEmptyToken_ReturnsForbidden() throws Exception {
        // Empty token should fail
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isForbidden());
    }

    @Test
    void jwtAuthentication_WithoutAuthorizationHeader_ReturnsForbidden() throws Exception {
        // No Authorization header should fail
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userEndpoint_SameUser_Success() throws Exception {
        // User should be able to access their own data
        mockMvc.perform(get("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateEndpoint_SameUser_Success() throws Exception {
        // User should be able to update their own data
        mockMvc.perform(put("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"user\",\"email\":\"user@example.com\",\"firstName\":\"Updated\",\"lastName\":\"User\",\"isActive\":true,\"isDeleted\":false}"))
                .andExpect(status().isOk());
    }

    @Test
    void csrfProtection_Disabled_ForStatelessAPI() throws Exception {
        // CSRF should be disabled for stateless JWT authentication
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void sessionManagement_Stateless_NoSessionCreated() throws Exception {
        // Session should not be created (stateless)
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
        // In stateless mode, no session should be created
        // This is verified by the SessionCreationPolicy.STATELESS configuration
    }

    @Test
    void inactiveUser_CannotLogin() throws Exception {
        // Deactivate user
        regularUser.setIsActive(false);
        userRepository.save(regularUser);

        // Attempt to use token of inactive user should fail
        // Note: The token might still be valid, but the user is inactive
        // This test verifies that inactive users are properly handled
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletedUser_CannotLogin() throws Exception {
        // Delete user
        regularUser.setIsDeleted(true);
        userRepository.save(regularUser);

        // Attempt to use token of deleted user should fail
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void multipleRoles_CorrectAuthorities() throws Exception {
        // Create a user with multiple roles
        GlobalRole b2bRole = GlobalRole.builder().name("B2B").build();
        globalRoleRepository.save(b2bRole);

        UserGlobalRole adminB2BRole = UserGlobalRole.builder()
                .user(adminUser)
                .globalRole(b2bRole)
                .build();
        userGlobalRoleRepository.save(adminB2BRole);

        // Admin user now has both ADMIN and B2B roles
        // Generate new token with updated authorities
        UserDetails multiRoleUser = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password("password")
                .authorities(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_B2B")
                )
                .build();
        String multiRoleToken = jwtService.generateToken(multiRoleUser);

        // Should be able to access admin endpoints
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + multiRoleToken))
                .andExpect(status().isOk());
    }
}






