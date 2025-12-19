package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionDTO;
import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSubscriptionService {

    private final B2BSubscriptionRepository b2bSubscriptionRepository;
    private final B2BSubscriptionMapper b2bSubscriptionMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSubscriptionDTO> getAllSubscriptions(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSubscriptionRepository.findAll(pageable)
                    .map(b2bSubscriptionMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all subscriptions");
    }

    @Transactional(readOnly = true)
    public List<B2BSubscriptionDTO> getSubscriptionsByLocation(Long locationId) {
        // Access control - admin only
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view subscriptions for this location");
        }

        return b2bSubscriptionRepository.findByLocationId(locationId).stream()
                .map(b2bSubscriptionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionDTO getSubscriptionById(Long id) {
        B2BSubscription subscription = b2bSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscription", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this subscription");
        }

        return b2bSubscriptionMapper.toDto(subscription);
    }

    @Transactional
    public B2BSubscriptionDTO createSubscription(B2BSubscriptionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create subscriptions");
        }

        B2BSubscription subscription = b2bSubscriptionMapper.toEntity(dto);
        subscription = b2bSubscriptionRepository.save(subscription);
        return b2bSubscriptionMapper.toDto(subscription);
    }

    @Transactional
    public B2BSubscriptionDTO updateSubscription(Long id, B2BSubscriptionDTO dto) {
        B2BSubscription subscription = b2bSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscription", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this subscription");
        }

        b2bSubscriptionMapper.updateEntityFromDto(dto, subscription);
        subscription = b2bSubscriptionRepository.save(subscription);
        return b2bSubscriptionMapper.toDto(subscription);
    }

    @Transactional
    public void deleteSubscription(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete subscriptions");
        }

        B2BSubscription subscription = b2bSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscription", "id", id));
        b2bSubscriptionRepository.delete(subscription);
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionDTO findByExternalId(String externalId) {
        B2BSubscription subscription = b2bSubscriptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscription", "externalId", externalId));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this subscription");
        }

        return b2bSubscriptionMapper.toDto(subscription);
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return b2bSubscriptionRepository.existsByExternalId(externalId);
    }
}
