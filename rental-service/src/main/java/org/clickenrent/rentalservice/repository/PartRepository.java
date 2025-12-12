package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Part entity.
 */
@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    Optional<Part> findByExternalId(String externalId);
}
