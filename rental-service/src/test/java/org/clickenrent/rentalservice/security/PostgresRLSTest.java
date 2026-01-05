package org.clickenrent.rentalservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database-level tests for PostgreSQL Row Level Security.
 * These tests verify that RLS policies correctly filter data at the database level,
 * even when bypassing Hibernate (using raw SQL).
 */
@SpringBootTest
@Sql("/test-data-tenant-isolation.sql")
public class PostgresRLSTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    void rlsShouldFilterByCompanyId() {
        // Set session variables to simulate Nike user
        jdbcTemplate.execute("SET app.is_superadmin = false");
        jdbcTemplate.execute("SET app.company_external_ids = 'nike-uuid'");
        
        // Direct SQL query (bypasses Hibernate)
        List<Map<String, Object>> rentals = jdbcTemplate.queryForList(
            "SELECT * FROM rental WHERE is_deleted = false"
        );
        
        // Should only see Nike rentals (3)
        assertThat(rentals).hasSize(3);
        assertThat(rentals).allMatch(r -> 
            r.get("company_external_id").equals("nike-uuid")
        );
    }
    
    @Test
    void rlsShouldAllowAdminAccessToAll() {
        // Set session variables to simulate admin
        jdbcTemplate.execute("SET app.is_superadmin = true");
        jdbcTemplate.execute("SET app.company_external_ids = ''");
        
        List<Map<String, Object>> rentals = jdbcTemplate.queryForList(
            "SELECT * FROM rental WHERE is_deleted = false"
        );
        
        // Admin sees all rentals (5 total: 3 Nike + 2 Adidas)
        assertThat(rentals).hasSize(5);
    }
    
    @Test
    void rlsShouldSupportMultipleCompanies() {
        // User belongs to both companies
        jdbcTemplate.execute("SET app.is_superadmin = false");
        jdbcTemplate.execute("SET app.company_external_ids = 'nike-uuid,adidas-uuid'");
        
        List<Map<String, Object>> rentals = jdbcTemplate.queryForList(
            "SELECT * FROM rental WHERE is_deleted = false"
        );
        
        // Sees both companies' rentals (5 total)
        assertThat(rentals).hasSize(5);
    }
    
    @Test
    void rlsShouldBlockAccessWithNoCompanies() {
        // User with no companies (customer)
        jdbcTemplate.execute("SET app.is_superadmin = false");
        jdbcTemplate.execute("SET app.company_external_ids = ''");
        
        List<Map<String, Object>> rentals = jdbcTemplate.queryForList(
            "SELECT * FROM rental WHERE is_deleted = false"
        );
        
        // Should see no rentals
        assertThat(rentals).isEmpty();
    }
    
    @Test
    void rlsShouldBlockDirectAccessToOtherCompanyData() {
        // Set session variables to simulate Nike user
        jdbcTemplate.execute("SET app.is_superadmin = false");
        jdbcTemplate.execute("SET app.company_external_ids = 'nike-uuid'");
        
        // Try to access specific Adidas rental by ID
        List<Map<String, Object>> rentals = jdbcTemplate.queryForList(
            "SELECT * FROM rental WHERE id = 100 AND is_deleted = false"
        );
        
        // Should not see Adidas rental (ID 100)
        assertThat(rentals).isEmpty();
    }
    
    @Test
    void rlsShouldWorkWithJoins() {
        // Set session variables to simulate Adidas user
        jdbcTemplate.execute("SET app.is_superadmin = false");
        jdbcTemplate.execute("SET app.company_external_ids = 'adidas-uuid'");
        
        // Query with join (RLS should still apply)
        List<Map<String, Object>> rentals = jdbcTemplate.queryForList(
            "SELECT r.* FROM rental r " +
            "INNER JOIN rental_status rs ON r.rental_status_id = rs.id " +
            "WHERE r.is_deleted = false"
        );
        
        // Should only see Adidas rentals (2)
        assertThat(rentals).hasSize(2);
        assertThat(rentals).allMatch(r -> 
            r.get("company_external_id").equals("adidas-uuid")
        );
    }
}
