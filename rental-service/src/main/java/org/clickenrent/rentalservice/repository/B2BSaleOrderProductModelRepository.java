package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSaleOrderProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSaleOrderProductModel entity.
 */
@Repository
public interface B2BSaleOrderProductModelRepository extends JpaRepository<B2BSaleOrderProductModel, Long> {
    Optional<B2BSaleOrderProductModel> findByExternalId(String externalId);
    List<B2BSaleOrderProductModel> findByB2bSaleOrderId(Long b2bSaleOrderId);
}


