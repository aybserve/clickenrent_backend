package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ChargingStation entity.
 */
@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    Optional<ChargingStation> findByExternalId(String externalId);
}
