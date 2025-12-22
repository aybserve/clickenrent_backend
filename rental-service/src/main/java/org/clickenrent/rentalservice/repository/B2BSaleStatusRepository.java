package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for B2BSaleStatus entity.
 */
@Repository
public interface B2BSaleStatusRepository extends JpaRepository<B2BSaleStatus, Long> {
    Optional<B2BSaleStatus> findByName(String name);
}




