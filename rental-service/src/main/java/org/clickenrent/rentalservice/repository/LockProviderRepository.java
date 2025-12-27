package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.LockProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LockProviderRepository extends JpaRepository<LockProvider, Long> {
    Optional<LockProvider> findByName(String name);
}







