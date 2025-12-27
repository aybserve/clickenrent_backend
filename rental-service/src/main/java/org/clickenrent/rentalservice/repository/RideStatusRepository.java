package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for RideStatus entity.
 */
@Repository
public interface RideStatusRepository extends JpaRepository<RideStatus, Long> {
    Optional<RideStatus> findByName(String name);
}







