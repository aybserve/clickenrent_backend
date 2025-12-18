package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSubscriptionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for B2BSubscriptionOrder entity.
 */
@Repository
public interface B2BSubscriptionOrderRepository extends JpaRepository<B2BSubscriptionOrder, Long> {
    Optional<B2BSubscriptionOrder> findByExternalId(String externalId);
}

