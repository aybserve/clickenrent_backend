package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.PartModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PartModel entity.
 */
@Repository
public interface PartModelRepository extends JpaRepository<PartModel, Long> {
    Optional<PartModel> findByExternalId(String externalId);
}
