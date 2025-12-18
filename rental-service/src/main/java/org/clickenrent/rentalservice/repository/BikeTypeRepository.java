package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for BikeType entity.
 */
@Repository
public interface BikeTypeRepository extends JpaRepository<BikeType, Long> {
    Optional<BikeType> findByName(String name);
}


