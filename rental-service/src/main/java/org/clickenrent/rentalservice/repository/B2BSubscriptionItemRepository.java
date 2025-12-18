package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.entity.B2BSubscriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSubscriptionItem entity.
 */
@Repository
public interface B2BSubscriptionItemRepository extends JpaRepository<B2BSubscriptionItem, Long> {
    Optional<B2BSubscriptionItem> findByExternalId(String externalId);
    List<B2BSubscriptionItem> findByB2bSubscription(B2BSubscription b2bSubscription);
    List<B2BSubscriptionItem> findByProductId(Long productId);
}

