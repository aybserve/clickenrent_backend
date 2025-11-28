package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.*;
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
 * Repository tests for UserCompanyRepository.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserCompanyRepositoryTest {

    @Autowired
    private UserCompanyRepository userCompanyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyRoleRepository companyRoleRepository;

    private User user1;
    private User user2;
    private Company company1;
    private Company company2;
    private CompanyRole ownerRole;
    private CompanyRole staffRole;
    private UserCompany userCompany1;
    private UserCompany userCompany2;

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

        // Create companies
        company1 = Company.builder()
                .externalId(UUID.randomUUID().toString())
                .name("Company 1")
                .build();

        company2 = Company.builder()
                .externalId(UUID.randomUUID().toString())
                .name("Company 2")
                .build();

        companyRepository.save(company1);
        companyRepository.save(company2);

        // Create roles
        ownerRole = CompanyRole.builder().name("Owner").build();
        staffRole = CompanyRole.builder().name("Staff").build();
        companyRoleRepository.save(ownerRole);
        companyRoleRepository.save(staffRole);

        // Create user-company associations
        userCompany1 = UserCompany.builder()
                .user(user1)
                .company(company1)
                .companyRole(ownerRole)
                .build();

        userCompany2 = UserCompany.builder()
                .user(user1)
                .company(company2)
                .companyRole(staffRole)
                .build();

        userCompanyRepository.save(userCompany1);
        userCompanyRepository.save(userCompany2);
    }

    @AfterEach
    void tearDown() {
        userCompanyRepository.deleteAll();
        userRepository.deleteAll();
        companyRepository.deleteAll();
        companyRoleRepository.deleteAll();
    }

    @Test
    void findByUser_UserWithCompanies_ReturnsCompanies() {
        // When
        List<UserCompany> userCompanies = userCompanyRepository.findByUser(user1);

        // Then
        assertThat(userCompanies).hasSize(2);
        assertThat(userCompanies).extracting(uc -> uc.getCompany().getName())
                .containsExactlyInAnyOrder("Company 1", "Company 2");
    }

    @Test
    void findByUser_UserWithoutCompanies_ReturnsEmpty() {
        // When
        List<UserCompany> userCompanies = userCompanyRepository.findByUser(user2);

        // Then
        assertThat(userCompanies).isEmpty();
    }

    @Test
    void findByUserId_UserWithCompanies_ReturnsCompanies() {
        // When
        List<UserCompany> userCompanies = userCompanyRepository.findByUserId(user1.getId());

        // Then
        assertThat(userCompanies).hasSize(2);
        assertThat(userCompanies).extracting(uc -> uc.getCompanyRole().getName())
                .containsExactlyInAnyOrder("Owner", "Staff");
    }

    @Test
    void findByUserId_UserWithoutCompanies_ReturnsEmpty() {
        // When
        List<UserCompany> userCompanies = userCompanyRepository.findByUserId(user2.getId());

        // Then
        assertThat(userCompanies).isEmpty();
    }

    @Test
    void findByUserId_NonExistentUser_ReturnsEmpty() {
        // When
        List<UserCompany> userCompanies = userCompanyRepository.findByUserId(99999L);

        // Then
        assertThat(userCompanies).isEmpty();
    }

    @Test
    void save_NewUserCompany_Success() {
        // Given
        UserCompany newUserCompany = UserCompany.builder()
                .user(user2)
                .company(company1)
                .companyRole(staffRole)
                .build();

        // When
        UserCompany saved = userCompanyRepository.save(newUserCompany);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user2.getId());
        assertThat(saved.getCompany().getId()).isEqualTo(company1.getId());
        assertThat(saved.getCompanyRole().getName()).isEqualTo("Staff");
    }

    @Test
    void delete_ExistingUserCompany_Success() {
        // Given
        Long userCompanyId = userCompany1.getId();

        // When
        userCompanyRepository.delete(userCompany1);

        // Then
        assertThat(userCompanyRepository.findById(userCompanyId)).isEmpty();
        assertThat(userCompanyRepository.findByUser(user1)).hasSize(1);
    }

    @Test
    void findByUser_ChecksRoleAssociation_Success() {
        // When
        List<UserCompany> userCompanies = userCompanyRepository.findByUser(user1);

        // Then
        assertThat(userCompanies).hasSize(2);
        
        UserCompany ownerAssociation = userCompanies.stream()
                .filter(uc -> uc.getCompany().getName().equals("Company 1"))
                .findFirst()
                .orElseThrow();
        
        assertThat(ownerAssociation.getCompanyRole().getName()).isEqualTo("Owner");
    }
}

