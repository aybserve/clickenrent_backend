package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionItemDTO;
import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.entity.B2BSubscriptionItem;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionItemMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionItemRepository;
import org.clickenrent.rentalservice.repository.B2BSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSubscriptionItemService {

    private final B2BSubscriptionItemRepository b2bSubscriptionItemRepository;
    private final B2BSubscriptionRepository b2bSubscriptionRepository;
    private final B2BSubscriptionItemMapper b2bSubscriptionItemMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<B2BSubscriptionItemDTO> getItemsBySubscription(Long subscriptionId) {
        B2BSubscription subscription = b2bSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscription", "id", subscriptionId));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(subscription.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view items for this subscription");
        }

        return b2bSubscriptionItemRepository.findByB2bSubscription(subscription).stream()
                .map(b2bSubscriptionItemMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionItemDTO getItemById(Long id) {
        B2BSubscriptionItem item = b2bSubscriptionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionItem", "id", id));
        return b2bSubscriptionItemMapper.toDto(item);
    }

    @Transactional
    public B2BSubscriptionItemDTO createItem(B2BSubscriptionItemDTO dto) {
        B2BSubscription subscription = b2bSubscriptionRepository.findById(dto.getB2bSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscription", "id", dto.getB2bSubscriptionId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(subscription.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to add items to this subscription");
        }

        B2BSubscriptionItem item = b2bSubscriptionItemMapper.toEntity(dto);
        item.sanitizeForCreate();
        item = b2bSubscriptionItemRepository.save(item);
        return b2bSubscriptionItemMapper.toDto(item);
    }

    @Transactional
    public B2BSubscriptionItemDTO updateItem(Long id, B2BSubscriptionItemDTO dto) {
        B2BSubscriptionItem item = b2bSubscriptionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionItem", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(item.getB2bSubscription().getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this item");
        }

        b2bSubscriptionItemMapper.updateEntityFromDto(dto, item);
        item = b2bSubscriptionItemRepository.save(item);
        return b2bSubscriptionItemMapper.toDto(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete subscription items");
        }

        B2BSubscriptionItem item = b2bSubscriptionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionItem", "id", id));
        b2bSubscriptionItemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionItemDTO getB2BSubscriptionItemByExternalId(String externalId) {
        B2BSubscriptionItem item = b2bSubscriptionItemRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionItem", "externalId", externalId));
        return b2bSubscriptionItemMapper.toDto(item);
    }
}
