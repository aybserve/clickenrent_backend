package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Country entity.
 * Provides standard CRUD operations for managing countries.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    
    Optional<Country> findByName(String name);
    
    Optional<Country> findByExternalId(String externalId);
}









