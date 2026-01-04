package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Coordinates entity.
 */
@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
    Optional<Coordinates> findByExternalId(String externalId);
}








