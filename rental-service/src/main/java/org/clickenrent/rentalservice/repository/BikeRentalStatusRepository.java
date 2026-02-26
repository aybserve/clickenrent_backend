package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeRentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for BikeRentalStatus entity.
 */
@Repository
public interface BikeRentalStatusRepository extends JpaRepository<BikeRentalStatus, Long> {
    Optional<BikeRentalStatus> findByName(String name);
    Optional<BikeRentalStatus> findByExternalId(String externalId);
}








