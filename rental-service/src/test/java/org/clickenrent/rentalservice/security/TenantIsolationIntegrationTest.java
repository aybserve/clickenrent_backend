package org.clickenrent.rentalservice.security;

import org.clickenrent.contracts.security.TenantContext;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.repository.RentalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for multi-tenant isolation.
 * Verifies that Hibernate filters correctly isolate data by company.
 */
@SpringBootTest
@Sql("/test-data-tenant-isolation.sql")
@Transactional
public class TenantIsolationIntegrationTest {
    
    @Autowired
    private RentalRepository rentalRepository;
    
    @BeforeEach
    void setUp() {
        // Clear tenant context before each test
        TenantContext.clear();
    }
    
    @AfterEach
    void tearDown() {
        // Clear tenant context after each test
        TenantContext.clear();
    }
    
    @Test
    void b2bUserShouldOnlySeeTheirCompanyData() {
        // Given: B2B user from Nike company
        TenantContext.setSuperAdmin(false);
        TenantContext.setCurrentCompanies(List.of("nike-uuid"));
        
        // When: User queries all rentals
        List<Rental> rentals = rentalRepository.findAll();
        
        // Then: Should only see Nike rentals (3)
        assertThat(rentals).hasSize(3);
        assertThat(rentals).allMatch(r -> r.getCompanyExternalId().equals("nike-uuid"));
    }
    
    @Test
    void findByIdShouldReturnNullForOtherCompanyData() {
        // Given: B2B user from Nike company
        TenantContext.setSuperAdmin(false);
        TenantContext.setCurrentCompanies(List.of("nike-uuid"));
        
        // When: User tries to access Adidas rental by ID
        Long adidasRentalId = 100L;
        Optional<Rental> rental = rentalRepository.findById(adidasRentalId);
        
        // Then: Should not find it because it belongs to Adidas
        assertThat(rental).isEmpty();
    }
    
    @Test
    void adminShouldSeeAllData() {
        // Given: Superadmin user
        TenantContext.setSuperAdmin(true);
        TenantContext.setCurrentCompanies(List.of());
        
        // When: Admin queries all rentals
        List<Rental> rentals = rentalRepository.findAll();
        
        // Then: Should see all rentals (5 total: 3 Nike + 2 Adidas)
        assertThat(rentals).hasSize(5);
    }
    
    @Test
    void multiCompanyUserShouldSeeUnionOfData() {
        // Given: B2B user who belongs to both Nike and Adidas
        TenantContext.setSuperAdmin(false);
        TenantContext.setCurrentCompanies(List.of("nike-uuid", "adidas-uuid"));
        
        // When: User queries all rentals
        List<Rental> rentals = rentalRepository.findAll();
        
        // Then: Should see both companies' rentals (5 total)
        assertThat(rentals).hasSize(5);
    }
    
    @Test
    void userWithNoCompaniesShouldSeeNoData() {
        // Given: Customer user with no companies
        TenantContext.setSuperAdmin(false);
        TenantContext.setCurrentCompanies(List.of());
        
        // When: User queries all rentals
        List<Rental> rentals = rentalRepository.findAll();
        
        // Then: Should see no rentals (customers access data differently)
        assertThat(rentals).isEmpty();
    }
    
    @Test
    void filterShouldWorkWithSpecificQueries() {
        // Given: B2B user from Adidas company
        TenantContext.setSuperAdmin(false);
        TenantContext.setCurrentCompanies(List.of("adidas-uuid"));
        
        // When: User queries by external ID
        Optional<Rental> rental = rentalRepository.findByExternalId("rental-nike-1");
        
        // Then: Should not find Nike rental
        assertThat(rental).isEmpty();
        
        // When: User queries their own company's rental
        Optional<Rental> adidasRental = rentalRepository.findByExternalId("rental-adidas-1");
        
        // Then: Should find it
        assertThat(adidasRental).isPresent();
        assertThat(adidasRental.get().getCompanyExternalId()).isEqualTo("adidas-uuid");
    }
}
