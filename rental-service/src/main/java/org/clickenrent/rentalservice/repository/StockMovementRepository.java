package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StockMovement entity.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Optional<StockMovement> findByExternalId(String externalId);
    List<StockMovement> findByProductId(Long productId);
}




