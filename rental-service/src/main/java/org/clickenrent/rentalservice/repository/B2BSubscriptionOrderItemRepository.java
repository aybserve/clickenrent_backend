package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSubscriptionOrderItem entity.
 */
@Repository
public interface B2BSubscriptionOrderItemRepository extends JpaRepository<B2BSubscriptionOrderItem, Long> {
    Optional<B2BSubscriptionOrderItem> findByExternalId(String externalId);
    List<B2BSubscriptionOrderItem> findByB2bSubscriptionOrderId(Long b2bSubscriptionOrderId);
}







