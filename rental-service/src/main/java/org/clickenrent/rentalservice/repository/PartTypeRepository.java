package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.PartType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PartType entity.
 */
@Repository
public interface PartTypeRepository extends JpaRepository<PartType, Long> {
    Optional<PartType> findByName(String name);
}
