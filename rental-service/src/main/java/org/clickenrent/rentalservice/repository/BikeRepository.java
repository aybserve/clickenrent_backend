package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Bike entity.
 */
@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    Optional<Bike> findByExternalId(String externalId);
    Optional<Bike> findByCode(String code);
}

