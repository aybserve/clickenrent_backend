package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for City entity.
 * Provides standard CRUD operations for managing cities.
 */
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    
    List<City> findByCountryId(Long countryId);
}

