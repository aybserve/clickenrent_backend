package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.RentalUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for RentalUnit entity.
 */
@Repository
public interface RentalUnitRepository extends JpaRepository<RentalUnit, Long> {
    Optional<RentalUnit> findByName(String name);
}




