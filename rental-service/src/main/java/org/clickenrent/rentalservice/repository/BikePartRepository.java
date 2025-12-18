package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BikePart entity.
 */
@Repository
public interface BikePartRepository extends JpaRepository<BikePart, Long> {
    List<BikePart> findByBikeId(Long bikeId);
    List<BikePart> findByPartId(Long partId);
}

