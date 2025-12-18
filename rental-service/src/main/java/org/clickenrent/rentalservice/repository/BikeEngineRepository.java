package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeEngine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for BikeEngine entity.
 */
@Repository
public interface BikeEngineRepository extends JpaRepository<BikeEngine, Long> {
    Optional<BikeEngine> findByExternalId(String externalId);
}


