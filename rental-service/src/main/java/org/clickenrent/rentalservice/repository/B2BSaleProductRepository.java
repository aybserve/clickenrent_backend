package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.entity.B2BSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSaleProduct entity.
 */
@Repository
public interface B2BSaleProductRepository extends JpaRepository<B2BSaleProduct, Long> {
    Optional<B2BSaleProduct> findByExternalId(String externalId);
    List<B2BSaleProduct> findByB2bSale(B2BSale b2bSale);
    List<B2BSaleProduct> findByProductId(Long productId);
}






