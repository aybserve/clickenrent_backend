package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for BikeStatus entity.
 */
@Repository
public interface BikeStatusRepository extends JpaRepository<BikeStatus, Long> {
    Optional<BikeStatus> findByName(String name);
}







