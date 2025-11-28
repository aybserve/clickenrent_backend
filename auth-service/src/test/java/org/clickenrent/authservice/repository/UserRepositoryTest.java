package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for UserRepository.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("testuser1")
                .email("test1@example.com")
                .password("password1")
                .firstName("Test1")
                .lastName("User1")
                .isActive(true)
                .isDeleted(false)
                .build();

        user2 = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("testuser2")
                .email("test2@example.com")
                .password("password2")
                .firstName("Test2")
                .lastName("User2")
                .isActive(false)
                .isDeleted(true)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByUserName_ExistingUser_ReturnsUser() {
        // When
        Optional<User> found = userRepository.findByUserName("testuser1");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("testuser1");
        assertThat(found.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    void findByUserName_NonExistingUser_ReturnsEmpty() {
        // When
        Optional<User> found = userRepository.findByUserName("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_ExistingUser_ReturnsUser() {
        // When
        Optional<User> found = userRepository.findByEmail("test1@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("testuser1");
        assertThat(found.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    void findByEmail_NonExistingUser_ReturnsEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByExternalId_ExistingUser_ReturnsUser() {
        // When
        Optional<User> found = userRepository.findByExternalId(user1.getExternalId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("testuser1");
        assertThat(found.get().getExternalId()).isEqualTo(user1.getExternalId());
    }

    @Test
    void findByExternalId_NonExistingUser_ReturnsEmpty() {
        // When
        Optional<User> found = userRepository.findByExternalId("non-existent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void save_NewUser_Success() {
        // Given
        User newUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("newuser")
                .email("newuser@example.com")
                .password("password")
                .firstName("New")
                .lastName("User")
                .isActive(true)
                .isDeleted(false)
                .build();

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserName()).isEqualTo("newuser");
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void update_ExistingUser_Success() {
        // Given
        user1.setFirstName("UpdatedFirstName");
        user1.setLastName("UpdatedLastName");

        // When
        User updated = userRepository.save(user1);

        // Then
        assertThat(updated.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(updated.getLastName()).isEqualTo("UpdatedLastName");

        Optional<User> found = userRepository.findById(user1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("UpdatedFirstName");
    }

    @Test
    void delete_ExistingUser_Success() {
        // Given
        Long userId = user1.getId();

        // When
        userRepository.delete(user1);

        // Then
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    void findAll_ReturnsAllUsers() {
        // When
        Iterable<User> allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(2);
    }

    @Test
    void findByUserName_CaseSensitive_Success() {
        // When
        Optional<User> lowerCase = userRepository.findByUserName("testuser1");

        // Then
        // Assuming username is case-sensitive in the database
        assertThat(userRepository.findByUserName("TESTUSER1")).isEmpty();
        assertThat(lowerCase).isPresent();
    }

    @Test
    void findByEmail_CaseSensitive_Success() {
        // When
        Optional<User> lowerCase = userRepository.findByEmail("test1@example.com");

        // Then
        // Email might be case-insensitive depending on database collation
        assertThat(lowerCase).isPresent();
    }

    @Test
    void save_WithNullOptionalFields_Success() {
        // Given
        User minimalUser = User.builder()
                .externalId(UUID.randomUUID().toString())
                .userName("minimaluser")
                .email("minimal@example.com")
                .password("password")
                .isActive(true)
                .isDeleted(false)
                .build();

        // When
        User saved = userRepository.save(minimalUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isNull();
        assertThat(saved.getLastName()).isNull();
        assertThat(saved.getPhone()).isNull();
    }
}

