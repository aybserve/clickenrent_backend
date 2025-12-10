package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSale entity.
 */
@Repository
public interface B2BSaleRepository extends JpaRepository<B2BSale, Long> {
    Optional<B2BSale> findByExternalId(String externalId);
    List<B2BSale> findByLocationId(Long locationId);
}
