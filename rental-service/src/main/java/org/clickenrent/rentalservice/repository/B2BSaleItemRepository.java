package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.entity.B2BSaleItem;
import org.clickenrent.rentalservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSaleItem entity.
 */
@Repository
public interface B2BSaleItemRepository extends JpaRepository<B2BSaleItem, Long> {
    Optional<B2BSaleItem> findByExternalId(String externalId);
    List<B2BSaleItem> findByB2bSale(B2BSale b2bSale);
    List<B2BSaleItem> findByProduct(Product product);
    List<B2BSaleItem> findByProductId(Long productId);
}

