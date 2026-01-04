package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSaleOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSaleOrderItem entity.
 */
@Repository
public interface B2BSaleOrderItemRepository extends JpaRepository<B2BSaleOrderItem, Long> {
    Optional<B2BSaleOrderItem> findByExternalId(String externalId);
    List<B2BSaleOrderItem> findByB2bSaleOrderId(Long b2bSaleOrderId);
}

