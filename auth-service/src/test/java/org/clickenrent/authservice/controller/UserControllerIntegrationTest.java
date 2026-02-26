package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.client.SearchServiceClient;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.event.IndexEventPublisher;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerIntegrationTest {

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

    @MockBean
    private IndexEventPublisher indexEventPublisher;

    @MockBean
    private SearchServiceClient searchServiceClient;

    private User adminUser;
    private User regularUser;
    private String adminToken;
    private String userToken;
    private GlobalRole adminRole;
    private GlobalRole userRole;
    private String adminUserName;
    private String regularUserName;

    @BeforeEach
    void setUp() {
        // Clean up only our test users and their role links (do not delete global_roles - seed data may be referenced)
        userGlobalRoleRepository.deleteAll();
        userRepository.deleteAll();
        // Reuse or create ADMIN and USER roles (never delete them to avoid FK from other tests)
        adminRole = globalRoleRepository.findByNameIgnoreCase("ADMIN")
                .orElseGet(() -> globalRoleRepository.save(GlobalRole.builder().name("ADMIN").build()));
        userRole = globalRoleRepository.findByNameIgnoreCase("USER")
                .orElseGet(() -> globalRoleRepository.save(GlobalRole.builder().name("USER").build()));

        adminUserName = "admin_" + UUID.randomUUID().toString().substring(0, 8);
        regularUserName = "user_" + UUID.randomUUID().toString().substring(0, 8);

        // Create admin user
        adminUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(adminUserName)
                .email("admin@example.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Admin")
                .lastName("User")
                .isActive(true)
                .isDeleted(false)
                .build();
        adminUser = userRepository.save(adminUser);

        // Assign admin role
        UserGlobalRole adminUserRole = UserGlobalRole.builder()
                .user(adminUser)
                .globalRole(adminRole)
                .build();
        userGlobalRoleRepository.save(adminUserRole);

        // Create regular user
        regularUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName(regularUserName)
                .email("user@example.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Regular")
                .lastName("User")
                .isActive(true)
                .isDeleted(false)
                .build();
        regularUser = userRepository.save(regularUser);

        // Generate tokens
        UserDetails adminUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username(adminUserName)
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();
        adminToken = jwtService.generateToken(adminUserDetails);

        UserDetails regularUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username(regularUserName)
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        userToken = jwtService.generateToken(regularUserDetails);
    }

    @AfterEach
    void tearDown() {
        userGlobalRoleRepository.deleteAll();
        userRepository.deleteAll();
        // Do not delete global_roles - they may be seed data or shared; next setUp will reuse them
    }

    @Test
    void getAllUsers_AsAdmin_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].userName").exists());
    }

    @Test
    void getAllUsers_AsRegularUser_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_Authenticated_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(regularUserName))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void getUserById_NotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByExternalId_Authenticated_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/users/external/" + regularUser.getExternalId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(regularUserName));
    }

    @Test
    void createUser_AsAdmin_ReturnsCreated() throws Exception {
        UserDTO newUser = UserDTO.builder()
                .userName("newuser")
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .isActive(true)
                .isDeleted(false)
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void createUser_AsRegularUser_ReturnsForbidden() throws Exception {
        UserDTO newUser = UserDTO.builder()
                .userName("newuser")
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + userToken)
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_Authenticated_ReturnsOk() throws Exception {
        UserDTO updateDTO = UserDTO.builder()
                .userName(regularUserName)
                .email("user@example.com")
                .firstName("Updated")
                .lastName("Name")
                .isActive(true)
                .isDeleted(false)
                .build();

        mockMvc.perform(put("/api/v1/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_NotFound_ReturnsNotFound() throws Exception {
        UserDTO updateDTO = UserDTO.builder()
                .userName(regularUserName)
                .email("user@example.com")
                .firstName("Updated")
                .lastName("Name")
                .build();

        mockMvc.perform(put("/api/v1/users/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_AsAdmin_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify user is soft-deleted
        User deletedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(deletedUser.getIsDeleted()).isTrue();
        org.assertj.core.api.Assertions.assertThat(deletedUser.getIsActive()).isFalse();
    }

    @Test
    void deleteUser_AsRegularUser_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + adminUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void activateUser_AsAdmin_ReturnsOk() throws Exception {
        // First deactivate the user
        regularUser.setIsActive(false);
        userRepository.save(regularUser);

        mockMvc.perform(put("/api/v1/users/" + regularUser.getId() + "/activate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void activateUser_AsRegularUser_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/api/v1/users/" + adminUser.getId() + "/activate")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deactivateUser_AsAdmin_ReturnsOk() throws Exception {
        mockMvc.perform(put("/api/v1/users/" + regularUser.getId() + "/deactivate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void deactivateUser_AsRegularUser_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/api/v1/users/" + adminUser.getId() + "/deactivate")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithPagination_ReturnsCorrectPage() throws Exception {
        // Create additional users
        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .externalId(UUID.randomUUID().toString())
                    .userName("user" + i)
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("password"))
                    .isActive(true)
                    .isDeleted(false)
                    .build();
            userRepository.save(user);
        }

        mockMvc.perform(get("/api/v1/users?page=0&size=3")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(3));
    }
}










