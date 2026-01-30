package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BikeRental entity.
 */
@Repository
public interface BikeRentalRepository extends JpaRepository<BikeRental, Long> {
    Optional<BikeRental> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    List<BikeRental> findByRental(Rental rental);
    
    // Date filtering methods
    Page<BikeRental> findByStartDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    Page<BikeRental> findByStartDateTimeAfter(LocalDateTime startDateTime, Pageable pageable);
    Page<BikeRental> findByStartDateTimeBefore(LocalDateTime endDateTime, Pageable pageable);
}






