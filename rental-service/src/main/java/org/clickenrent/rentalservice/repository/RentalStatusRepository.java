package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for RentalStatus entity.
 */
@Repository
public interface RentalStatusRepository extends JpaRepository<RentalStatus, Long> {
    Optional<RentalStatus> findByName(String name);
}








