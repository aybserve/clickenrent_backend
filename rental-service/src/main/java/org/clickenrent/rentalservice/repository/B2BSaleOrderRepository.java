package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSaleOrder entity.
 */
@Repository
public interface B2BSaleOrderRepository extends JpaRepository<B2BSaleOrder, Long> {
    Optional<B2BSaleOrder> findByExternalId(String externalId);
    List<B2BSaleOrder> findBySellerCompanyExternalId(String sellerCompanyExternalId);
    List<B2BSaleOrder> findByBuyerCompanyExternalId(String buyerCompanyExternalId);
}




