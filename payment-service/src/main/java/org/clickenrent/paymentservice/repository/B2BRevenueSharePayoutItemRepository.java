package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.B2BRevenueSharePayoutItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for B2BRevenueSharePayoutItem entity
 */
@Repository
public interface B2BRevenueSharePayoutItemRepository extends JpaRepository<B2BRevenueSharePayoutItem, Long> {
    
    List<B2BRevenueSharePayoutItem> findByB2bRevenueSharePayoutId(Long payoutId);
    
    Optional<B2BRevenueSharePayoutItem> findByBikeRentalId(Long bikeRentalId);
    
    Optional<B2BRevenueSharePayoutItem> findByExternalId(String externalId);
}


