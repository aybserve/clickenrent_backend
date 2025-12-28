package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.HubImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for HubImage entity.
 */
@Repository
public interface HubImageRepository extends JpaRepository<HubImage, Long> {
    Optional<HubImage> findByExternalId(String externalId);
    List<HubImage> findByHub(Hub hub);
}








