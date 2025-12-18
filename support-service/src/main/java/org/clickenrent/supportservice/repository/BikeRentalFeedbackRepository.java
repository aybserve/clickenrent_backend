package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeRentalFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeRentalFeedback entity.
 */
@Repository
public interface BikeRentalFeedbackRepository extends JpaRepository<BikeRentalFeedback, Long> {
    
    List<BikeRentalFeedback> findByUserId(Long userId);
    
    Optional<BikeRentalFeedback> findByBikeRentalId(Long bikeRentalId);
    
    List<BikeRentalFeedback> findByRate(Integer rate);
}

