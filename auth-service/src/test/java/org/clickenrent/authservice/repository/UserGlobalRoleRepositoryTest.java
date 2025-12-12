package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for UserGlobalRoleRepository.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserGlobalRoleRepositoryTest {

    @Autowired
    private UserGlobalRoleRepository userGlobalRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalRoleRepository globalRoleRepository;

    private User user1;
    private User user2;
    private GlobalRole adminRole;
    private GlobalRole userRole;
    private UserGlobalRole userGlobalRole1;
    private UserGlobalRole userGlobalRole2;

    @BeforeEach
    void setUp() {
        // Create users
        user1 = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("user1")
                .email("user1@example.com")
                .password("password")
                .isActive(true)
                .isDeleted(false)
                .build();

        user2 = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("user2")
                .email("user2@example.com")
                .password("password")
                .isActive(true)
                .isDeleted(false)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // Create roles
        adminRole = GlobalRole.builder().name("Admin").build();
        userRole = GlobalRole.builder().name("User").build();
        globalRoleRepository.save(adminRole);
        globalRoleRepository.save(userRole);

        // Assign roles
        userGlobalRole1 = UserGlobalRole.builder()
                .user(user1)
                .globalRole(adminRole)
                .build();

        userGlobalRole2 = UserGlobalRole.builder()
                .user(user1)
                .globalRole(userRole)
                .build();

        userGlobalRoleRepository.save(userGlobalRole1);
        userGlobalRoleRepository.save(userGlobalRole2);
    }

    @AfterEach
    void tearDown() {
        userGlobalRoleRepository.deleteAll();
        userRepository.deleteAll();
        globalRoleRepository.deleteAll();
    }

    @Test
    void findByUser_UserWithRoles_ReturnsRoles() {
        // When
        List<UserGlobalRole> roles = userGlobalRoleRepository.findByUser(user1);

        // Then
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting(ugr -> ugr.getGlobalRole().getName())
                .containsExactlyInAnyOrder("Admin", "User");
    }

    @Test
    void findByUser_UserWithoutRoles_ReturnsEmpty() {
        // When
        List<UserGlobalRole> roles = userGlobalRoleRepository.findByUser(user2);

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    void findByUserId_UserWithRoles_ReturnsRoles() {
        // When
        List<UserGlobalRole> roles = userGlobalRoleRepository.findByUserId(user1.getId());

        // Then
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting(ugr -> ugr.getGlobalRole().getName())
                .containsExactlyInAnyOrder("Admin", "User");
    }

    @Test
    void findByUserId_UserWithoutRoles_ReturnsEmpty() {
        // When
        List<UserGlobalRole> roles = userGlobalRoleRepository.findByUserId(user2.getId());

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    void findByUserId_NonExistentUser_ReturnsEmpty() {
        // When
        List<UserGlobalRole> roles = userGlobalRoleRepository.findByUserId(99999L);

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    void save_NewUserGlobalRole_Success() {
        // Given
        UserGlobalRole newRole = UserGlobalRole.builder()
                .user(user2)
                .globalRole(userRole)
                .build();

        // When
        UserGlobalRole saved = userGlobalRoleRepository.save(newRole);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user2.getId());
        assertThat(saved.getGlobalRole().getId()).isEqualTo(userRole.getId());
    }

    @Test
    void delete_ExistingUserGlobalRole_Success() {
        // Given
        Long roleId = userGlobalRole1.getId();

        // When
        userGlobalRoleRepository.delete(userGlobalRole1);

        // Then
        assertThat(userGlobalRoleRepository.findById(roleId)).isEmpty();
        assertThat(userGlobalRoleRepository.findByUser(user1)).hasSize(1);
    }
}


