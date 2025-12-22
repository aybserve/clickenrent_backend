package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BatteryChargeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for BatteryChargeStatus entity.
 */
@Repository
public interface BatteryChargeStatusRepository extends JpaRepository<BatteryChargeStatus, Long> {
    Optional<BatteryChargeStatus> findByName(String name);
}




