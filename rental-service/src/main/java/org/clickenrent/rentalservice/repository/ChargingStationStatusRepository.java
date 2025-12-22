package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.ChargingStationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ChargingStationStatus entity.
 */
@Repository
public interface ChargingStationStatusRepository extends JpaRepository<ChargingStationStatus, Long> {
    Optional<ChargingStationStatus> findByName(String name);
}




